package com.blockchain.blockChainApp.Services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;


public class BlockChain {
  static long TEAM_ID = 1097844002039L;
  public static long N = 1561906343821L;
  public static long PRIVATE_KEY = Long.parseLong(System.getenv("PRIVATE_KEY"));
  public static long PUBLIC_KEY = Long.parseLong(System.getenv("PUBLIC_KEY"));
  private Validation validation;
  private JSONObject lastBlock;
  private String previousTime;
  public BlockChain() {
    validation = new Validation();
    lastBlock = createGenesisBlock();
    previousTime = null;
  }
  /**
  * @param request: String of the request
  * @return: JSONObject of the request
  */
  public static JSONObject getJsonObjectFromRequest(String request) {
    //decode string by url-safe base64
    byte[] decodedBytes = Base64.decodeBase64(request);
    try {
      // decompress the decoded bytes using zlib, it is a JSON object string
      String decompressed = Helper.decompressZlib(decodedBytes);
      JSONObject object = new JSONObject(decompressed);
      return object;
    } catch (Exception e) {
      return null;
    }
  }

  /**
  * @param responseObject: JSONObject of the response
  * @return: String of the response object
  */
  public static String responseString(JSONObject responseObject) throws IOException {
    String responseString = responseObject.toString();
    byte[] compressed = Helper.compressZlib(responseString);
    String encoded = java.util.Base64.getUrlEncoder().encodeToString(compressed);
    int paddingCount = (4 - (encoded.length() % 4)) % 4;
    if (paddingCount > 0){
      encoded += "=".repeat(paddingCount);
    }
    return encoded;
  }

  public String getResponseFromRequest(String request) throws IOException, NoSuchAlgorithmException {
    JSONObject requestObject = getJsonObjectFromRequest(request);
    if (requestObject == null) {
      return null;
    }
    JSONObject resultObject = resultObject(requestObject);
    if (resultObject == null) {
      return null;
    }
    return responseString(resultObject);
  }

  /**
  * @param transaction: JSONObject of a transaction
  * @return: String of the input of the transaction hash
  *
  * @note: create input for transaction hash: ccHash(SHA256(input))
  */
  public static String inputOfTransactionHash(JSONObject transaction) {
    String time = transaction.getString("time");
    String send = "";
    if(transaction.has("send")){
      send = String.valueOf(transaction.getLong("send"));
    }
    BigInteger recv = transaction.getBigInteger("recv");
    int amt = transaction.getInt("amt");
    String fee = "";
    if (transaction.has("fee")) {
      fee = String.valueOf(transaction.getInt("fee"));
    }
    return time + "|" + send + "|" + recv + "|" + amt + "|" + fee;
  }

  public static JSONObject getLatestTransaction(JSONArray transactions) {
    if (transactions.length() == 0) {
      return null;
    }
    return transactions.getJSONObject(transactions.length() - 1);
  }

  /**
  * @param transaction: JSONObject of the transaction
  * @return: void
  * @note: add fields to the latest transaction: fee, send, hash, sig
  */
  public static void addFieldsToMinerTransaction(JSONObject transaction)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    transaction.put("fee", 0);
    transaction.put("send", TEAM_ID);
    String transactionHash = Helper.ccHash(inputOfTransactionHash(transaction));
    transaction.put("hash", transactionHash);
    transaction.put("sig", createSignature(transactionHash));
  }

  /**
  * @param transaction: JSONObject of the transaction
  * @return: boolean of whether the transaction is a miner transaction
  */
  public static boolean isMinerTransaction(JSONObject transaction) {
    return transaction.has("time") && transaction.has("recv") && transaction.has("amt") &&
      !transaction.has("send") && !transaction.has("sig") && !transaction.has("hash") && !transaction.has("fee");
  }

  /**
  * @param new_transactions: JSONObject of the request
  * @return: JSONArray of all_tx for the new block yet to be created
  * @note: create all_tx for the new block, add required fields (fee, hash, sig, send) to the worker reward transaction
  * and add a new transaction to reward the worker for mining the block which sender is the team
  */
  private JSONArray createAllTxForNewBlock(JSONArray new_transactions, int expectedId)
    throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String prevTimeForTx = previousTime;
    for (int i = 0; i < new_transactions.length(); i++) {
      JSONObject tx = new_transactions.getJSONObject(i);
      // current transaction time should be greater than previous block time
      if(!validation.validateTransaction(tx, true, false)) return null;
      if(prevTimeForTx!=null && tx.getString("time").compareTo(prevTimeForTx) <= 0) return null;
      prevTimeForTx = tx.getString("time");
    }

    JSONObject workerReward = new JSONObject();
    Long newBlockTime = Long.parseLong(previousTime) + 600000000000L;
    workerReward.put("time", String.valueOf(newBlockTime));
    workerReward.put("recv", TEAM_ID);

