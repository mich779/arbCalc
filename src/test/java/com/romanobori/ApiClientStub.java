package com.romanobori;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApiClientStub implements ApiClient {

    ArbOrders orders;

    NewArbOrder order;

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
        return null;
    }

    @Override
    public void addArbOrder(NewArbOrder newArbOrder) {
        order = newArbOrder;
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
}
