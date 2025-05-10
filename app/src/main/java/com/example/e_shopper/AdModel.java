package com.example.e_shopper;

public class AdModel {
    private String imageUrl;
    private String link;

    public AdModel() {
        // Default constructor required for calls to DataSnapshot.getValue(AdModel.class)
    }

    public AdModel(String imageUrl, String link) {
        this.imageUrl = imageUrl;
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

