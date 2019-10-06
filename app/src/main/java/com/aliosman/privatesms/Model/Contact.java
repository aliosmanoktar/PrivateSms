/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import java.io.Serializable;

public class Contact implements Serializable {
    private int ID;
    private String number;
    private String name;
    private String lookupKey;

    public Contact setID(int ID) {
        this.ID = ID;
        return this;
    }

    public Contact setNumber(String number) {
        this.number = number;
        return this;
    }

    public Contact setName(String name) {
        this.name = name;
        return this;
    }

    public Contact setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
        return this;
    }

    public int getID() {
        return ID;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getNameText() {
        return (name == null || name.isEmpty()) ? number : name;
    }

    public String getLookupKey() {
        return lookupKey;
    }
}
