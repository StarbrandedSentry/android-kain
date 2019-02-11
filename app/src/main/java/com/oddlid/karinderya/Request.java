package com.oddlid.karinderya;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Request {
    public Request()
    {}

    public Request(String status)
    {
        this.status = status;
    }

    public Request(String name, String uid, String location, String date_made, String status)
    {
        this.name = name;
        this.uid = uid;
        this.location = location;
        this.date_made = date_made;
        this.status = status;
    }

    private String name, uid, location;
    private String date_made;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

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

    @Exclude
    public Map<String, Object> statusToMap()
    {
        HashMap<String, Object> status = new HashMap<>();
        status.put("denied", status);

        return status;
    }

}
