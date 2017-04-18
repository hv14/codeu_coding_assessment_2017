package com.google.codeu.codingchallenge;

import java.security.Key;

/**
 * Created by himonal on 4/17/17.
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

    public String getKeyGroup() {
        return keyGroup;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public KeyGroup getNextKeyGroup() {
        return nextKeyGroup;
    }

    public void setNextKeyGroup(KeyGroup next) {
        nextKeyGroup = next;
    }
}
