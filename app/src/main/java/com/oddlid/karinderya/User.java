package com.oddlid.karinderya;

public class User {
    public User()
    {}
    public User(String name, int user_level)
    {
        this.name = name;
        this.user_level = user_level;
        this.store_count = 0;
    }

    private String name;
    private int user_level, store_count;

    //SETTERS AND GETTERS LOG: name, user_level


    public int getStore_count() {
        return store_count;
    }

    public void setStore_count(int store_count) {
        this.store_count = store_count;
    }

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
