package com.oddlid.karinderya;

public class Promo {
    public Promo()
    {}

    public Promo(String store_id,String store_name , String name, String description, String type, String time_type)
    {
        this.store_id = store_id;
        this.store_name = store_name;
        this.name = name;
        this.description = description;
        this.type = type;
        this.time_type = time_type;
    }

    public Promo(String store_id,String store_name ,String name, String description, String type, String time_type, String time_from, String time_to)
    {
        this.store_id = store_id;
        this.store_name = store_name;
        this.name = name;
        this.description = description;
        this.type = type;
        this.time_type = time_type;
        this.time_from = time_from;
        this.time_to = time_to;
    }

    String store_id, store_name;
    String name, description, type, time_type, time_from, time_to;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime_type() {
        return time_type;
    }

    public void setTime_type(String time_type) {
        this.time_type = time_type;
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
}
