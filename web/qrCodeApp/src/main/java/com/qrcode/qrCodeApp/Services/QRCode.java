package com.qrcode.qrCodeApp.Services;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.qrcode.qrCodeApp.AuthServiceGrpc;
import com.qrcode.qrCodeApp.AuthRequest;
import com.qrcode.qrCodeApp.AuthResponse;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;



public class QRCode {
  private static final String TEAM_NAME = System.getenv("TEAM_NAME");
  private static final String TEAM_AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");
  private static final String AUTH_SERVICE_URL = "http://auth-service.default.svc.cluster.local:9000/rest_auth";
  private static final String AUTH_SERVICE_HOST = "auth-service.default.svc.cluster.local";
  private static final int AUTH_SERVICE_PORT = 9000;

  public static String handleEncodeRequest(String message) throws NoSuchAlgorithmException {
    String encodedHexString = encodeMessage(message);
    return getResponseString(encodedHexString);
  }
  public static String handleDecodeRequest(String encodedData, String timestamp) throws IOException, NoSuchAlgorithmException {
    String msg = decodeQRCode(encodedData);
    return msg;
  }
  public static String encodeMessage(String message) {
    int version = message.length() <= 13 ? 1 : 2;
    byte[] encodedData = encodeData(message);
    int[][] qrMatrix = generateQRMatrix(encodedData, version);
    int[] logisticMap = produceLogisticMap(version);
    encryptQRImageWithLogisticMap(qrMatrix, logisticMap);
    
    return encodeMatrixToHexString(qrMatrix);
  }
  public static byte[] encodeData(String message) {
    byte[] messageBytes = message.getBytes();
    int dataLength = messageBytes.length;

    // character count indicator (8 bits for version 1-9) + message(where each character is 8 bits and followed by 1 byte padding)
    byte[] encoded = new byte[1 + dataLength * 2];
    encoded[0] = (byte) dataLength; // char count
    for (int i =0; i < dataLength; i++){
      int encodedIndex = i * 2 + 1;
      encoded[encodedIndex] = messageBytes[i];
      // checksum = xor all bits of the byte
      for(int j = 0; j < 8; j++){
        encoded[encodedIndex+1] ^= (messageBytes[i] >> j) & 1;
      }
    }

    return encoded;
  }
  public static int[][] generateQRMatrix(byte[] encodedData, int version) {
    int size = version == 1 ? 21 : 25;
    int[][] matrix = new int[size][size];

    addFunctionPatterns(matrix, version);
    fillMatrixWithData(matrix, encodedData, version);

    return matrix;
  }

  private static void encryptQRImageWithLogisticMap(int[][] matrix, int[] logisticMap) {
    int size = logisticMap.length;
    int mSize = matrix.length;
    int bitCount = 0;
    int cell = 0;
    int cellIndex = 0;

    for (int r = 0; r < mSize; r++){
      for (int c = 0; c < mSize; c++){
        int newbit = matrix[r][c];
        newbit = newbit ^ ((logisticMap[cellIndex] >> bitCount) & 1);
        matrix[r][c] = newbit;
        bitCount ++;
        if (bitCount == 8){
          cellIndex ++;
          bitCount = 0;
        }
      }
    }
  }

  public static int[] produceLogisticMap(int version) {
    // TODO: finish this
    int size = version == 1 ? 21 : 25;
    int logisticSize = size * size / 8 + 1;
    int[] logisticMap = new int[logisticSize];
    double xn = 0.1;
    double r = 4;
    logisticMap[0] = 25;
    for (int i = 1; i < logisticSize; i++){
      xn = r * xn * (1-xn);
      logisticMap[i] = (int)(xn * 255);
    }
    return logisticMap;
  }

