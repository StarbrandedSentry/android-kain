package com.oddlid.karinderya;

public class Store {
    public Store(String id, String uid, String name, String location)
    {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.location = location;
    }

    private String id, uid, name, location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
