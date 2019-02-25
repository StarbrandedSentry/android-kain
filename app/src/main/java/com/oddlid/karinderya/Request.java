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

    public Request(String name, String uid, String date_made, String street_address, String city, String contact_number
                   , String capacity, String date_started, String status, String operating_hours, String time_from, String time_to)
    {
        this.name = name;
        this.uid = uid;
        this.date_made = date_made;
        this.street_address = street_address;
        this.city = city;
        this.contact_number = contact_number;
        this.capacity = capacity;
        this.date_started = date_started;
        this.status = status;
        this.operating_hours = operating_hours;
        this.time_from = time_from;
        this.time_to = time_to;
    }

    public Request(String name, String uid, String date_made, String street_address, String city, String contact_number
            , String capacity, String date_started, String status, String operating_hours)
    {
        this.name = name;
        this.uid = uid;
        this.date_made = date_made;
        this.street_address = street_address;
        this.city = city;
        this.contact_number = contact_number;
        this.capacity = capacity;
        this.date_started = date_started;
        this.status = status;
        this.operating_hours = operating_hours;
    }

    private String name, uid, street_address, city, contact_number, capacity, date_started, operating_hours, time_from, time_to;
    private String date_made;

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDate_started() {
        return date_started;
    }

    public void setDate_started(String date_started) {
        this.date_started = date_started;
    }

    public String getOperating_hours() {
        return operating_hours;
    }

    public void setOperating_hours(String operating_hours) {
        this.operating_hours = operating_hours;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

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
