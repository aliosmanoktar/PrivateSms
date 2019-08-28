package com.aliosman.privatesms;

public class Contact {
    private String name;
    private String number;
    private int ID;

    public Contact setName(String name) {
        this.name = name;
        return this;
    }

    public Contact setNumber(String number) {
        this.number = number;
        return this;
    }

    public Contact setID(int ID) {
        this.ID = ID;
        return this;
    }

    public String GetShowName(){
        if (name!=null)
            return name;
        else
            return number;
    }
}
