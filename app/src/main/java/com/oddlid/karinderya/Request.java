package com.oddlid.karinderya;

public class Request {
    public Request()
    {}


    public Request(String name, String uid, String location, String date_made)
    {
        this.name = name;
        this.uid = uid;
        this.location = location;
        this.date_made = date_made;
    }

    private String name, uid, location;
    private String date_made;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate_made() {
        return date_made;
    }

    public void setDate_made(String date_made) {
        this.date_made = date_made;
    }
}
