package com.romanobori;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;

import java.sql.Timestamp;

public class NewArbOrder {

    String symbol;
    ARBTradeAction action;
    double quantity;
    double price;

    public NewArbOrder(String symbol, ARBTradeAction action, double quantity, double price) {
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewArbOrder that = (NewArbOrder) o;

        if (Double.compare(that.quantity, quantity) != 0) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) return false;
        return action == that.action;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        temp = Double.doubleToLongBits(quantity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
