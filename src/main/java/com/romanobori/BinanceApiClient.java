package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;

public class BinanceApiClient implements ApiClient {

    BinanceApiRestClient binanceApi;
    int orderBookLimit;

    public BinanceApiClient(BinanceApiRestClient binanceApi, int orderBookLimit) {
        this.binanceApi = binanceApi;
        this.orderBookLimit = orderBookLimit;
    }

    @Override
    public ArbOrders getOpenOrders(String symbol) {
        return null;
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
