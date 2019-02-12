package com.oddlid.karinderya;

public class Upload {
    public Upload(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String name, url;

    public Upload(String name, String url)
    {
        if(name.trim().equals(""))
        {
            name = "No Name";
        }
        this.name = name;
        this.url = url;
    }

    public Upload(String url)
    {
        this.url = url;
    }
}
