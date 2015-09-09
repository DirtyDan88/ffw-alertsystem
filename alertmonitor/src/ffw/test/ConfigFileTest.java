package ffw.test;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ffw.alertmonitor.AlertActionManager.AlertActionDesc;
import ffw.util.config.ConfigFile;


public class ConfigFileTest {
  
  private final String pathToTestFiles = "test/";
  
  @Test
  public void testExampleConfigFile() {
    boolean validConfig  = ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    boolean faultyConfig = ConfigFile.setFileName(pathToTestFiles + "faultyExampleConfig.xml");
    
    assertTrue(validConfig);
    assertFalse(faultyConfig);
  }
  
  @Test
  public void testGetParam() {
    
  }
  
  @Test
  public void testGetAlertActionDescs() {
    ConfigFile.setFileName(pathToTestFiles + "notExistingConfig.xml");
    List<AlertActionDesc> listActionDescs = ConfigFile.getAlertActionDescs();
    assertEquals(0, listActionDescs.size());
    
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    listActionDescs = ConfigFile.getAlertActionDescs();
    
    assertEquals("ffw.test",             listActionDescs.get(0).packageName);
    assertEquals("AlertActionTestClass", listActionDescs.get(0).className);
    assertEquals("AlertActionExample1",  listActionDescs.get(0).instanceName);
    
    assertEquals("ffw.test",             listActionDescs.get(1).packageName);
    assertEquals("AlertActionTestClass", listActionDescs.get(1).className);
    assertEquals("AlertActionExample2",  listActionDescs.get(1).instanceName);
  }
  
  @Test
  public void testGetAlertActionParam() {
 // isActive
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    
    String paramValue = ConfigFile.getAlertActionParam(
      "AlertActionExample1", 
      "test-param1"
    );
    
    assertEquals("some value", paramValue);
  }
  
  @Test
  public void testGetAllAlertActionParams() {
    
  }
  
  
  
  
}
