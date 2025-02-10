package com.blockchain.blockChainApp;

import com.blockchain.blockChainApp.Services.BlockChain;
import com.blockchain.blockChainApp.Services.Validation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class TestValidation
{
  static String invalidRequest = "eJyNk91uozAQRt_F177weH48w6usqgqCaSK12VWD2pWqvPtOLFKaJmrW4sKMsTlzPvgIm22_24fu10fon58f579t-lo3b6GDZEWJUsoJLYb-ZQ4dp2XEMO9eaugCsGbOidI6Qgzb_rD1xWx12BS0cHyI4c_vdy8R-vJuDF36fCqB6cSl-sLcvz7V-VSr4RgvoA67p9BRItEEBcEoLpxGhYC8SAoxTNWpzAyTLMym4nd6TSzEkJOWrP7mQ92PVz2f26CJbCrckBpHQUbiE4zF27oaB1gmPFE1EFJDc3C6QsGMyox-AN9BGbmK6kgNZem_-P7i3WjCezGZyxAXpUJ5janUOrih-iWmc0qwpqQyDtOQ_yMlBSroF5rleJOy2UEAOBNDcSz-hguJmKmACKB8irnMe6GrNqSp9vyzl8y3vQC7F4dmP1ZWLzoo5WnSL15EFjF5FUNsFUe7ECNtz76-P661MSyVe38Z5QJEcqVDSMjaHP0DeDj-A-aD82g=";
  static String validRequest = "eJyFkt1uwjAMhd_F173wT2wnfZVpQoWmgARsgmpMQrz7Qlf-BmK5inwS5_M5OcBs0Sw3UL8doFmtJv33sN3m2RfUhMljCIiMkipo1j3UiuOqoF-uM9RAGlkZA14XVLBodosicsrTmUuC43sFnx_7UgpS5GULNV5OIaXYqeci9M12nvtTWzhWd0y75RxqMzRxUSbxasR0w2DOkUykgi4XKFZRdB6ZqSgWU3hALl3UHB3pxLTLm_Zh6JEwqKWOsgxQAwmxkrqZxEjpBUpCpJEjuKrFBwpVJ3blFPBCcd9ohGglKrXeDhC_D0b2SBiTuck_CVEpRcUUlMWvCU2D-LTp2puEiMaE6JoQSnG-wduEcDrc2eT95FprYazchCaW3JNhGVSqp-CDU16sPw_hKuHJACrkjly2r51iNclks1unNJIIcSwo5zwihZDwbyKEImxFdAynj3v8AV8iypQ=";
  static String request = "eJyFk9tum0AQht9lr7mYw85heZWqigBDbClxqxg1lSK_ewcCBddJuldoF3a--f7hLXXH5nRO9be31Dw9PYy_58eXvvuVai-SRd3Ji1WpeR5TLbCsKo2n5z7VCcVJCDJsK1Xp2FyOcZhbMnOFdP1epZ8_XmNrOj0dUg1_XwLrilu2OBibl8d-nG5N1-qG6HJ6TDUKcRCwBVepVkpGViRAp9gb-oCiDL4AI6lnL1nvgKc7TAiLaVS-9OfDvx0vfB1k46Y9zEjvNZUwxx1MbOU_ZhCA0OMGl4KbmaYVHfDgOzMEtLjBzQ2IDAeUvRv4WE60D0ScobCtbgqIFQgHBVc35kiyMGc3LYT3xIWyUg4g2dTcaF7z1VZx8H4GmimoCDmia5kieKdAKOY5JiSMrRiwIDgbSfF7BlSPtDFagq_jabXDFmHYxXPb9lyH5JN4FBhIVQ2Ut3haMAn1sosH13Rolw65N-w36TTzN-f-9WHbg2m45639NEfpQgaCRKsqFVcjN2bgxRQL69IEZ1K2fCdLWZkMgxf168BwaLN3hjtZH_zln9bJYjm7ksI0uNc_g4cQeA==";

  @Test
  public void testInvalidRequest() throws JSONException, IOException, NoSuchAlgorithmException {
    BlockChain blockChain = new BlockChain();
    String response = blockChain.getResponseFromRequest(invalidRequest);
    assertNull(response);
    blockChain = new BlockChain();
    String invalidRequest2 = "eJyFlNtu2kAURf9lnv1wbnMu_EpVRcaYgJTQKqCmUsS_90BsMDIFP1hoBux11t7MV-k27XZXFj--Svv29nL4e_740Xd_ygIhzEUACDia0r4fyqLCcDXlsH3vy6JgdaoEAterNGXT7je5SdEvO-Mox59N-f3rM5eEc3u7Kgu4fAswfF2tz41D-_HaH05rbTk2N1D77WsyibC7ukJ4bQZQr8AKjKoVm7LuE4vNPKoO1BQRJKAz6JrzuYeqeb583-9Ws7EHRpVWfN3pmeobhdSquTK74iMUTRAaSKQaAPJcn2MYhohVfUJigUumWs8k9976MCjMJSZkA0WZBOUsjNxPgqpjUHgNyplgyXIT1PJ-UACpJyezOE07cAqlABNgsdEOWKVTlN_UDpRKyWbU7IRYLT3Rxc_t2AMjJmNdu0z0hFiOjKwcNjai_kePph7MBwO6XvW0xMyy8oke1EEPXfUwAla0qR68bwdRIxk87_bATlYCRzcoVcFsTkwSKVuQ8Vl1UPtVu27rpMSQvGF5A6cLCEOa5nyojiAhzDZ2ixQkY5IZicVpxy0b9oSEJUsM1aclvnnrw5SIMh6q-b9C9ulps2ylg2mJz205pcTXlLRX1u72tOnOv9n1ny_TtWFlGhxTJIJQ6ooHwVGGETEKYxPN6s6myDOpYoYniM-OH8wCd6sT5vEfSo5rrw==";
    String response2 = blockChain.getResponseFromRequest(invalidRequest2);
    assertNull(response2);

  }

  @Test
  public void testValidRequest() throws JSONException, IOException, NoSuchAlgorithmException {
    BlockChain blockChain = new BlockChain();
    String response = blockChain.getResponseFromRequest(validRequest);
    assertNotNull(response);
    String expectedResult = "eJyFk9tu4yAQht-Fa1_MgZlh_CpVtcI2biL1pMbaXanKu5e4buJsvC1XiB8NH_PBe-h3ef8c2rv3kB8ff01_5-lb6X-HFsEtxQhAwN6E_DSFVmAZTZj2TyW0ASWREES4jNCEXT7sakheut7Yw_G-Ca8vf-pS5Brvh9DCeRegp1Gs1GDKbw9lOpUNx-aK6bB_CK0qKBsLIVuzYJpCVKOEytyEsVQoEhYwWpixJpo83iDXKqIGBnhiOpTn4ebSC2EU9RELz1AzCZKgmCqnhP4NigPgwhFNRNMNhYghmZBHOFNcF1ogBk6Cgw0zxOeBiSwhJFdT_sEQ1qUk4FGI7WKoi2xdHoeVIcTFEF4MAdfOZ1gbgm5TEaubuUK9FjebmHNfrDb6C9mE4wauMJoB1en3fSFRLqj9Sk5UVRRQJ4MzhCRkRkoVb4GALzMJY3T41w0CM2kNDaL_8EJyHvsEqVvJ2fo_JP-xo5t2hJ066fL6_8hihy52KmXvNFzZORk9fgAGxPgu";
    JSONObject expectedResultObject = BlockChain.getJsonObjectFromRequest(expectedResult);
    assertEquals(expectedResultObject.toString(), BlockChain.getJsonObjectFromRequest(response).toString());

    blockChain = new BlockChain();
    String validRequest2 = "eJyFkstuwjAQRf9l1lnM0_bkVyqEQjAQCWgFUamE-PeaNOXRINWr6I7tOXOcM7SbpttD_XaGZrud91_D5yG3n1ATekyqiIziFTS7HmrDcVXQd7sMNZAlNkbF-4IKNs1xU4rsedFGcbjMKvh4P5VIpZS7JdR424XkaWUxl0LfHNa5v2YLuFRPUMduDbWH4GbmiilVrzlXuWARWaRfaBZTCXGCrMySUJTdSutj3i8nl42EnngponFg-unqEsrRspmC_COHSkQeBFUo3uU0McSSrR7k2OiG7m4W2l5HfnLTDkf2-TS_ZQRj8PiCLxi1qAtkYYJoKapzwhBsmHLQXchZi5LgQlK9vHXQLeYkHsceJFz8W_rbgzC5o3Aiif8JF44Ykj8If_U7cjBnJJp0Ui7PSlEUyzSzyzcairpx";
    String response2 = blockChain.getResponseFromRequest(validRequest2);
    assertNotNull(response2);
    expectedResult = "eJyFk01u2zAQRu_CtRbzPxxdpSgCSZZjA0laxEJbIPDdO1GUWKnVmCthSGoeHz--lOHQHZ9K--2ldA8Pd9Of-fN5HH6VFiG8igAQcDSle5xKq7CMpkzHx7G0BbWSEghcRmnKoTsdcpJi7AfnKOfvTfn543eWhHP6uCstfKwCjLpXH3Ni6p7vx-m11pdz8wnqdLwvbZiFqoZArc02535MLER1fIcmVmHzK2Qh4gosFJqtT-PT7upnC2FU2jGLz0xvXYMtt-ZiNL4hB7OEYQzC6Bc5nZtnbb-So4sbvLjpZXg98ic3w6YbBrbqyCHSbELOZmCBlXRoqHbFqtUlqILZLSuiBsSuM82MkCclycUWjPwFBGskpy8oyJT3pfVfFIQaAUwV2W9dEJOD1VihsJlraPYx_ioq70LINAgQryiEMiLoLHBLyDCw9D3VVUy2HhHpf3JimznJkGYIaJ0ThiUotHpEZN2I_ToomFvOfwHk1_dX";
    expectedResultObject = BlockChain.getJsonObjectFromRequest(expectedResult);
    assertEquals(expectedResultObject.toString(), BlockChain.getJsonObjectFromRequest(response2).toString());

    blockChain = new BlockChain();
    validRequest2 = "eJydlNtq20AQht9lr3Ux59nxq5QQdHJiSNLSmKYQ8u4dC9mS7cSBCgRmd6395v9m9730j-3upWx-vJf26el-_3f6-Xvs_5QNQngVASDgaEr7vC8bhflpyn73PJZNQa2kBALLU5ry2L4-5iTF2PXOUT7umvLr51sOCef0bigbOK0CjLpVH3Ni3_5-GPeHsVo-mjOo191DMjERqCq6IzYzqBqhIIIb5Nh2TKz8IiHoTJ1zYEB2De3BFqZulpu_ji_DVdkz49hXzW_gRHXKB5Glci79Lp8EQNEwI2Jb8uHKTKa6ygdjzgeXfGiLxCBn-bRf5OOBB6SqVo_5BCUmCaOHz_mIWqLKkTozRbpCVkFnBdJ6ls666JkwfIzO1CamCYTCU5KIVZPm84aaQIhR-Kjp0EXmlxxpjzksaio_cZw7P2Io9dgN_UrSee3TNqRfONKIikpeIWLVw9UHr-26h9VmR7Tq4b5aMJw5Gj51pJrbeDZxlnQrGnOAU19V8MqXwCYqZAEhrreT2Wpnwt12EYSQS1SUCUybz1t6wnCkWmcMU8xzdYWhIA6SKoluY_Q4jNC2-n-C6HCqMxTMlxdBPQcPtbOVIJ_98OKnzzK0r5d-8i8v49v9MnYwP418dxWKJDbWy0uFKGVgShGpq_MgASYgRkZwK21m82yPYxJggVUujwRRcET2hxP7N0ezGyr36Hmd3n38A9gNejM=";
    response2 = blockChain.getResponseFromRequest(validRequest2);
    assertNotNull(response2);
    expectedResult = "eJydldtq20AQht9lr3Ux59nxq5RSdHJiSNLSmLYQ8u4dG9mSbMcpFRjM7qL99v9mVm-lf2x3L2Xz5a20T0_f9n-Of3-O_a-yQQivIgAEHE1pn_dlozA9TdnvnseyKaiVlEBgfkpTHtvXx5ykGLveOcr716b8-P47h4RzejeUDZxXAUbdqo85sW9_Poz7w1gt780K6nX3kExMBKqK7ojNBKpGKIjgBjm2HRMr30gIOlHnHBiQXUN7sIWpm-Xmr-PLcHXsiXHsq-Y78Eh1zgeRpXIu_SyfBEDRMCNim_PhykymusgHY8oH53xoi8Qgq3zaD_LxwANSVaunfIISk4TRw6d8RC1R5USdmSJdIaugswJpXaWzPPREGD5GZ2pHpiMIhackEasmze2COoIQo_BJ06GKzC850h5zWNRUfuZYOz9hKPXYDf1C0vrsx21IP3CkERWVvELEooarD17bZQ2rTY5oUcN9tWBYORpuOlLNbTyLOI90LxpzgHNdVfDKl8AmKmQBIa73k9lqZ8LddhaEkEtUlAlMm9slfcRwpFonDFPMvrrCUBAHSZVE9zF6HEZoW_0_QXTo6gwF88ezoJ6Dh9rZQpBPfnj20-cxtK__4AdzpWfDsoberV2YeEXyAFgvrxei1IKpR6R-dr342Gbo0c9-JMAExMgI7ulhNs96OkUHFljlsoeIgiOyoJzYP-nlbqjco9f1TXf1JcCTpcut_KakkGziat1CEkySZJYkW2nbllaSDo33_hcuUqcv";
    expectedResultObject = BlockChain.getJsonObjectFromRequest(expectedResult);
    assertEquals(expectedResultObject.toString(), BlockChain.getJsonObjectFromRequest(response2).toString());
  }
  /*
  @Test
  public void testValidateRequest() throws JSONException, IOException, NoSuchAlgorithmException {
    JSONObject validRequest = BlockChain.getJsonObjectFromRequest(request);
    assertTrue(Validation.validateRequest(validRequest));

    JSONObject invalidRequest = new JSONObject(validRequest.toString());
    invalidRequest.remove("chain");
    assertFalse(Validation.validateRequest(invalidRequest));

    invalidRequest = new JSONObject(validRequest.toString());
    invalidRequest.put("new_target", "invalid");
    assertFalse(Validation.validateRequest(invalidRequest));

    invalidRequest = new JSONObject(validRequest.toString());
    invalidRequest.put("new_tx", new JSONArray());
    assertTrue(Validation.validateRequest(invalidRequest));
  } */

//  @Test
//  public void testValidateBlock() throws JSONException, IOException, NoSuchAlgorithmException {
//    JSONObject validRequest = BlockChain.getJsonObjectFromRequest(request);
//    JSONArray chain = validRequest.getJSONArray("chain");
//    JSONObject validBlock = chain.getJSONObject(0);
//    assertTrue(Validation.validateBlock(validBlock, 0, null));
//
//    JSONObject invalidBlock = new JSONObject(validBlock.toString());
//    invalidBlock.remove("pow");
//    assertFalse(Validation.validateBlock(invalidBlock, 0, null));
//
//    assertFalse(Validation.validateBlock(validBlock, 1, null));
//
//    invalidBlock = new JSONObject(validBlock.toString());
//    invalidBlock.put("pow", "invalid");
//    assertFalse(Validation.validateBlock(invalidBlock, 0, null));
//  }

  @Test
  public void testValidateTransaction() throws JSONException, IOException, NoSuchAlgorithmException {
    JSONObject validRequest = BlockChain.getJsonObjectFromRequest(request);
    JSONArray newTx = validRequest.getJSONArray("new_tx");
    JSONObject validTransaction = newTx.getJSONObject(0);
    Validation validation = new Validation();
//    assertTrue(validation.validateTransaction(validTransaction, true, false));
//    validation = new Validation();
    JSONObject invalidTransaction = new JSONObject(validTransaction.toString());
    invalidTransaction.remove("recv");
    assertFalse(validation.validateTransaction(invalidTransaction, true, false));
//    validation = new Validation();
    invalidTransaction = new JSONObject(validTransaction.toString());
    invalidTransaction.put("time", "invalid");
    assertFalse(validation.validateTransaction(invalidTransaction, true, false));
  }

//  @Test
//  public void testIsValidRewardTransaction() throws JSONException, IOException, NoSuchAlgorithmException {
//    JSONObject validRequest = BlockChain.getJsonObjectFromRequest(request);
//    JSONArray chain = validRequest.getJSONArray("chain");
//    JSONObject block = chain.getJSONObject(0);
//    JSONArray allTx = block.getJSONArray("all_tx");
//    JSONObject rewardTransaction = allTx.getJSONObject(allTx.length() - 1);
//    Validation validation = new Validation();
//    assertTrue(validation.validateTransaction(rewardTransaction, false, true));
//    JSONObject invalidRewardTransaction = new JSONObject(rewardTransaction.toString());
//    invalidRewardTransaction.put("send", 123456789L);
//    assertFalse(validation.validateTransaction(invalidRewardTransaction,false,true));
//  }
  @Test
  public void testDescendingTime(){

  }
}
