package com.blockchain.blockChainApp.Services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class Validation {

  static long TEAM_ID = 1097844002039L;
  private HashMap<Long, Long> balances;
  public Validation() {
    balances = new HashMap<>();
  }

  /**
   * @param block: JSONObject of the block
   * @param expectedId: int of the expected block id
   * @param previousTime: String of the previous block time
   * @param previousBlock: JSONObject of the previous block
   * @return: the time of the last transaction in the block, null if the block is invalid
   */
  public String validateBlock(JSONObject block, int expectedId, String previousTime, JSONObject previousBlock) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    if (!block.has("id") || block.getInt("id") != expectedId) {
      return null;
    }
    if(!block.has("pow") || !block.has("hash") || !block.has("target")) return null;
    String pow = block.getString("pow");
    String hash = block.getString("hash");
    String target = block.getString("target");

    if (!pow.matches("^\\d+$") ||
      !hash.matches("^[0-9a-fA-F]+$") ||
      !target.matches("^[0-9a-fA-F]+$")) {
      return null;
    }

    if(!block.has("all_tx")) return null;

    JSONArray allTx = block.getJSONArray("all_tx");
    if (allTx.length() == 0) return null;
    // check each transaction in all_tx is valid
    // Check transactions are also ordered in a monotonically increasing timestamp
    //  and must come later than any logged transactions.
    Long totalFee = 0L;
    int rewardTxCount = 0;
    for (int i = 0; i < allTx.length(); i++) {
      JSONObject tx = allTx.getJSONObject(i);
      // if reward transaction, false, because reward transaction should be at the end
      // TODO: Check there is exactly one reward transaction at the end of all_tx
      if(isRewardTransaction(tx)){
        rewardTxCount++;
        if(rewardTxCount > 1) return null;
        if(!validateTransaction(tx, false, true)) return null;
        // check if reward is correctly decreased - rewardTx.getInt("amt") == 500000000 / Math.pow(2, expectedId)
        BigInteger baseReward = BigInteger.valueOf(500000000L);
        BigInteger divisor = BigInteger.valueOf(2).pow(expectedId/2);
        BigInteger reward = baseReward.divide(divisor);
        if(tx.getLong("amt") != reward.longValue()) return null;
        // total fee distributed to miners
        balances.put(tx.getLong("recv"), balances.getOrDefault(tx.getLong("recv"), 0L) + totalFee);
      }else{
        if(!validateTransaction(tx, false, false)) return null;
        // accumulate total fee
        totalFee += tx.getLong("fee");
      }
      if(previousTime!=null && tx.getString("time").compareTo(previousTime) <= 0) return null;
      previousTime = tx.getString("time");
    }
    if(rewardTxCount != 1) return null;
    // check if block hash and pow is correct - ccHash(SHA("block_id|previous_block_hash|tx1_hash...") + PoW) < target
    byte[] shaHash = Helper.createSHAHash(BlockChain.inputOfBlockSHAHash(allTx, previousBlock));
    if(!BlockChain.getBlockHash(shaHash, pow).equals(hash)) return null;
    if (hash.compareTo(target) >= 0) return null;
    return previousTime;
  }
  /**
   * @param tx: JSONObject of the transaction
   * @param isNewTx: boolean of whether the transaction is a new transaction
   * @return: boolean of whether the transaction is valid
   */
  public boolean validateTransaction(JSONObject tx, boolean isNewTx, boolean isReward) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    // TODO: check if all fields are required, is fee required?
    String[] allTxFields = { "recv", "amt", "time", "hash", "send", "sig", "fee" };
    // if all transaction, check if it is a reward transaction
    if(!isNewTx){
      if(isReward){
        // Add balance to receiver
        balances.put(tx.getLong("recv"), balances.getOrDefault(tx.getLong("recv"), 0L) + tx.getLong("amt"));
        return true;
      }
      for (String field : allTxFields) {
        if (!tx.has(field)) return false;
      }
    }

    // if new transaction, check if it is a miner transaction
    if(isNewTx){
      // miner: recv, amt, time
      if(BlockChain.isMinerTransaction(tx)){
        // Add balance to receiver
        BlockChain.addFieldsToMinerTransaction(tx);
        balances.put(tx.getLong("recv"), balances.getOrDefault(tx.getLong("recv"), 0L) + tx.getLong("amt"));
        return true;
      }
      for (String field : allTxFields) {
        if (!tx.has(field)) return false;
      }
    }
    // TODO: at this point all transactions should have all required fields - desired?
    // check if amt is valid
    if(tx.has("amt") && tx.getLong("amt") < 0) return false;
//    if(tx.has("time") && !tx.getString("time").matches("^\\d{19}$")) return false;
    if(tx.has("fee") && tx.getLong("fee") < 0) return false;
    // if transaction has hash, verify it is a valid hash
    if (tx.has("hash")) {
      String hash = tx.getString("hash");
      if (!Helper.ccHash(BlockChain.inputOfTransactionHash(tx)).equals(hash)) {
        return false;
      }
    }
    if (tx.has("sig")) {
      Long sig = tx.getLong("sig");
      String hash = "";
      if(!tx.has("hash")) hash = Helper.ccHash(BlockChain.inputOfTransactionHash(tx));
      else hash = tx.getString("hash");
      //check signature for reward transaction
      if (Helper.decryptedRSA(sig, tx.getLong("send"), BlockChain.N) != Long.parseLong(hash, 16)) {
        return false;
      }
    }
    // TODO: check balance of sender is sufficient - create a ledger to record balance of each account
    long send = tx.getLong("send");
    long recv = tx.getLong("recv");
    long amt = tx.getLong("amt");
    long fee = tx.getLong("fee");
    balances.put(send, balances.getOrDefault(send, 0L) - amt - fee);
    if (balances.get(send) < 0) return false;
    // add balance to receiver
    balances.put(recv, balances.getOrDefault(recv, 0L) + amt);

    return true;
  }

  public boolean isRewardTransaction(JSONObject tx) {
    return tx.has("recv") && tx.has("amt") && tx.has("time") && tx.has("hash") &&
      !tx.has("send") && !tx.has("sig") && !tx.has("fee");
  }

}
