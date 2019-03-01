package com.oddlid.karinderya;

import com.google.firebase.firestore.GeoPoint;

public class StoreLocation {
    public StoreLocation()
    {

    }

    public StoreLocation(String id, GeoPoint geo_location, String name, String image)
    {
        this.id = id;
        this.geo_location = geo_location;
        this.name = name;
        this.image = image;
    }

    private String id, name, image;
    private GeoPoint geo_location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoPoint getGeo_location() {
        return geo_location;
    }

    public void setGeo_location(GeoPoint geo_location) {
        this.geo_location = geo_location;
    }
}
