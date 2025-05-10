package com.example.e_shopper;


public class CategoryModel {
    private String id;
    private String name;
    private String iconUrl;
    private int getPrice;
    // Change this to match your database

    // Default constructor (required for Firebase)
    public CategoryModel() {}

    public CategoryModel(String id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getPrice() {
        return getPrice;
    }
}