//    BigInteger baseReward = BigInteger.valueOf(500000000L);
//    BigInteger numBlocks = BigInteger.valueOf(requestObject.getJSONArray("chain").length());
//    BigInteger divisor = BigInteger.valueOf(2).pow(numBlocks.divide(BigInteger.valueOf(2)).intValue());
//    BigInteger reward = baseReward.divide(divisor);
    workerReward.put("amt", rewardAmt(expectedId));
    workerReward.put("hash", Helper.ccHash(inputOfTransactionHash(workerReward)));

    new_transactions.put(workerReward);

    return new_transactions;
  }

  public static long rewardAmt(int expectedId) {
    BigInteger baseReward = BigInteger.valueOf(500000000L);
    BigInteger divisor = BigInteger.valueOf(2).pow( expectedId/2);
    BigInteger reward = baseReward.divide(divisor);
    return reward.longValue();
  }

  /**
  * @param transactionHash: String of the transaction hash
  * @return: Long of the signature
  */
  private static Long createSignature(String transactionHash) {
    return Helper.encryptedRSA(Long.parseLong(transactionHash, 16), PRIVATE_KEY, N);
  }

  public static JSONObject createGenesisBlock() {
    JSONObject genesis = new JSONObject();
    genesis.put("id", -1);
    genesis.put("hash", "00000000");
    genesis.put("all_tx", new JSONArray());
    return genesis;
  }

  /**
   * Get the last block from the chain
   * @param requestObject
   * @return
   */
  public static JSONObject getLastBlockFromChain(JSONObject requestObject) {
    JSONArray blocks_chain = requestObject.getJSONArray("chain");
    if (blocks_chain.length() == 0) {
      return createGenesisBlock();
    }
    return blocks_chain.getJSONObject(blocks_chain.length() - 1);
  }

  /**
  * @param new_transactions: JSONArray of all_tx for the new block yet to be created
  * @param previous_block: JSONObject of the previous block
  * @return: String of the input of the block hash
  */
  public static String inputOfBlockSHAHash(JSONArray new_transactions,
      JSONObject previous_block) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String result = "";
    result += (previous_block.getInt("id") + 1) + "|";
    result += previous_block.getString("hash");
    for (int i = 0; i < new_transactions.length(); i++) {
      String hash_tmp = "";
      try {
        hash_tmp = new_transactions.getJSONObject(i).getString("hash");
      } catch (Exception e) {
        // create hash for the transaction
        hash_tmp = Helper.ccHash(inputOfTransactionHash(new_transactions.getJSONObject(i)));
      }
      result += "|" + hash_tmp;
    }
    return result;
  }


  /**
  * @param shaHash: byte[] of the SHA-256 hash of the input
  * @param pow: String of the proof of work
  * @return: String of the block hash
  */
  public static String getBlockHash(byte[] shaHash, String pow)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    StringBuilder ShaHexString = new StringBuilder();
    for (byte b : shaHash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        ShaHexString.append('0');
      }
      ShaHexString.append(hex);
    }
    String ccInput = ShaHexString.toString() + pow;
    return Helper.ccHash(ccInput);
  }

  /**
  * @param requestObject: JSONObject of the request
  * @param shaHash: byte[] of the SHA-256 hash of the input
  * @return: String of the proof of work
  */
  public static String findPOW(JSONObject requestObject, byte[] shaHash)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String target = requestObject.getString("new_target");

    long pow = 0;
    while (true) {
      String ccHash = getBlockHash(shaHash, String.valueOf(pow));
      if (ccHash.compareTo(target) < 0) {
        break;
      }
      pow++;
    }

    return String.valueOf(pow);
  }

  /**
  * @param requestObject: JSONObject of the request
  * @return: JSONObject of the new block
  */
  public JSONObject createNewBlock(JSONObject requestObject) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    JSONObject newBlock = new JSONObject();
    int expectedId = lastBlock.getInt("id") + 1;
    newBlock.put("id", expectedId);
    newBlock.put("target", requestObject.getString("new_target"));

    JSONArray new_transactions = createAllTxForNewBlock(requestObject.getJSONArray("new_tx"), expectedId);
    if (new_transactions == null) {
      return null;
    }
    newBlock.put("all_tx", new_transactions);
    byte[] shaHash = Helper.createSHAHash(inputOfBlockSHAHash(new_transactions, lastBlock));
    String pow = findPOW(requestObject, shaHash);
    newBlock.put("pow", pow);
    newBlock.put("hash", getBlockHash(shaHash, pow));
    return newBlock;
  }

  /**
  * @param requestObject: JSONObject of the request
  * @return: JSONObject of the response
  */
  public JSONObject resultObject(JSONObject requestObject)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    JSONObject result = new JSONObject();
    JSONArray chain = new JSONArray(requestObject.getJSONArray("chain"));
    // validate chain
    if(!validateChain(chain)) {
      return null;
    }

    JSONObject newBlock = createNewBlock(requestObject);
    if (newBlock == null) {
      return null;
    }
    chain.put(newBlock);

    result.put("chain", chain);

    return result;
  }
  public boolean validateChain(JSONArray chain) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    // check blocks are valid
    for (int i = 0; i < chain.length(); i++) {
      JSONObject block = chain.getJSONObject(i);
      String currLastTime = validation.validateBlock(block, i, previousTime, lastBlock);
      if (currLastTime == null) {
        return false;
      }
      previousTime = currLastTime;
      lastBlock = block;
    }
    return true;
  }


}
