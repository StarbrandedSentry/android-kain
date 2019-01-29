package com.oddlid.karinderya;

public class ViewDatabase {
    private String name;
    private int user_level;

    public ViewDatabase()
    {
        //empty constructor
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
