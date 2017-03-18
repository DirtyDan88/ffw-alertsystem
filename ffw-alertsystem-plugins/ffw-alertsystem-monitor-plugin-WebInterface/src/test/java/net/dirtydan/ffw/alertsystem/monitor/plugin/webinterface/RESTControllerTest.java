/*
  Copyright (c) 2015-2017, Max Stark <max.stark88@web.de>
    All rights reserved.
  
  This file is part of ffw-alertsystem, which is free software: you
  can redistribute it and/or modify it under the terms of the GNU
  General Public License as published by the Free Software Foundation,
  either version 2 of the License, or (at your option) any later
  version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

package net.dirtydan.ffw.alertsystem.monitor.plugin.webinterface;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;


public class RESTControllerTest {
  
  static int port = 42422;
  
  
  @BeforeClass
  public static void setup() {
    JunitMonitorPluginSource cs = new JunitMonitorPluginSource();
    JunitMonitorPluginManager pm = new JunitMonitorPluginManager(cs);
    pm.loadAll();
    pm.startAll();
    
    RestAssured.port = port;
  }
  
  
  @Test
  public void testAuthorizatiom() {
    when()
      .get("/plugins/activate").
    then()
      .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }
  
  @Test
  public void testPluginController() {
    given()
      .auth().basic("junit-tester", "test").
    when()
      .get("/api/plugins/activate/PLUGIN-NAME").
    then()
      .statusCode(HttpStatus.SC_OK);
  }
  
}
