package com.romanobori;

import support.BinanceApiWebSocketClientStub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApiClientStub extends ApiClient {

    ArbOrders orders;

    NewArbOrder order;

    String orderId = "100";

    boolean orderSuccess = true;

    BinanceApiWebSocketClientStub streamClient;

    public ApiClientStub(ArbOrders orders) {
        this.orders = orders;
    }

    public ApiClientStub() {
    }

    @Override
    public ArbOrders getOrderBook(String symbol) {
        return orders;
    }

    @Override
    public List<MyArbOrder> getMyOrders() {

        return Arrays.asList(new MyArbOrder(
                order.symbol, this.orderId, order.price,
                order.quantity, this.orderSuccess ? order.quantity : 0.0
                , order.action,
                System.currentTimeMillis())
        );
    }

    @Override
    public String addArbOrder(NewArbOrder newArbOrder) {
        order = newArbOrder;
        return this.orderId;
    }

    @Override
    public void cancelOrder(MyArbOrder order) {

    }

    public void setOrderBook(ArbOrders orders) {
        this.orders = orders;
    }

    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(ArbWalletEntry withdrawalDetails) {

    }

    @Override
    public ArbWallet getWallet() {

        return null;
    }

    public  NewArbOrder getLatestOrder(){
        return order;
    }

    public void setOrderSuccess(boolean orderSuccess) {
        this.orderSuccess = orderSuccess;
    }
}
