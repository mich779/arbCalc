package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.function.Consumer;

import static java.lang.Double.parseDouble;

public class AmountFillerDetectorBinance extends AmountFillerDetectorObservable {
    private BinanceApiWebSocketClient socketClient;
    private String binanceListenKey;

    public AmountFillerDetectorBinance(BinanceApiWebSocketClient socketClient, String binanceListeningKey) {
        this.socketClient = socketClient;
        this.binanceListenKey = binanceListeningKey;
    }

    @Override
    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder) {
        socketClient.onUserDataUpdateEvent(binanceListenKey,
                response -> {
                    if (! checkPreconditions(orderDetails, response)) return;

                    OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                    double originalQuantity = parseDouble(orderTradeUpdateEvent.getOriginalQuantity());
                    double accumulatedQuantity = parseDouble(orderTradeUpdateEvent.getAccumulatedQuantity());
                    double quantityLastFilled = parseDouble(orderTradeUpdateEvent.getQuantityLastFilledTrade());

                    secondOrder.accept(quantityLastFilled);
                    notifyObservers(orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED ? "FILLED" : "PARTIAL",
                            originalQuantity - accumulatedQuantity - quantityLastFilled);
                });
    }

    private boolean checkPreconditions(LimitOrderDetails orderDetails, UserDataUpdateEvent response) {
        OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();


        return responseTypeIsOrderTradeUpdate(response) &&
                orderStatusIsPartialOrFilled(orderTradeUpdateEvent.getOrderStatus()) &&
                isCurrentOrder(orderDetails.getOrderId(), orderTradeUpdateEvent);

    }

    private boolean orderStatusIsPartialOrFilled(OrderStatus status){
        return status == OrderStatus.FILLED || status == OrderStatus.PARTIALLY_FILLED;
    }

    private double getAccumulatedQuantity(OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return parseDouble(orderTradeUpdateEvent.getAccumulatedQuantity());
    }

    private double getOriginalQuantity(OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return parseDouble(orderTradeUpdateEvent.getOriginalQuantity());
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
