package com.oddlid.karinderya.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private LatLng position;
    private int iconPicture;
    private String snippet;
    private String title;

    public ClusterMarker(LatLng position, int iconPicture, String snippet, String title) {
        this.position = position;
        this.iconPicture = iconPicture;
        this.snippet = snippet;
        this.title = title;
    }

    public ClusterMarker()
    {}

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
