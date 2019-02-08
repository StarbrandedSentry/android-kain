package com.oddlid.karinderya;

public class EntryUpload {
    private String image_url;

    public EntryUpload()
    {}

    public EntryUpload(String url)
    {
        this.image_url = url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
