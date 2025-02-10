package com.qrcode.qrCodeApp;

import org.junit.jupiter.api.Test;
import com.qrcode.qrCodeApp.Services.QRCode;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEncoded {
  @Test
  public void testEncodeData(){
    String msg = "Test";
    byte[] encoded = QRCode.encodeData(msg);
    // convert byte array to string
    assertEquals("045401650073017400", Hex.encodeHexString( encoded ));

  }
  @Test
  public void testVersion1(){
    String msg = "CC Team";
    byte[] encoded_1 = QRCode.encodeData(msg);
    int[][] qrcode = QRCode.generateQRMatrix(encoded_1, 1);
    String qrcodeHex = QRCode.encodeMatrixToHexString(qrcode);
    assertEquals(qrcodeHex,"0xfe03fc120xd06e82bb0x74b5dba70x2ec111070xfaafe00e0x8a05170x492f60x599912030x7d80003a0xff889100x4c00ba050x35d088ee0x964504120x1fca80");
    
    //for (int[] row : qrcode)
    //  System.out.println(java.util.Arrays.toString(row));
    
    String encodedMsg = QRCode.encodeMessage(msg);
    assertEquals(encodedMsg, "0x66d92b800x5bc76d830x121a7fa60x51c111870x3a5f3ca30x8be36a130xedb223a0xfc8e98780x33bf50de0x2e8709700x545a2d0f0xecef7ae0x461175cd0xff132a");
    
  }

  @Test
  public void testVersion2(){
    String msg = "CC Team is awesome!";
    byte[] encoded = QRCode.encodeData(msg);
    int[][] qrcode = QRCode.generateQRMatrix(encoded, 2);
    String qrcodeHex = QRCode.encodeMatrixToHexString(qrcode);
    assertEquals(qrcodeHex, "0xfe373fc10x38106e990x8bb740050xdba002ec0x10c907fa0xaafe00090xed8880x58d9a8c40x984117080xe20000x3240020b0xbc4140080xa8600ecc0xf80019440xff902b100x48918ba00xfd5d0070x6ee861410x42a22fe0x3800");
    
    String encodedMsg = QRCode.encodeMessage(msg);
    assertEquals(encodedMsg,"0x66ede8530xb3b981a10xed18e4040xa4a0026c0xd039db570x21976f0d0xed168440xfdce22bf0xd67e47ec0x2171a0600x2a1a95010x875f3f480x78347f130x886ccc430xc90f439a0x331f54900x7bbcbf030x20d731250xc555223e0x15858");
  }
}
