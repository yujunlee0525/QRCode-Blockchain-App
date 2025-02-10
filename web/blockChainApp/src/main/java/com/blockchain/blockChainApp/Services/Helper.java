package com.blockchain.blockChainApp.Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Helper {
  public static String decompressZlib(byte[] compressedData) throws IOException {
    // Decompress the compressedData using zlib
    // https://stackoverflow.com/questions/33020765/java-decompress-a-string-compressed-with-zlib-deflate
    ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
    InflaterInputStream inflater = new InflaterInputStream(byteStream);

    String result = "";
    byte[] buf = new byte[5];
    int rlen;
    while ((rlen = inflater.read(buf)) != -1) {
      result += new String(Arrays.copyOf(buf, rlen));
    }
    return  result;
  }

  /**
   * @param inputStr: String to compress
   * @return: byte[] of the compressed zlib data
   */
  public static byte[] compressZlib(String inputStr) throws IOException {
    // Compress the data using zlib
    // https://stackoverflow.com/questions/33020765/java-decompress-a-string-compressed-with-zlib-deflate
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    DeflaterOutputStream deflator = new DeflaterOutputStream(byteStream);
    deflator.write(inputStr.getBytes());
    deflator.flush();
    deflator.close();

    return byteStream.toByteArray();
  }

  /**
   * @param transactionHash: long of the transaction hash
   * @param d: long of the private key
   * @param n: long of the modulo
   * @return: long of the encrypted RSA value
   */
  public static long encryptedRSA(long transactionHash, long d, long n) {
    BigInteger transactionHashVal = BigInteger.valueOf(transactionHash);
    BigInteger dVal = BigInteger.valueOf(d);
    BigInteger nVal = BigInteger.valueOf(n);
    BigInteger result = transactionHashVal.modPow(dVal, nVal);

    return result.longValue();
  }

  /**
   * @param signature: long of the encrypted RSA value
   * @param e: long of the public key
   * @param n: long of the modulo
   * @return: long of the decrypted RSA value
   * @note: for validataion purpose
   */
  public static long decryptedRSA(long signature, long e, long n) {
    BigInteger ciphertextVal = BigInteger.valueOf(signature);
    BigInteger eVal = BigInteger.valueOf(e);
    BigInteger nVal = BigInteger.valueOf(n);
    BigInteger result = ciphertextVal.modPow(eVal, nVal);

    return result.longValue();
  }

  /**
   * @param originalString
   * @return ccHash of the input string
   * @throws UnsupportedEncodingException
   * @throws NoSuchAlgorithmException
   */
  public static String ccHash(String originalString) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    byte[] hash = createSHAHash(originalString);
    // ccHash: get first 8 bytes of the hash
    byte[] ccHash = new byte[4];
    System.arraycopy(hash, 0, ccHash, 0, 4);

    StringBuilder hexString = new StringBuilder();
    for (byte b : ccHash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  /**

   * @param originalString
   * @return byte[] of the SHA-256 hash of the input
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  public static byte[] createSHAHash(String originalString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    // use sha256 to hash the input: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha-256-in-java
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(originalString.getBytes("UTF-8"));
  }
}
