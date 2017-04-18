// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.lang.reflect.Array;
import java.util.*;

final class MyJSON implements JSON {

  private HashMap<String, String> strings = new HashMap<>();
  private HashMap<String, JSON> objects = new HashMap<>();

  @Override
  public JSON getObject(String name) {
    return objects.get(name);
  }

  @Override
  public JSON setObject(String name, JSON value) {
    objects.put(name, value);
    return this;
  }

  @Override
  public String getString(String name) {
    return strings.get(name);
  }

  @Override
  public JSON setString(String name, String value) {
    strings.put(name, value);
    return this;
  }

  @Override
  public void getObjects(Collection<String> names) {
    Iterator it = objects.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      names.add(pair.getKey().toString());
    }
  }

  @Override
  public void getStrings(Collection<String> names) {
    Iterator it = strings.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      names.add(pair.getKey().toString());
    }  }
}
