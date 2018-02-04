package com.romanobori;

import java.util.List;

public class MyArbOrder {

    String symbol;
    String orderId;
    double price;
    double origQuantity;
    double executedQuantity;
    ARBTradeAction action;
    long time;

    public MyArbOrder(String symbol, String orderId, double price, double origQuantity, double executedQuantity, ARBTradeAction action, long time) {
        this.symbol = symbol;
        this.orderId = orderId;
        this.price = price;
        this.origQuantity = origQuantity;
        this.executedQuantity = executedQuantity;
        this.action = action;
        this.time = time;
    }
}
