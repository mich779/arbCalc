package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrderState;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class OrderSuccessCallbackBitfinex extends OrderSuccessCallback {

    private BitfinexApiBroker bitfinexClient;

    public OrderSuccessCallbackBitfinex(BitfinexApiBroker bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }

    @Override
    public void register(String orderId, Runnable action, AtomicBoolean orderComplete) {
        OrderManager orderManager = bitfinexClient.getOrderManager();
        orderManager.registerCallback(exchangeOrder -> {
            if(isCurrentOrder(orderId, exchangeOrder) && executedSuccessfully(exchangeOrder)){
                System.out.println("should print this if first order passed !");
                action.run();
                orderComplete.set(true);
            }
        });
    }

    private boolean executedSuccessfully(ExchangeOrder exchangeOrder) {
        return exchangeOrder.getState() == ExchangeOrderState.STATE_EXECUTED;
    }

    private boolean isCurrentOrder(String orderId, ExchangeOrder exchangeOrder) {
        return exchangeOrder.getOrderId() == Long.parseLong(orderId);
    }
}
