package com.romanobori;

import java.util.List;

public class ApiClientStub implements ApiClient {

    ArbOrders orders;

    public ApiClientStub(ArbOrders orders) {
        this.orders = orders;
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
    }

    @Override
    public void cancelOrder(MyArbOrder order) {

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
}
