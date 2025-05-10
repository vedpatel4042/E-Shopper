package com.example.e_shopper;

public class OrderModel {
    private String orderId;
    private String date;
    private double totalAmount;
    private String paymentMethod;
    private String shippingAddress;

    // Empty constructor required for Firebase
    public OrderModel() {
    }

    public OrderModel(String orderId, String date, double totalAmount, String paymentMethod, String shippingAddress) {
        this.orderId = orderId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
