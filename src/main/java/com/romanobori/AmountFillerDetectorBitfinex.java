package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrderState;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.function.Consumer;

public class AmountFillerDetectorBitfinex extends AmountFillerDetectorObservable {

    private BitfinexApiBroker bitfinexClient;

    public AmountFillerDetectorBitfinex(BitfinexApiBroker bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }

    @Override
    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder) {

        final AtomicDouble leftAmount = new AtomicDouble(orderDetails.getAmount());

        bitfinexClient.getOrderManager().registerCallback(exchangeOrder -> {
            ExchangeOrderState exchangeState = exchangeOrder.getState();
            if (!checkPrecoditions(orderDetails, exchangeOrder, exchangeState)) return;
            Double updatedLeftAmount = Math.abs(exchangeOrder.getAmount());
            secondOrder.accept(leftAmount.get() - updatedLeftAmount);
            leftAmount.set(updatedLeftAmount);
            notifyObservers(isStateComplete(exchangeState) ? "FILLED" : "PARTIAL");
        });

    }

    private boolean checkPrecoditions(LimitOrderDetails orderDetails, ExchangeOrder exchangeOrder, ExchangeOrderState exchangeState) {
        return stateIsPartialOrExecutedCompletely(exchangeState) &&
                isCurrentOrder(orderDetails.getOrderId(), exchangeOrder);
    }

    private boolean stateIsPartialOrExecutedCompletely(ExchangeOrderState exchangeState) {
        return stateIsPartiallyFilled(exchangeState) || isStateComplete(exchangeState);
    }

    private boolean isStateComplete(ExchangeOrderState exchangeState) {
        return stateIs(exchangeState, ExchangeOrderState.STATE_EXECUTED);
    }

    private boolean stateIsPartiallyFilled(ExchangeOrderState exchangeState) {
        return stateIs(exchangeState, ExchangeOrderState.STATE_PARTIALLY_FILLED);
    }

    private boolean stateIs(ExchangeOrderState exchangeState, ExchangeOrderState statePartiallyFilled) {
        return exchangeState == statePartiallyFilled;
    }


    private boolean isCurrentOrder(String orderId, ExchangeOrder exchangeOrder) {
        return exchangeOrder.getOrderId() == Long.parseLong(orderId);
    }
}
