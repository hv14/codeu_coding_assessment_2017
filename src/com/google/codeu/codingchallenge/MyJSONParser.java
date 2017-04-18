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
import java.security.Key;
import java.util.HashMap;
import java.util.SplittableRandom;

/**
 * purpose of this class is to implement the JSONParser interface. Overrides the parse method
 * Created by Himani Vyas
 */
final class MyJSONParser implements JSONParser {
  //HashMap to keep track of opening and closing braces so we can make sure there are the same amount of opening braces
  //as closing braces
  private HashMap<Character, Integer> braces;

  //Key Group identifiers. For more info on Key Group look at KeyGroup class
  private final String START_OBJ = "{\""; //Series of characters that represent a new object starting
  private final String END_OBJ = "\"}"; //Series of characters that represent an object ending
  private final String TRANSITION_TO_STRING = "\":\""; //Series of characters that represent a transition from a key to a string value
  private final String TRANSITION_TO_OBJ = "\":{"; //Series of characters that represents a transition from a key to an object value
  private final String NEW_KEY_VALUE_PAIR = "\",\""; //Series of characters that represents a new key value pair starting
  private final String NEW_ITEM_AFTER_OBJ = "\"},\""; //Series of characters that represents a new item after an object

  private final String GENERIC_ERROR_MSG = "Invalid JSON-lite object";

  /**
   * Method overriden from interface. The purpose of this method is to parse the given string, see if it is a JSON-lite
   * object and if it then input its data into a JSON object.
   * @param in String (represents the potential valid JSON-lite object)
   * @return JSON (an object containing the data from in if it was a valid JSON-lite object
   * @throws IOException if in is not valid JSON-lite object then throw IOException
   */
  @Override
  public JSON parse(String in) throws IOException {
    in = formatString(in);
    KeyGroup jsonKeyGroup = null;
    if (in.equalsIgnoreCase("{}")) {
      return new MyJSON();
    }
    if (in.substring(0, 2).equalsIgnoreCase(START_OBJ) && checkForEscapedCharacters(in)) {
      braces = new HashMap<>();
      initBraces(in);
      jsonKeyGroup = createLinkedList(in, new KeyGroup(START_OBJ, 2), 2);
      MyJSON myJSON = new MyJSON();
      if (checkLinkedList(jsonKeyGroup, in)) {
        myJSON = createJSON(jsonKeyGroup, in, myJSON);
      }
      else {
        throw new IOException(GENERIC_ERROR_MSG);
      }

      return myJSON;
    }
    else {
      throw new IOException(GENERIC_ERROR_MSG);
    }
  }

  /**
   * Purpose of this method is to format the string so it doesn't include any spaces.
   * TODO: This method makes the problem easier but potential bug when a key or value in the JSON-lite object has a
   * TODO: space in it. Figure out a way to have code work while eliminating this work around
   * @param unformattedString String to format
   * @return String with no spaces or whitespace
   */
  private String formatString(String unformattedString) {
    unformattedString = unformattedString.trim();
    unformattedString = unformattedString.replaceAll("\\s", "");
    System.out.print(unformattedString);
    return unformattedString;
  }

  /**
   * purpse of this method is to check if there are any escaped characters in the string and if there are are they
   * correctly formatted i.e  escapable characters are after a \
   * @param in String that represents JSON-lite object
   * @return boolean
   */
  private boolean checkForEscapedCharacters(String in) {
    int i = 0;
    while (i < in.length()) {
      if (isKeyGrouping(in, i)) {
        i = getKeyGrouping(in, i).getIndex();
      }
      else {
        if (in.charAt(i) == '"') {
          return false;
        }
        else if (in.charAt(i) == '\\') {
          if (!validEscapedCharacter(in, i)) {
            return false;
          }
        }
        i = i + 1;
      }
    }

    return true;
  }

  /**
   * helper method for checkForEscapedCharacters. The purpose of this method is to see if each \ is followed by an
   * escapable character
   * @param in String that represents JSON-lite object
   * @param index int
   * @return boolean
   */
  private boolean validEscapedCharacter(String in, int index) {
    if (in.charAt(index) == '\\') {
      if (in.charAt(index + 1) == '\\' || in.charAt(index + 1) == 'n' || in.charAt(index + 1) == 't' ||
              in.charAt(index + 1) == '"') {
        return true;
      }
      else {
        return false;
      }
    }

    return false;
  }

  /**
   * purpose of this method is to populate the braces hashmap with how many { and } there are
   * @param in String
   */
  private void initBraces(String in) {
    for (Character currChar : in.toCharArray()) {
      if (currChar =='{' || currChar == '}') {
        if (braces.size() == 0 || braces.get(currChar) == null) {
          braces.put(currChar, 1);
        }
        else {
          braces.put(currChar, braces.get(currChar) + 1);
        }
      }
    }
  }

