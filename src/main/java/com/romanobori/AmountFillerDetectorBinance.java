package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.romanobori.commands.LimitOrderDetails;
import com.romanobori.commands.UpdateConditionDetails;

import java.util.Observer;
import java.util.function.Consumer;

public class AmountFillerDetectorBinance extends AmountFillerDetector {
    private BinanceApiWebSocketClient socketClient;
    private String binanceListenKey;

    public AmountFillerDetectorBinance(BinanceApiWebSocketClient socketClient, String binanceListeningKey) {
        this.socketClient = socketClient;
        this.binanceListenKey = binanceListeningKey;
    }

    @Override
    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder, Observer observer) {
        socketClient.onUserDataUpdateEvent(binanceListenKey,
                response -> {
                    System.out.println(response);
                    if (responseTypeIsOrderTradeUpdate(response)) {
                        OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                        if (orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED
                                || orderTradeUpdateEvent.getOrderStatus() == OrderStatus.PARTIALLY_FILLED) {
                            if (isCurrentOrder(orderDetails.getOrderId(), orderTradeUpdateEvent)) {
                                double amountExecuted = Double.parseDouble(orderTradeUpdateEvent.getQuantityLastFilledTrade());
                                secondOrder.accept(amountExecuted);
                                if (orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED) {
                                    observer.update(this, new UpdateConditionDetails("FULL", 0.0));
                                } else {
                                    observer.update(this, new UpdateConditionDetails("PARTIAL", getOriginalQuantity(orderTradeUpdateEvent) - getAccumulatedQuantity(orderTradeUpdateEvent)));
                                }
                                System.out.println("should print this if first order passed !");
                            }
                        }
                    }
                });
    }

    private double getAccumulatedQuantity(OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Double.parseDouble(orderTradeUpdateEvent.getAccumulatedQuantity());
    }

    private double getOriginalQuantity(OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Double.parseDouble(orderTradeUpdateEvent.getOriginalQuantity());
    }

    private boolean responseTypeIsOrderTradeUpdate(UserDataUpdateEvent response) {
        return response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE;
    }


    private boolean filledCompletely(OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED;
    }

    private boolean isCurrentOrder(String orderId, OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Long.toString(orderTradeUpdateEvent.getOrderId()).equals(orderId);
    }
}
