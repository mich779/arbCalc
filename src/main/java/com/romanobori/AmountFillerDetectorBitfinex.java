package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrderState;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.google.common.util.concurrent.AtomicDouble;
import com.romanobori.commands.LimitOrderDetails;
import com.romanobori.commands.UpdateConditionDetails;

import java.util.Observer;
import java.util.function.Consumer;

public class AmountFillerDetectorBitfinex extends AmountFillerDetector {

    private BitfinexApiBroker bitfinexClient;

    public AmountFillerDetectorBitfinex(BitfinexApiBroker bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }

    @Override
    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder, Observer observer) {
        final AtomicDouble leftAmount = new AtomicDouble(orderDetails.getAmount());
        OrderManager orderManager = bitfinexClient.getOrderManager();
        orderManager.registerCallback(exchangeOrder -> {
            ExchangeOrderState exchangeState = exchangeOrder.getState();
            if (stateIsPartiallyFilled(exchangeState) || isStateComplete(exchangeState)) {
                System.out.println(exchangeOrder);
                if (isCurrentOrder(orderDetails.getOrderId(), exchangeOrder)) {
                    double updatedLeftAmount = Math.abs(exchangeOrder.getAmount());
                    secondOrder.accept(leftAmount.get() - updatedLeftAmount);
                    leftAmount.set(updatedLeftAmount);
                    if (isStateComplete(exchangeState)) {
                        observer.update(this, new UpdateConditionDetails("FULL", 0.0));
                    }else{
                        observer.update(this, new UpdateConditionDetails("PARTIAL", Math.abs(exchangeOrder.getAmount())));
                    }
                }
            }
        });

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
