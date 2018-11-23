package com.example.elien.sym_lab2;

import java.io.Serializable;

class Data implements Serializable {

    private static final long serialVersionUID = -7676157349813018600L;

    private String name;
    private String surname;
    private boolean ismajor;
    private float mark;

    public Data(String name, String surname, boolean ismajor, float mark) {
        this.name = name;
        this.surname = surname;
        this.ismajor = ismajor;
        this.mark = mark;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean getIsmajor() {
        return ismajor;
    }
    public void setIsmajor(boolean isvegan) {
        this.ismajor = isvegan;
    }

    public float getMark() {
        return mark;
    }
    public void setMark(float mark) {
        this.mark = mark;
    }
}
