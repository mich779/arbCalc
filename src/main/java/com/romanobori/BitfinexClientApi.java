package com.romanobori;

import com.bitfinex.client.BitfinexClient;

public class BitfinexClientApi implements ApiClient {
    BitfinexClient bitfinexClient;
    public BitfinexClientApi(BitfinexClient bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
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