  /**
   * the purpose of this method is recursively create a linked list of KeyGroup objects.
   * @param in String
   * @param current KeyGroup
   * @param startIndex int
   * @return KeyGroup
   */
  private KeyGroup createLinkedList(String in, KeyGroup current, int startIndex) {
    //Base Case: if our index + 2 is greater than the length of our string. We add 2 because some of the helper methods
    //get in.substring(index, index + 2) so index + 2 cannot be greater than in.length() otherwise an exception is thrown
    if (startIndex + 2 > in.length()) {
      return null;
    }

    //Recursive Case: If we have found a KeyGroup object then make our current's next equal to that object. Then
    //recursively create current's next by passing in the right parameters to this method
    if (isKeyGrouping(in, startIndex)) {
      current.setNextKeyGroup(getKeyGrouping(in, startIndex));

      //current.getNextKeygroup().getIndex() is the index of the character right after the keyGroup object ends
      //For example: in = "{"name":"sam"}" current = "{"" current.next = " ":" " current.next.index = 9
      createLinkedList(in, current.getNextKeyGroup(), current.getNextKeyGroup().getIndex());
    }
    //Other Recursive Case: We are at a character that does not have any value for us so just look at the next character
    else {
      createLinkedList(in, current, startIndex + 1);
    }

    return current;
  }

  /**
   * Helper method to see if the next series of characters form a KeyGroup
   * @param in String
   * @param i int
   * @return boolean
   */
  private boolean isKeyGrouping(String in, int i) {
    if (i + 4 <= in.length() && in.substring(i, i + 4).equalsIgnoreCase(NEW_ITEM_AFTER_OBJ) ||
            i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(TRANSITION_TO_STRING) ||
            i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(TRANSITION_TO_OBJ) ||
            i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(NEW_KEY_VALUE_PAIR) ||
            i + 2 <= in.length() && in.substring(i, i + 2).equalsIgnoreCase(START_OBJ) ||
            i + 2 <= in.length() && in.substring(i, i + 2).equalsIgnoreCase(END_OBJ)) {
      return true;
    }

    return false;
  }

  /**
   * Helper method to retrieve a KeyGroup from the given string. One thing to note is if the KeyGroup is a
   * TRANSITION_TO_OBJ then the index of the KeyGroup is purposely set to be off by one so the next KeyGroup can be a
   * START_OBJ
   * @param in String
   * @param i int
   * @return KeyGroup
   */
  private KeyGroup getKeyGrouping(String in, int i) {
    if (i + 4 <= in.length() && in.substring(i, i + 4).equalsIgnoreCase(NEW_ITEM_AFTER_OBJ)) {
      return new KeyGroup(NEW_ITEM_AFTER_OBJ, i + 4);
    }
    else if (i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(TRANSITION_TO_STRING)) {
      return new KeyGroup(TRANSITION_TO_STRING, i + 3);
    }
    else if (i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(TRANSITION_TO_OBJ)) {
      return new KeyGroup(TRANSITION_TO_OBJ, i + 2);
    }
    else if (i + 3 <= in.length() && in.substring(i, i + 3).equalsIgnoreCase(NEW_KEY_VALUE_PAIR)) {
      return new KeyGroup(NEW_KEY_VALUE_PAIR, i + 3);
    }
    else if (i + 2 <= in.length() && in.substring(i, i + 2).equalsIgnoreCase(START_OBJ)) {
      return new KeyGroup(START_OBJ, i + 2);
    }
    else if (i + 2 <= in.length() && in.substring(i, i + 2).equalsIgnoreCase(END_OBJ)) {
      return new KeyGroup(END_OBJ, i + 2);
    }
    else {
      return null;
    }
  }

