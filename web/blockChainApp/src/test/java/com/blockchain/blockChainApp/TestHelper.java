package com.blockchain.blockChainApp;

import com.blockchain.blockChainApp.Services.BlockChain;
import org.junit.jupiter.api.Test;
import com.blockchain.blockChainApp.Services.Helper;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelper {
  @Test
  public void testDecrypteRSA() {
    Long send1  = 895456882897L;
    long decryp1 = Helper.decryptedRSA(295281186908L, send1, BlockChain.N);
    assertEquals(Long.parseLong("b6c1b10f",16), decryp1);
    long decryp2 = Helper.decryptedRSA(1240206310211L, BlockChain.PUBLIC_KEY, BlockChain.N);
    assertEquals(Long.parseLong("a25b1fc9",16), decryp2);
  }

}
