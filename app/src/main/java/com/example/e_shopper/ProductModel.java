package com.example.e_shopper;

import java.util.List;

public class ProductModel {
    private String id;
    private String name;
    private String description;
    private double price;
    private double rating;
    private String category;
    private String brand;
    private List<String> imageUrls;
    private List<String> specifications;
    private int quantity;

    public ProductModel() {
        // Default constructor required for Firebase
    }

    // ✅ Fix: Add a new constructor for (id, name, price)
    public ProductModel(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductModel(String id, String name, String description, double price, double rating,
                        String category, String brand, List<String> imageUrls, List<String> specifications, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.category = category;
        this.brand = brand;
        this.imageUrls = imageUrls;
        this.specifications = specifications;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public List<String> getImageUrls() { return imageUrls; }
    public List<String> getSpecifications() { return specifications; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setSpecifications(List<String> specifications) { this.specifications = specifications; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }
}


//package com.example.e_shopper;
//
//import java.util.List;
//
//public class ProductModel {
//    private String id;
//    private String name;
//    private String description;
//    private double price;
//    private double rating;
//    private String category;
//    private String brand;
//    private List<String> imageUrls;
//    private List<String> specifications;
//
//    public ProductModel() {
//        // Default constructor required for Firebase
//    }
//
//    public ProductModel(String id, String name, String description, double price, double rating,
//                        String category, String brand, List<String> imageUrls, List<String> specifications) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.price = price;
//        this.rating = rating;
//        this.category = category;
//        this.brand = brand;
//        this.imageUrls = imageUrls;
//        this.specifications = specifications;
//    }
//
//    // Getters
//    public String getId() { return id; }
//    public String getName() { return name; }
//    public String getDescription() { return description; }
//    public double getPrice() { return price; }
//    public double getRating() { return rating; }
//    public String getCategory() { return category; }
//    public String getBrand() { return brand; }
//    public List<String> getImageUrls() { return imageUrls; }
//    public List<String> getSpecifications() { return specifications; }
//
//    // Setters
//    public void setId(String id) { this.id = id; }
//    public void setName(String name) { this.name = name; }
//    public void setDescription(String description) { this.description = description; }
//    public void setPrice(double price) { this.price = price; }
//    public void setRating(double rating) { this.rating = rating; }
//    public void setCategory(String category) { this.category = category; }
//    public void setBrand(String brand) { this.brand = brand; }
//    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
//    public void setSpecifications(List<String> specifications) { this.specifications = specifications; }
//
//    public String getImageUrl() {
//        if (imageUrls != null && !imageUrls.isEmpty()) {
//            return imageUrls.get(0);
//        }
//        return null;
//    }
//}