  /**
   * purpose of this method is to check the linked list of Key Groups and see if the order is a valid order
   * @param current KeyGroup
   * @param in String
   * @return boolean
   */
  private boolean checkLinkedList(KeyGroup current, String in) {
    while (current.getNextKeyGroup() != null) {
      KeyGroup next = current.getNextKeyGroup();

      //A START_OBJ and NEW_ITEM_AFTER_OBJ can only be followed by a TRANSITION_TO_OBJ or TRANSITION_TO_STRING
      if (current.getKeyGroup().equalsIgnoreCase(START_OBJ) ||
              current.getKeyGroup().equalsIgnoreCase(NEW_ITEM_AFTER_OBJ)) {
        if ( !next.getKeyGroup().equalsIgnoreCase(TRANSITION_TO_OBJ) &&
                !next.getKeyGroup().equalsIgnoreCase(TRANSITION_TO_STRING) ) {
          return false;
        }
      }
      //A TRANSITION_TO_STRING can only be followed an END_OBJ or NEW_KEY_VALUE_PAIR. Unless there are objects within
      //the JSON-lite (This is indicated by there being more than 1 opening braces) In that case it can be followed by
      //a NEW_ITEM_AFTER_OBJ
      else if (current.getKeyGroup().equalsIgnoreCase(TRANSITION_TO_STRING)) {
        if (braces.get('{') > 1) {
          if ( !next.getKeyGroup().equalsIgnoreCase(END_OBJ) &&
                  !next.getKeyGroup().equalsIgnoreCase(NEW_ITEM_AFTER_OBJ) &&
                  !next.getKeyGroup().equalsIgnoreCase(NEW_KEY_VALUE_PAIR)) {
            return false;
          }
        }
        else if ( !next.getKeyGroup().equalsIgnoreCase(END_OBJ) &&
                !next.getKeyGroup().equalsIgnoreCase(NEW_KEY_VALUE_PAIR) ) {
          return false;
        }
      }
      //A TRANSITION_TO_OBJ can only be followed by a START_OBJ
      else if (current.getKeyGroup().equalsIgnoreCase(TRANSITION_TO_OBJ)) {
        if (!next.getKeyGroup().equalsIgnoreCase(START_OBJ)) {
          return false;
        }
      }
      //A NEW_KEY_VALUE_PAIR can only be followed by a TRANSITION_TO_STRING obj
      else if (current.getKeyGroup().equalsIgnoreCase(NEW_KEY_VALUE_PAIR)) {
        if (!next.getKeyGroup().equalsIgnoreCase(TRANSITION_TO_STRING)) {
          return false;
        }
      }
      //A END_OBJ is either the last thing in the JSON-lite object or it is only followed by closing braces
      else if (current.getKeyGroup().equalsIgnoreCase(END_OBJ)) {
        if (current.getIndex() != in.length() || in.charAt(current.getIndex() + 1) != '}') {
          return false;
        }
      }
      else {
        return false;
      }

      current = next;
    }

    //Check to see the last elements are only closing braces
    if (current.getKeyGroup().equalsIgnoreCase(END_OBJ) &&
            checkBraces() &&
            checkRestAreBraces(in, current.getIndex() + 1)) {
      return true;
    }
    else {
      return false;
    }

  }

  /**
   * helper method to see if there are same number of opening braces as there are closing braces
   * @return boolean
   */
  private boolean checkBraces() {
    return braces.get('{') == braces.get('}');
  }

  /**
   * helper method to see if characters after index are all closing braces
   * @param in String
   * @param index int
   * @return boolean
   */
  private boolean checkRestAreBraces(String in, int index) {
    for (int i = index; i < in.length(); i++) {
      if (in.charAt(i) != '{' || in.charAt(i) != '}') {
        return false;
      }
    }

    return true;
  }

  /**
   * the purpose of this method is to recurse through the validated linked list of KeyGroup objects and make a JSON
   * object coresponding to the data
   * @param current KeyGroup
   * @param in String
   * @param myJSON MyJSON
   * @return MyJSON
   */
  private MyJSON createJSON(KeyGroup current, String in, MyJSON myJSON) {
    //Base Case: We are are an END_OBJ
    if (current.getKeyGroup().equalsIgnoreCase(END_OBJ)) {
      return myJSON;
    }
    //Recursive Case: Input a key value pair into a MyJSON object depending on if its a string key Value pair or
    // an object key Value pair
    if (current.getKeyGroup().equalsIgnoreCase(START_OBJ) || current.getKeyGroup().equalsIgnoreCase(NEW_KEY_VALUE_PAIR) ||
            current.getKeyGroup().equalsIgnoreCase(NEW_ITEM_AFTER_OBJ)) {
      int keyStart = current.getIndex();
      int keyEnd = getEndingIndex(current);
      if (current.getNextKeyGroup().getKeyGroup().equalsIgnoreCase(TRANSITION_TO_STRING)) {
        int valStart = current.getNextKeyGroup().getIndex();
        int valEnd = getEndingIndex(current.getNextKeyGroup());
        myJSON.setString(in.substring(keyStart, keyEnd), in.substring(valStart, valEnd));

        return createJSON(current.getNextKeyGroup(), in, myJSON);
      }
      //if key value pair is an object key value pair then create another MyJSON object and call createJSON using that
      //MyJSON object. Then after the new MyJSON object is created, insert it into the original MyJSON object
      else if (current.getNextKeyGroup().getKeyGroup().equalsIgnoreCase(TRANSITION_TO_OBJ)) {
        MyJSON newJson = new MyJSON();
        newJson = createJSON(current.getNextKeyGroup(), in, newJson);
        myJSON.setObject(in.substring(keyStart, keyEnd), newJson);

        return createJSON(current.getNextKeyGroup(), in, myJSON);
      }
    }

    return createJSON(current.getNextKeyGroup(), in, myJSON);
  }

  /**
   * helper method to get the ending indices for the names of the keys and values. There is an off by one error for
   * TRANSITION_TO_OBJ KeyGroup objects so just add one for them
   * @param current KeyGroup
   * @return int
   */
  private int getEndingIndex(KeyGroup current) {
    if (current.getNextKeyGroup().getKeyGroup().equalsIgnoreCase(TRANSITION_TO_OBJ)) {
      return current.getNextKeyGroup().getIndex() - current.getNextKeyGroup().getKeyGroup().length() + 1;
    }

    return current.getNextKeyGroup().getIndex() - current.getNextKeyGroup().getKeyGroup().length();
  }













}
