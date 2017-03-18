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

package net.dirtydan.ffw.alertsystem.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.Test;

import net.dirtydan.ffw.alertsystem.common.util.CollectionHelper.DynamicListSize;


public class CollectionHelperTest {
  
  private static class DynamicListSizeImpl implements DynamicListSize {
    int is;
    @Override
    public int get() { return is; }
  }
  
  @Test
  public void testDynamicLimitedList() {
    // GIVEN
    DynamicListSizeImpl dynamicListSize = new DynamicListSizeImpl();
    List<String> dynamicLimitedList = CollectionHelper.getDynamicLimitedList(dynamicListSize);
    
    // WHEN
    dynamicListSize.is = 2;
    dynamicLimitedList.add("String_0");
    dynamicLimitedList.add("String_1");
    dynamicLimitedList.add("String_2");
    
    // THEN
    assertThat(dynamicLimitedList.get(0)).isEqualTo("String_1");
    assertThat(dynamicLimitedList.get(1)).isEqualTo("String_2");
    assertThatExceptionOfType(IndexOutOfBoundsException.class)
      .isThrownBy(() -> { dynamicLimitedList.get(2); });
    
    // WHEN
    dynamicListSize.is = 3;
    dynamicLimitedList.add("String_3");
    
    // THEN
    assertThat(dynamicLimitedList.get(2)).isEqualTo("String_3");
    assertThatExceptionOfType(IndexOutOfBoundsException.class)
      .isThrownBy(() -> { dynamicLimitedList.get(3); });
  }
  
}
