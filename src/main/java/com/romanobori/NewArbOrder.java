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
}
