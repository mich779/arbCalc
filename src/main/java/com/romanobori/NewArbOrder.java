package com.romanobori;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;

import java.sql.Timestamp;
import java.util.Objects;

public abstract class NewArbOrder {

    String symbol;
    ARBTradeAction action;
    double quantity;

    public NewArbOrder() {
    }

    public NewArbOrder(String symbol, ARBTradeAction action, double quantity) {
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public ARBTradeAction getAction() {
        return action;
    }

    public double getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewArbOrder)) return false;

        NewArbOrder that = (NewArbOrder) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                Objects.equals(symbol, that.symbol) &&
                action == that.action;
    }

    @Override
    public int hashCode() {

        return Objects.hash(symbol, action, quantity);
    }
}
