package com.romanobori.commands;

public class LimitOrderDetails {
    String orderId;
    Double price;

    public LimitOrderDetails(String orderId, Double price) {
        this.orderId = orderId;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }
    public Double getPrice() {
        return price;
    }
}
