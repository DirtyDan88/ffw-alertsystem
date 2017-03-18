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

import java.util.LinkedList;
import java.util.List;


public final class CollectionHelper {
  
  private CollectionHelper() { /* static class, prevent instantiation */ }
  
  
  public static <T> List<T> getDynamicLimitedList(DynamicListSize listSize) {
    return new LinkedList<T>() {
      private static final long serialVersionUID = 1L;
      
      @Override
      public boolean add(T object) {
        if (listSize.get() <= 0) {
          return false;
        }
        
        if (size() >= listSize.get()) {
          super.removeFirst();
        }
        
        return super.add(object);
      }
    };
  }
  
  public interface DynamicListSize {
    int get();
  }
  
}
