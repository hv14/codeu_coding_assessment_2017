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

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.IOException;
import java.util.HashMap;

final class MyJSONParser implements JSONParser {
  private HashMap<String, Integer> keyGroups = new HashMap<>();
  private final String START_OBJ = "{\"";
  private final String END_OBJ = "\"}";
  private final String TRANSITION = "\":\"";
  private final String NEW_STRING = "\",\"";


  @Override
  public JSON parse(String in) throws IOException {
    in = formatString(in);
    MyJSON myJSON = new MyJSON();
    int startKeyIndex = 0;
    int endKeyIndex = 0;
    int startValueIndex = 0;
    int endValueIndex = 0;
    int currInIndex = 0;
    while (currInIndex < in.length()) {
      if (isKeyGrouping(in, currInIndex)) {
        String keyGroup = getKeyGrouping(in, currInIndex);
        Integer numKeyGroup = keyGroups.get(keyGroup);
        keyGroups.put(keyGroup, numKeyGroup++);

        if (keyGroup.equals(START_OBJ)) {
          startKeyIndex = currInIndex + 2;
          currInIndex = currInIndex  + 2;
        }
        else if (keyGroup.equals(NEW_STRING)) {
          startKeyIndex = currInIndex + 3;
          endValueIndex = currInIndex;
          currInIndex = currInIndex + 3;
        }
        else if (keyGroup.equals(END_OBJ)) {
          endValueIndex = currInIndex;
          currInIndex = currInIndex + 2;
        }
        else if (keyGroup.equals(TRANSITION)) {
          endKeyIndex = currInIndex;
          startValueIndex = currInIndex + 3;
          currInIndex = currInIndex + 3;
        }




      }


    }

    return new MyJSON();
  }

  private String formatString(String unformattedString) {
    unformattedString = unformattedString.trim();
    unformattedString = unformattedString.replaceAll("\\s", "");
    System.out.print(unformattedString);
    return unformattedString;
  }

  private boolean isKeyGrouping(String in, int i) {
    if (in.substring(i, i + 2).equalsIgnoreCase(START_OBJ) ||
            in.substring(i, i + 2).equalsIgnoreCase(END_OBJ) ||
            in.substring(i, i + 3).equalsIgnoreCase(TRANSITION) ||
            in.substring(i, i + 3).equalsIgnoreCase(NEW_STRING)) {
      return true;
    }

    return false;
  }

  private String getKeyGrouping(String in, int i) {
    if (in.substring(i, i + 2).equalsIgnoreCase(START_OBJ)) {
      return START_OBJ;
    }
    else if (in.substring(i, i + 2).equalsIgnoreCase(END_OBJ)) {
      return END_OBJ;
    }
    else if (in.substring(i, i + 3).equalsIgnoreCase(TRANSITION)) {
      return TRANSITION;
    }
    else if (in.substring(i, i + 3).equalsIgnoreCase(NEW_STRING)) {
      return NEW_STRING;
    }
    else {
      return "";
    }
  }



}
