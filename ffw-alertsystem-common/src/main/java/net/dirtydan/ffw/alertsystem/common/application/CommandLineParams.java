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

package net.dirtydan.ffw.alertsystem.common.application;

import com.beust.jcommander.Parameter;



/**
 * Represents the allowed command-line parameter for all applications within
 * the ffw-alertsystem. Other parameters (specific for the applications) must
 * be defined and implemented with the xml-configuration files.<br>
 * 
 * @see @ApplicationConfig
 */
public class CommandLineParams {
  
  @Parameter(names = "-config", description = "The XML-config-file")
  public String configFile = "config.xml";
  
  @Parameter(names = "-logInFile", description = "Log output in file")
  public boolean logInFile = false;
  
  @Parameter(names = "-logLevel", description = "Level of logging (1-5)")
  public Integer logLevel = 5;
  
}