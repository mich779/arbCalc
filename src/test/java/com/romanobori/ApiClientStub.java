package com.romanobori;

public class ApiClientStub implements ApiClient {

    ArbOrders orders;

    public ApiClientStub(ArbOrders orders) {
        this.orders = orders;
    }

    @Override
    public ArbOrders getOpenOrders(String symbol) {
        return orders;
    }

    @Override
    public MyArbOrders getMyOrders() {
        return null;
    }

    @Override
    public void addArbOrder(NewArbOrder order) {

    }

    @Override
    public void cancelOrder(long orderId) {

    }

    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(long withrawalId) {

    }
}
