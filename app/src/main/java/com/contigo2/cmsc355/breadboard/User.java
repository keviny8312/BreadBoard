package com.contigo2.cmsc355.breadboard;

public class User {
    private String name, email, group;
    private int fontSize;

    public User() {                            // user object, stores name, email, group, and font size
        name = null;
        email = null;
        group = null;
        fontSize = 12;
    }

    public User(String n, String e, String g) {
        name = n;
        email = e;
        group = g;
        fontSize = 12;
    }

    public User(String n, String e, String g, int f) {
        name = n;
        email = e;
        group = g;
        fontSize = f;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGroup() {
        return group;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setName(String n) {
        name = n;
    }

    public void setEmail(String e) {
        email = e;
    }

    public void setGroup(String g) {
        group = g;
    }

    public void setFontSize(int f) {
        fontSize = f;
    }

}
