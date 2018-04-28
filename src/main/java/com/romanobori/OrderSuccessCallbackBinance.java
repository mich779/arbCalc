package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class OrderSuccessCallbackBinance extends OrderSuccessCallback {
    private BinanceApiWebSocketClient socketClient;
    private String binanceListenKey;
    public OrderSuccessCallbackBinance(BinanceApiWebSocketClient socketClient, String binanceListeningKey) {
        this.socketClient = socketClient;
        this.binanceListenKey = binanceListeningKey;
    }

    @Override
    public void register(String orderId, Runnable action, AtomicBoolean orderCompleted) {
        socketClient.onUserDataUpdateEvent(binanceListenKey,
                response -> {
                    System.out.println(response);
                    if(responseTypeIsOrderTradeUpdate(response)){
                        if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                            System.out.println("should print this if first order passed !");
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
