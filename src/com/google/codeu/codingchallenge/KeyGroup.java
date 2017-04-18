package com.google.codeu.codingchallenge;

import java.security.Key;

/**
 * The purpose of this class is to represent a KeyGroup. A KeyGroup is a series of characters that represent something
 * meaningful in a JSON-lite object. Each group points to another KeyGroup which should one that is valid to come
 * after it. For Example an START_OBJ KeyGroup cannot be followed by a NEW_KEY_VALUE_PAIR.
 * The current KeyGroups are...
 * START_OBJ = " {" " Series of characters that represent a new object starting
 * END_OBJ = " "} " Series of characters that represent an object ending
 * TRANSITION_TO_STRING = " ":" " Series of characters that represent a transition from a key to a string value
 * TRANSITION_TO_OBJ = " ":{ " Series of characters that represents a transition from a key to an object value
 * NEW_KEY_VALUE_PAIR = " "," " Series of characters that represents a new key value pair starting
 * NEW_ITEM_AFTER_OBJ = " "}," " Series of characters that represents a new item after an object
 * Created by Himani Vyas on 4/17/17.
 */
public class KeyGroup {
    private String keyGroup;
    private int index;
    private KeyGroup nextKeyGroup;

    public KeyGroup(String group, int index) {
        keyGroup = group;
        this.index = index;
        nextKeyGroup = null;
    }

    /**
     *
     * @return String
     */
    public String getKeyGroup() {
        return keyGroup;
    }

    /**
     *
     * @return int
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index int
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     *
     * @return KeyGroup
     */
    public KeyGroup getNextKeyGroup() {
        return nextKeyGroup;
    }

    /**
     *
     * @param next KeyGroup
     */
    public void setNextKeyGroup(KeyGroup next) {
        nextKeyGroup = next;
    }
}
