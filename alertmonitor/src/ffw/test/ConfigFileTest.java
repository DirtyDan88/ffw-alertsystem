package ffw.test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ffw.alertmonitor.AlertActionManager.AlertActionDesc;
import ffw.util.config.ConfigFile;


public class ConfigFileTest {
  
  private final String pathToTestFiles = "test/";
  
  @Test
  public void testExampleConfigFile() {
    boolean validConfig       = ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    boolean faultyConfig      = ConfigFile.setFileName(pathToTestFiles + "faultyExampleConfig.xml");
    boolean notExistingConfig = ConfigFile.setFileName(pathToTestFiles + "notExistingConfig.xml");
    
    assertTrue(validConfig);
    assertFalse(faultyConfig);
    assertFalse(notExistingConfig);
  }
  
  @Test
  public void testGetParam() {
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    
    String paramValue = "";
    paramValue = ConfigFile.getConfigParam("notExistingParam");
    assertNull(paramValue);
    
    paramValue = ConfigFile.getConfigParam("pocsag-port");
    assertEquals("12345", paramValue);
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
  public void testGetAlertActionRics() {
    ConfigFile.setFileName(pathToTestFiles + "notExistingConfig.xml");
    List<String> ricList = ConfigFile.getAlertActionRics("");
    assertEquals(0, ricList.size());
    
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    
    ricList = ConfigFile.getAlertActionRics(
                "AlertActionExample1"
              );
    assertEquals(1, ricList.size());
    assertTrue(ricList.contains("42"));
    
    ricList = ConfigFile.getAlertActionRics(
                "AlertActionExample2"
              );
    assertEquals(2, ricList.size());
    assertTrue(ricList.contains("42"));
    assertTrue(ricList.contains("99"));
    
    ricList = ConfigFile.getAlertActionRics(
                "AlertActionExample3"
              );
    assertEquals(1, ricList.size());
    assertTrue(ricList.contains("*"));
  }
  
  @Test
  public void testGetAlertActionParams() {
    ConfigFile.setFileName(pathToTestFiles + "notExistingConfig.xml");
    Map<String, String> paramList = ConfigFile.getAlertActionParams("");
    assertEquals(0, paramList.size());
    
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");
    
    paramList = ConfigFile.getAlertActionParams(
                             "AlertActionExample1"
                           );
    assertEquals(3, paramList.size());
    assertTrue(paramList.containsKey("test-param1"));
    assertTrue(paramList.get("test-param1").equals("some value"));
    assertTrue(paramList.containsKey("test-param2"));
    assertTrue(paramList.get("test-param2").equals("other value"));
    
    paramList = ConfigFile.getAlertActionParams(
      "AlertActionExample2"
    );
    assertEquals(0, paramList.size());
    
    paramList = ConfigFile.getAlertActionParams(
      "AlertActionExample3"
    );
    assertEquals(0, paramList.size());
  }
  
  /*
  @Test
  public void testGetAlertActionParam() {
    ConfigFile.setFileName(pathToTestFiles + "validExampleConfig.xml");

    String paramValue = ConfigFile.getAlertActionParam(
      "AlertActionExample1", 
      "test-param1"
    );
    assertEquals("some value", paramValue);
  }
  */
}
