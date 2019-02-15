package com.oddlid.karinderya;

public class MenuItem {
    public MenuItem()
    {

    }

    public MenuItem(String name, String url,String availability)
    {
        if(name.trim().equals(""))
        {
            name = "n/a";
        }

        this.name = name;
        this.image_url = url;
        this.availability = availability;
    }
    private String availability, name, image_url;

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
