package com.romanobori;

import java.util.List;
import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyArbOrder that = (MyArbOrder) o;
        return Double.compare(that.price, price) == 0 &&
                Double.compare(that.origQuantity, origQuantity) == 0 &&
                Double.compare(that.executedQuantity, executedQuantity) == 0 &&
                time == that.time &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(orderId, that.orderId) &&
                action == that.action;
    }

    @Override
    public int hashCode() {

        return Objects.hash(symbol, orderId, price, origQuantity, executedQuantity, action, time);
    }
}
