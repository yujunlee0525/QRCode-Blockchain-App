package com.qrcode.qrCodeApp;

import org.junit.jupiter.api.Test;
import com.qrcode.qrCodeApp.Services.QRCode;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDecoded {
  @Test
  public void testVersion1(){
    String input2 = "0xf79377080xd26040830x4b43651a0xd94c13ef0x5169ad450x75afa9450xf11f388d0xf5479ef20xf96b4cb20x962fa8da0x8f1ab7110xab40719c0xf8431070x107d95570xf8d86f6b0xf0ab7a390x75898fa30x451af5a80xa13800340x102965380x9512f92c0xdcd606370xb86214780x5fb386dd0x28c93d710x4a393d990x7c57642e0xc4c668290x20ae2e960xcdcd754b0xb70b95f90x228302e4";
    String input = "0x663cbc630x98f78b370xbef2db180xddd114aa0x54a0e1d70xe9a244870x1659c7170x89506acf0x4dc10b180xefdf26e0xe8e005e10x86a4fda90x15ee1b320xa0ee170e0xe461c3740x928e7f340x98698d070x239af4b60x627807440xfd89c1300x422764790xcd1ac7250xf4364a650x48eeb1570xa7946ab10x665176370x81a511290xceaf2a070xd3fdb0060xebf8889f0x9f7c49160x69e8175b";
    /*
    int [][] matrix = QRCode.decryptWithLogisticMap(input2);
    int [][] qrCode = QRCode.findQRCode(matrix);
    for (int[] row : qrCode)
      System.out.println(java.util.Arrays.toString(row));
    byte[] data = QRCode.extractFilling(qrCode);
    System.out.println(Hex.encodeHexString( data ));
    */
    assertEquals(QRCode.decodeQRCode(input), "Upc7Lu");
  }
  @Test
  public void testEncodeWithDecode(){
    String input1 = "CC%20Team";
    String input2 = "CC Team is awesome!";
    byte[] encoded_1 = QRCode.encodeData(input1);
    int[][] qrcode_1 = QRCode.generateQRMatrix(encoded_1, 1);
    byte[] encoded_2 = QRCode.encodeData(input2);
    int[][] qrcode_2 = QRCode.generateQRMatrix(encoded_2, 2);
    String output1 = QRCode.decodeFilling(QRCode.extractFilling(qrcode_1));
    String output2 = QRCode.decodeFilling(QRCode.extractFilling(qrcode_2));
    assertEquals(input1, output1);
    assertEquals(input2, output2);
  }
}
