package ffw.test;
//TODO: import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import ffw.alertlistener.AlertMessage;
import ffw.alertlistener.AlertMessageFactory;


public class AlertMessageTest {
  
  @Test
  public void test() {
    //fail("Not yet implemented");
  }
  
  @Test
  public void testAlertMessageFactory() {
    AlertMessage message1 = AlertMessageFactory.create("");
    Assert.assertNull(message1);
    
    //AlertMessage message2 = AlertMessageFactory.create("POCSAG");
    //Assert.assertThat(message2, instanceOf(BaseClass.class));
  }
}
