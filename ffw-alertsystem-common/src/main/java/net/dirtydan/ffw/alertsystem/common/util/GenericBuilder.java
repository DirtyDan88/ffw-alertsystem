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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;



public class GenericBuilder<T> {
  
  private final Supplier<T> _instantiator;
  
  private List<Consumer<T>> _instanceModifiers = new ArrayList<>();
  
  
  
  public GenericBuilder(Supplier<T> instantiator) {
    _instantiator = instantiator;
  }
  
  public static <T> GenericBuilder<T> of(Supplier<T> instantiator) {
    return new GenericBuilder<T>(instantiator);
  }
  
  public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
    Consumer<T> c = instance -> consumer.accept(instance, value);
    _instanceModifiers.add(c);
    return this;
  }
  
  public T build() {
    T value = _instantiator.get();
    _instanceModifiers.forEach(modifier -> modifier.accept(value));
    _instanceModifiers.clear();
    return value;
  }
  
}