  private static void addFunctionPatterns(int[][] matrix, int version) {
    addPositionDetectionPattern(matrix, 0, 0);
    addPositionDetectionPattern(matrix, 0, matrix.length - 7);
    addPositionDetectionPattern(matrix, matrix.length - 7, 0);
    // add Timing Patterns
    for (int i = 8; i < matrix.length - 8; i++) {
      matrix[6][i] = matrix[i][6] = (i % 2 == 0) ? 1 : 0;
    }

    if (version == 2) {
      addAlignmentPattern(matrix, 18, 18);
    }
    
  }
  public static void addPositionDetectionPattern(int[][] matrix, int row, int col) {
    for (int r = 0; r < 7; r++) {
      for (int c = 0; c < 7; c++) {
        if ((r == 0 || r == 6 || c == 0 || c == 6) || (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
          matrix[row + r][col + c] = 1;
        } else {
          matrix[row + r][col + c] = 0;
        }
      }
    }
  }
  private static void addAlignmentPattern(int[][] matrix, int row, int col) {
    for (int r = -2; r <= 2; r++) {
      for (int c = -2; c <= 2; c++) {
        matrix[row + r][col + c] = (Math.abs(r) == 2 || Math.abs(c) == 2 || (r == 0 && c == 0)) ? 1 : 0;
      }
    }
  }
  private static int zigzagUp(int[][] matrix, byte[] data, int start, int end, int col, int index){
    for (int r = start; r >= end; r--){
      if (r == 6){
        continue; 
      }
      int byteIndex = index/8;
      int bitIndex = 7 - index%8; 
      matrix[r][col] = (data[byteIndex] >> bitIndex) & 1;
      index ++;

      byteIndex = index/8;
      bitIndex = 7 - index%8;
      matrix[r][col-1] = (data[byteIndex] >> bitIndex) & 1;
      index ++;
    }
    return index;
  }
  private static int zigzagDown(int[][] matrix, byte[] data, int start, int end, int col, int index){
    for (int r = start; r < end; r++){
      if (r == 6){
        continue; 
      }
      int byteIndex = index/8;
      int bitIndex = 7 - index%8; 
      matrix[r][col] = (data[byteIndex] >> bitIndex) & 1;
      index ++;

      byteIndex = index/8;
      bitIndex = 7 - index%8;
      matrix[r][col-1] = (data[byteIndex] >> bitIndex) & 1;
      index ++;
    }
    return index;
  }
  private static void fillMatrixWithData(int[][] matrix, byte[] data, int version) {
    // 370 bits payload translates to arounded 47 total bytes needed for v2
    // 224 bits payload translates to 28 bytes needed for v1
    // fill the space with 11101100 00010001 -> 0xEC, 0x11 -> 236, 17 
    int total_size = version == 1 ? 28 : 47;
    int size = data.length;
    byte[] payload;
    if (size < total_size){
      payload = new byte[total_size];
      System.arraycopy(data, 0, payload, 0, size);
      byte padding = (byte)236;    //11101100
      for (int i = size; i < total_size; i ++){
        payload[i] = padding;
        if (padding == (byte)236){
          padding = (byte)17;       //00010001
        }else{
          padding = (byte)236;
        }
      }
    }else{
      payload = data;
    }

    int index = 0;
    int col = matrix.length - 1;
    int mSize = matrix.length;

    while(col > 0){
      if (col > mSize -1 - 8){
        if (version == 2 && col == mSize - 1 - 4){
          //alignment pattern
          index = zigzagUp(matrix, payload, mSize-1, mSize-4, col, index);
          index = zigzagUp(matrix, payload, mSize-10, 8, col, index);
          col -= 2;
          index = zigzagDown(matrix, payload, 8, mSize-9, col, index);
          index = zigzagDown(matrix, payload, mSize-4, mSize, col, index);
          col -= 2; 
        }else{
          index = zigzagUp(matrix, payload, mSize-1, 8, col, index);
          col -= 2;
          index = zigzagDown(matrix, payload, 8, mSize, col, index);
          col -= 2;
        }
      }
      else if (col > 8){
        if (version == 2 && col == mSize - 1 - 8){
          //alignment pattern
          index = zigzagUp(matrix, payload, mSize-1, mSize-4, col, index);
          index = zigzagUp(matrix, payload, mSize-10, 0, col, index);
          col -= 2;
          index = zigzagDown(matrix, payload, 0, mSize, col, index);
          col -= 2; 
        }else{
          index = zigzagUp(matrix, payload, mSize-1, 0, col, index);
          col -= 2;
          index = zigzagDown(matrix, payload, 0, mSize, col, index);
          col -= 2;
        }
      }
      else{ //less than equal to 8 
        index = zigzagUp(matrix, payload, mSize-9, 8, col, index);
        col -= 2;
        if (col == 6){ //timing pattern
          col -= 1;
        }
        index = zigzagDown(matrix, payload, 8, mSize-8, col, index);
        col -= 2;
      }
    }
  }

  public static String encodeMatrixToHexString(int[][] matrix) {
    StringBuilder hexString = new StringBuilder();
    int value = 0;
    int bitCount = 0;

    for (int[] row : matrix) {
      for (int bit : row) {
        value = (value << 1) | bit;
        bitCount++;
        if (bitCount == 32) {
          hexString.append(String.format("0x%x", value));
          value = 0;
          bitCount = 0;
        }
      }
    }

    if (bitCount > 0) {
      hexString.append(String.format("0x%x", value));
    }

    return hexString.toString();
  }

  public static String decodeQRCode(String encodedData) {
    int[][] matrix = decryptWithLogisticMap(encodedData);
    int [][] qrCode = findQRCode(matrix); //with rotation
    byte[] payload = extractFilling(qrCode);
    String msg = decodeFilling(payload);
    return msg;
  }

  public static String decodeFilling(byte[] payload){
    int dataLength = payload.length;
    StringBuilder msg = new StringBuilder();
    for (int i = 1; i < dataLength; i+=2){
      msg.append((char)payload[i]);
    }
    return msg.toString();
  }
  private static int zigzagUpExtract(int[][] matrix, byte[] data, int start, int end, int col, int index, int byteSize){
    for (int r = start; r >= end; r--){
      if (r == 6){
        continue; 
      }
      int byteIndex = index/8;
      if (byteIndex == byteSize) return -1;
      data[byteIndex] = (byte)((data[byteIndex] << 1) + (byte)matrix[r][col]);
      index ++;

      byteIndex = index/8;
      data[byteIndex] = (byte)((data[byteIndex] << 1) + (byte)matrix[r][col -1]);
      index ++;
    }
    return index;
  }
  private static int zigzagDownExtract(int[][] matrix, byte[] data, int start, int end, int col, int index, int byteSize){
    for (int r = start; r < end; r++){
      if (r == 6){
        continue; 
      }
      int byteIndex = index/8;
      if (byteIndex == byteSize) return -1;
      data[byteIndex] = (byte)((data[byteIndex] << 1) + (byte)matrix[r][col]);
      index ++;

      byteIndex = index/8;
      data[byteIndex] = (byte)((data[byteIndex] << 1) + (byte)matrix[r][col-1]);
      index ++;
    }
    return index;
  }
  public static byte[] extractFilling(int [][] matrix){
    int mSize = matrix.length;
    int version = mSize == 21 ? 1 : 2;

    //get byte size:
    byte[] tmp = new byte[1];
    zigzagUpExtract(matrix, tmp, mSize-1, mSize - 4, mSize - 1, 0, 2);
    int byteSize = (int) tmp[0] * 2 + 1; 
    byte[] payload = new byte[byteSize];

    int col = mSize - 1;
    int index = 0;
    while(col > 0){
      if (col > mSize -1 - 8){
        if (version == 2 && col == mSize - 1 - 4){
          //alignment pattern
          index = zigzagUpExtract(matrix, payload, mSize-1, mSize-4, col, index, byteSize);
          if (index < 0) break;
          index = zigzagUpExtract(matrix, payload, mSize-10, 8, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
          index = zigzagDownExtract(matrix, payload, 8, mSize-9, col, index, byteSize);
          if (index < 0) break;
          index = zigzagDownExtract(matrix, payload, mSize-4, mSize, col, index, byteSize);
          if (index < 0) break;
          col -= 2; 
        }else{
          index = zigzagUpExtract(matrix, payload, mSize-1, 8, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
          index = zigzagDownExtract(matrix, payload, 8, mSize, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
        }
      }
      else if (col > 8){
        if (version == 2 && col == mSize - 1 - 8){
          //alignment pattern
          index = zigzagUpExtract(matrix, payload, mSize-1, mSize-4, col, index, byteSize);
          if (index < 0) break;
          index = zigzagUpExtract(matrix, payload, mSize-10, 0, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
          index = zigzagDownExtract(matrix, payload, 0, mSize, col, index, byteSize);
          if (index < 0) break;
          col -= 2; 
        }else{
          index = zigzagUpExtract(matrix, payload, mSize-1, 0, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
          index = zigzagDownExtract(matrix, payload, 0, mSize, col, index, byteSize);
          if (index < 0) break;
          col -= 2;
        }
      }
      else{ //less than equal to 8 
        index = zigzagUpExtract(matrix, payload, mSize-9, 8, col, index, byteSize);
        if (index < 0) break;
        col -= 2;
        if (col == 6){ //timing pattern
          col -= 1;
        }
        index = zigzagDownExtract(matrix, payload, 8, mSize-8, col, index, byteSize);
        if (index < 0) break;
        col -= 2;
      }
    }
    return payload;
  }

  public static int[][] findQRCode(int[][] matrix){
    int size = 32;

    int r = 0;
    int c = 0;
    int[][] pos = new int[3][2];
    int count = 0;

    while (r < size-7){
      if (count == 3){
        break;
      }
      if (c > size - 7){
        r ++;
        c = 0;
        continue;
      }
      if (foundPositionDetection(r,c,matrix)){
        pos[count][0] = r;
        pos[count][1] = c;
        count ++;
        c += 7;
        if (c > size - 14){
          r += 7;
          c = 0;
        }
      }else{
        c++;
      }
    }

    int r1 = pos[0][0];
    int c1 = pos[0][1];
    int r2 = pos[1][0];
    int c2 = pos[1][1];
    int r3 = pos[2][0];
    int c3 = pos[2][1];
    int rotation = 0;
    int rowStart = 0;
    int rowEnd = 0;
    int colStart = 0;
    int colEnd = 0;
    if (r1 == r2 && c1 == c3){
      rotation = 0;
      rowStart = r1;
      rowEnd = r3 + 7;
      colStart = c1;
      colEnd = c2 + 7;
    }else if (r1 == r2 && c2 == c3){
      rowStart = r1;
      rowEnd = r3 + 7;
      colStart = c1;
      colEnd = c2 + 7;
      rotation = 270; 
    }else if (c1 == c2 && r2 == r3){
      rotation = 90;
      rowStart = r1;
      rowEnd = r2 + 7;
      colStart = c2;
      colEnd = c3 + 7;
    }else if (c1 == c3 && r2 == r3){
      rotation = 180;
      rowStart = r1;
      rowEnd = r3 + 7;
      colStart = c2;
      colEnd = c3 + 7;
    }else{
      System.out.println("invalid qrCode");
      return matrix;
      //TODO: deal with invalid case
    }
    if (rowEnd - rowStart != colEnd - colStart){
      //TODO: deal with invalid case
      System.out.println("invalid dimension");
      return matrix;
    }
    if (rowEnd - rowStart != 21 && rowEnd - rowStart != 25){
      //TODO: deal with invalid case
      System.out.println("invalid version");
      return matrix;
    }
    int[][] qrCode = new int[rowEnd - rowStart][colEnd - colStart];
    for (int i = 0; i < rowEnd-rowStart; i++){
      for (int j = 0; j < colEnd - colStart; j++){
        qrCode[i][j] = matrix[rowStart + i][colStart + j];
      }
    }
    
    return rotateQRCode(qrCode, rotation);
  }

  private static int[][] rotateQRCode(int[][] matrix, int rotation){
    int n = matrix.length;
    if (rotation == 0){
      return matrix;
    }
    if (rotation == 90){
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          int temp = matrix[i][j];
          matrix[i][j] = matrix[j][i];
          matrix[j][i] = temp;
        }
      }
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n / 2; j++) {
          int temp = matrix[i][j];
          matrix[i][j] = matrix[i][n - j - 1];
          matrix[i][n - j - 1] = temp;
        }
      }
      return matrix;
    }

    if (rotation == 180){
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < (n + 1) / 2; j++) {
          int temp = matrix[i][j];
          matrix[i][j] = matrix[n - i - 1][n - j - 1];
          matrix[n - i - 1][n - j - 1] = temp;
        }
      }
      return matrix;
    }
    else{ // 270
      for (int i = 0; i < n / 2; i++) {
        for (int j = i; j < n - i - 1; j++) {
          int temp = matrix[i][j];
          matrix[i][j] = matrix[j][n - i - 1]; // left to top
          matrix[j][n - i - 1] = matrix[n - i - 1][n - j - 1]; // bottom to left
          matrix[n - i - 1][n - j - 1] = matrix[n - j - 1][i]; // right to bottom
          matrix[n - j - 1][i] = temp; // top to right
        }
      }
      return matrix;
    }
  }
  private static boolean foundPositionDetection(int row, int col, int[][] matrix){
    for (int r = 0; r < 7; r++) {
      for (int c = 0; c < 7; c++) {
        if ((r == 0 || r == 6 || c == 0 || c == 6) || (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
          if(matrix[row + r][col + c] != 1){
            return false;
          }
        } else {
          if (matrix[row + r][col + c] != 0){
            return false;
          }
        }
      }
    }
    return true;
  }

  private static int[] produceDecodeLogisticMap() {
    // TODO: finish this
    int size = 32;
    int logisticSize = size * size/8;
    int[] logisticMap = new int[logisticSize];
    double xn = 0.1;
    double r = 4;
    logisticMap[0] = 25;
    for (int i = 1; i < logisticSize; i++){
      xn = r * xn * (1-xn);
      logisticMap[i] = (int)(xn * 255);
    }
    return logisticMap;
  }

  public static int[][] decryptWithLogisticMap(String encodedData){
    int[] logisticMap = produceDecodeLogisticMap();
    String[] hexArray = encodedData.split("0x");
    long[] data = new long[32];
    for (int i = 0; i < 32; i++){
      data[i] = Long.parseUnsignedLong(hexArray[i+1], 16);
    }

    int[][] matrix = new int[32][32];
    int mSize = matrix.length;
    int cellBitCount = 0;
    int hexBitCount = 0;
    int cellIndex = 0;
    int hexIndex = 0;

    for (int r = 0; r < mSize; r++){
      for (int c = 0; c < mSize; c++){
        int newbit = (int)(data[hexIndex] >> (31 - hexBitCount)) & 1;
        newbit = newbit ^ ((logisticMap[cellIndex] >> cellBitCount) & 1);
        matrix[r][c] = newbit;
        hexBitCount ++;
        cellBitCount ++;
        if (cellBitCount == 8){
          cellIndex ++;
          cellBitCount = 0;
        }
        if (hexBitCount == 32){
          hexBitCount = 0;
          hexIndex ++; 
        }
      }
    }
    return matrix; 
  }
  
  
/*
  private static String authenticateWithRestServer(String decodedMessage, String timestamp) throws IOException {
    Vertx vertx = Vertx.vertx();
    WebClient client = WebClient.create(vertx);
    JsonObject requestBody = new JsonObject()
        .put("team_name", TEAM_NAME)
        .put("timestamp", timestamp)
        .put("decoded_qrcode", decodedMessage);

    String token = "";
    String message = "";
    client.post(AUTH_SERVICE_PORT, AUTH_SERVICE_HOST, "/rest-auth")
      .putHeader("Content-Type", "application/json")
      .sendJsonObject(requestBody)
      .onSuccess(response->{
        JsonObject responseBody = response.bodyAsJsonObject();;
        message = responseBody.getString("message");
        token = responseBody.getString("token");
        System.out.println(message + token);
      })
      .onComplete(ar -> {
        client.close();
        vertx.close();
      });
      return token;
  }
  */
  private static String authenticateWithGrpcServer(String decodedMessage, String timestamp) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress(AUTH_SERVICE_HOST, AUTH_SERVICE_PORT)
      .usePlaintext()
      .build();
    try {
      AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);
      AuthRequest request = AuthRequest.newBuilder()
        .setTeamName(TEAM_NAME)
        .setTimestamp(Long.parseLong(timestamp))
        .setDecodedQrcode(decodedMessage)
        .build();
      AuthResponse response = stub.auth(request);
      return response.getMessage();
    } finally {
      channel.shutdown();
    }
  }

  public static String getResponseString(String result) {
    return TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\n" + result;
  }
}

