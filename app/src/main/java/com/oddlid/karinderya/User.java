package com.oddlid.karinderya;

public class User {
    public User()
    {}
    public User(String name, int user_level)
    {
        this.name = name;
        this.user_level = user_level;
    }

    private String name;
    private int user_level;

    //SETTERS AND GETTERS LOG: name, user_level
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }
}
