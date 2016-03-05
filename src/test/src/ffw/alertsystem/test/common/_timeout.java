package ffw.alertsystem.test.common;

import static org.junit.Assert.fail;




public class _timeout {
  
  public static void waitfor(BooleanRef flag) {
    // wait max. 12*250ms = 3s before timeout
    int timeout = 12;
    
    try {
      while (!flag.is && timeout > 0) {
        timeout--;
        Thread.sleep(250);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // reset flag for next test
    flag.is = false;
    
    if (timeout == 0) {
      fail("Timeout when waiting for flag");
    }
  }
  
  // since both, the primitive boolean type and the Boolean wrapper class, are
  // not offering pass-by-reference, we have to implement our own wrapper.
  public static class BooleanRef {
    public boolean is = false;
  }
  
}
