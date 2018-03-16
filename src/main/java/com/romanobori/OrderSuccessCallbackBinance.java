package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.romanobori.commands.ConditionKeeperThread;

public class OrderSuccessCallbackBinance extends OrderSuccessCallback {
    BinanceApiWebSocketClient socketClient;
    String binanceListenKey;
    public OrderSuccessCallbackBinance(BinanceApiWebSocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public void register(String orderId, Runnable action, ConditionKeeperThread t) {
        this.socketClient.onUserDataUpdateEvent(binanceListenKey,
                response -> {
                    if(responseTypeIsOrderTradeUpdate(response)){
                        if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                            action.run();
                            t.stop();
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
