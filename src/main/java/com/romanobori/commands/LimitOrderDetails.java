package com.romanobori.commands;

public class LimitOrderDetails {
    private String orderId;
    private Double price;
    private Double amount;

    public LimitOrderDetails(String orderId, Double price, Double amount) {
        this.orderId = orderId;
        this.price = price;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }
    public Double getPrice() {
        return price;
    }

    public Double getAmount() {
        return amount;
    }
}
