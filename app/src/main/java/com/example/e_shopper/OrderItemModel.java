package com.example.e_shopper;

public class OrderItemModel {
    private String productName;
    private int quantity;
    private double price;
    private String productImageUrl;

    // Empty constructor required for Firebase
    public OrderItemModel() {
    }

    public OrderItemModel(String productName, int quantity, double price, String productImageUrl) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.productImageUrl = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
}
