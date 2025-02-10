package com.blockchain.blockChainApp;

import com.blockchain.blockChainApp.Services.BlockChain;
import com.blockchain.blockChainApp.Services.Helper;
import com.blockchain.blockChainApp.Services.Validation;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBlockChainService {
    static String request = "eJyFk9tum0AQht9lr7mYw85heZWqigBDbClxqxg1lSK_ewcCBddJuldoF3a--f7hLXXH5nRO9be31Dw9PYy_58eXvvuVai-SRd3Ji1WpeR5TLbCsKo2n5z7VCcVJCDJsK1Xp2FyOcZhbMnOFdP1epZ8_XmNrOj0dUg1_XwLrilu2OBibl8d-nG5N1-qG6HJ6TDUKcRCwBVepVkpGViRAp9gb-oCiDL4AI6lnL1nvgKc7TAiLaVS-9OfDvx0vfB1k46Y9zEjvNZUwxx1MbOU_ZhCA0OMGl4KbmaYVHfDgOzMEtLjBzQ2IDAeUvRv4WE60D0ScobCtbgqIFQgHBVc35kiyMGc3LYT3xIWyUg4g2dTcaF7z1VZx8H4GmimoCDmia5kieKdAKOY5JiSMrRiwIDgbSfF7BlSPtDFagq_jabXDFmHYxXPb9lyH5JN4FBhIVQ2Ut3haMAn1sosH13Rolw65N-w36TTzN-f-9WHbg2m45639NEfpQgaCRKsqFVcjN2bgxRQL69IEZ1K2fCdLWZkMgxf168BwaLN3hjtZH_zln9bJYjm7ksI0uNc_g4cQeA==";
    static String response = "eJyFk9tum0AUAP9ln3k4lz2X5VeqqgIMsaUkrWLUVor87z12IIDtuDwhFnZnZ5b31O2bw2uqv72n5vn5x_j3cvvWd79T7UWyqDt5sSo1L2OqBaarSuPhpU91QnESggzLlaq0b477GMwtmblCOn2v0q-ff-LRefSwSzV8vgTWFbdsMTA2b0_9eJ41naoN0fHwlGoU4iBgC65SzZSMrEiATvFs6AOKMvgEjKSevWS9AT7PYUJYTGPlY_-6u97xxNdBNm7a3QXpY00lzDEHE1v5jxkEIPSYwaXgYqZpRQfc-coMAU1ucHEDIsMOZe0G7suJ7QMRZyhss5sCYgXCQcHZjTmSTMzZTQvhLXGhrJQDSBY1G81zX20VB-8vQBcKKkKO6FrOCT4oEIp5jhMSxmYMmBCcjaT4LQOqR22MLcHjPK122CIMqzzbbV_WIfkijwIDqaqB8pKnBZNQL6s8ONehVR1yb9g3dZr7RzfWKWQgSDR7UXE1cmMGnrSwsE7EnEnZ8o0ZZWUyDDjUx3VwaLN3hksdjD-DYqOhFbG6-5tv6nzJkMVydiUF_2S4yjwfdJIWh66s8ly9-LBP5L_XB_uGuHhe9QmTUyBeBUIr0nabQGDx1ekff489yA==";
    JSONObject firstTransaction = new JSONObject("{\"chain\": [], \"new_target\": \"1\", \"new_tx\": [{\"recv\": 895456882897, \"amt\": 500000000, \"time\": \"1582520400000000000\"}]}");

    private static final String TEAM_NAME = System.getenv("TEAM_NAME");
    private static final String TEAM_AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");

    @Test
    public void testRequestWithNoAllTransaction() throws JSONException, IOException, NoSuchAlgorithmException {
//      JSONObject requestObject =new JSONObject("{\"chain\":[{\"all_tx\":[{\"recv\":1097844002039,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"29ebc739\"}],\"pow\":\"43\",\"id\":0,\"hash\":\"0198f57e\",\"target\":\"0d\"},{\"all_tx\":[{\"sig\":1264597458788,\"recv\":569898087119,\"fee\":4140064,\"amt\":16748510,\"time\":\"1582520413719786436\",\"send\":1097844002039,\"hash\":\"6fc83ea7\"},{\"recv\":1097844002039,\"amt\":500000000,\"time\":\"1582520997943504569\",\"hash\":\"28ff760b\"}],\"pow\":\"5\",\"id\":1,\"hash\":\"054648f7\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":531399367681,\"recv\":696133487807,\"fee\":3032913,\"amt\":62948912,\"time\":\"1582521033285211553\",\"send\":1097844002039,\"hash\":\"19d66f33\"},{\"sig\":1526961917929,\"recv\":560501322739,\"fee\":3355425,\"amt\":476410,\"time\":\"1582521040316761040\",\"send\":1097844002039,\"hash\":\"defe7723\"},{\"recv\":560501322739,\"amt\":250000000,\"time\":\"1582521596360663251\",\"hash\":\"cdd3502b\"}],\"pow\":\"32\",\"id\":2,\"hash\":\"03fee259\",\"target\":\"0e\"}],\"new_target\":\"09\",\"new_tx\":[]}"
//      );
//      Validation validation = new Validation();
//      assertTrue(validation.validateRequest(requestObject));
//      JSONObject responseObject = new JSONObject("{\"chain\":[{\"all_tx\":[{\"recv\":1097844002039,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"29ebc739\"}],\"pow\":\"43\",\"id\":0,\"hash\":\"0198f57e\",\"target\":\"0d\"},{\"all_tx\":[{\"sig\":1264597458788,\"recv\":569898087119,\"fee\":4140064,\"amt\":16748510,\"time\":\"1582520413719786436\",\"send\":1097844002039,\"hash\":\"6fc83ea7\"},{\"recv\":1097844002039,\"amt\":500000000,\"time\":\"1582520997943504569\",\"hash\":\"28ff760b\"}],\"pow\":\"5\",\"id\":1,\"hash\":\"054648f7\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":531399367681,\"recv\":696133487807,\"fee\":3032913,\"amt\":62948912,\"time\":\"1582521033285211553\",\"send\":1097844002039,\"hash\":\"19d66f33\"},{\"sig\":1526961917929,\"recv\":560501322739,\"fee\":3355425,\"amt\":476410,\"time\":\"1582521040316761040\",\"send\":1097844002039,\"hash\":\"defe7723\"},{\"recv\":560501322739,\"amt\":250000000,\"time\":\"1582521596360663251\",\"hash\":\"cdd3502b\"}],\"pow\":\"32\",\"id\":2,\"hash\":\"03fee259\",\"target\":\"0e\"},{\"all_tx\":[{\"recv\":1097844002039,\"amt\":250000000,\"time\":\"1582522196360663251\",\"hash\":\"3f3d984a\"}],\"pow\":\"0\",\"id\":3,\"hash\":\"0647ed1c\",\"target\":\"09\"}]}"
//      );
//      assertEquals(BlockChain.resultObject(requestObject).toString(), responseObject.toString());
    }

    // Test create block with first transaction
//    @Test
//    public void testInsertFirstTransaction() throws JSONException, IOException, NoSuchAlgorithmException {
//      JSONObject previousBlock = BlockChain.getLastBlockFromChain(firstTransaction);
//      JSONArray new_transactions = new JSONArray(firstTransaction.getJSONArray("new_tx"));
//      JSONObject transactionObject = new_transactions.getJSONObject(0);
//      transactionObject.put("hash", Helper.ccHash(BlockChain.inputOfTransactionHash(transactionObject)));
//      assertEquals(transactionObject.getString("hash"),"4b277860");
//
//      JSONObject newBlock = BlockChain.createNewBlock(firstTransaction, previousBlock, new_transactions);
//
////      System.out.println(newBlock);
//      assert !newBlock.isEmpty();
//      assertEquals(newBlock.getInt("id"),0);
//      assertEquals(newBlock.getJSONArray("all_tx").getJSONObject(0).getInt("amt"),500000000L);
//    }

    // Test helper
    @Test
    public void testCompressAndDecompress() throws JSONException, IOException {
      String decompressedString = Helper.decompressZlib(Base64.decodeBase64(request));
      String encodedString = Base64.encodeBase64URLSafeString(Helper.compressZlib(decompressedString));
      int paddingCount = 4 - (encodedString.length() % 4);
      if (paddingCount > 0){
        encodedString += "=".repeat(paddingCount);
      }
      assertEquals(encodedString, request);
    }

//    @Test
//    public void testResultObject() throws JSONException, IOException, NoSuchAlgorithmException {
//      JSONObject rObject = BlockChain.getJsonObjectFromRequest(request);
//      JSONObject resultObject = BlockChain.resultObject(rObject);
//      assertEquals(resultObject.toString(), BlockChain.getJsonObjectFromRequest(response).toString());
//    }
//
//    @Test
//    public void testResponseString() throws JSONException, IOException, NoSuchAlgorithmException {
//      String responseString = BlockChain.getResponseFromRequest(request);
//      assertEquals(response, responseString);
//    }

}
