package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class OrderSuccessCallbackBinance extends OrderSuccessCallback {
    BinanceApiWebSocketClient socketClient;
    String binanceListenKey;
    public OrderSuccessCallbackBinance(BinanceApiWebSocketClient socketClient, String binanceListeningKey) {
        this.socketClient = socketClient;
        this.binanceListenKey = binanceListeningKey;
    }

    @Override
    public void register(String orderId, Runnable action, AtomicBoolean orderCompleted) {
        this.socketClient.onUserDataUpdateEvent(binanceListenKey,
                response -> {
                    if(responseTypeIsOrderTradeUpdate(response)){
                        if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                            action.run();
                            orderCompleted.set(true);
                        }
                    }
                });
    }

    private boolean responseTypeIsOrderTradeUpdate(UserDataUpdateEvent response) {
        return response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE;
    }


    private boolean currentOrderHasFilled(String orderId, OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Long.toString(orderTradeUpdateEvent.getOrderId()).equals(orderId) &&
                orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED;
    }
}