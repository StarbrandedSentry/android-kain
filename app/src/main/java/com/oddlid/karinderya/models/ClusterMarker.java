package com.oddlid.karinderya.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private LatLng position;
    private String iconPicture;
    private String snippet;
    private String title;
    private String iden;

    public ClusterMarker(LatLng position, String iconPicture, String snippet, String title, String id) {
        this.position = position;
        this.iconPicture = iconPicture;
        this.snippet = snippet;
        this.title = title;
        this.iden = id;
    }

    public ClusterMarker()
    {}

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(String iconPicture) {
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
