package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

class BuyFromBinanceSellInBitfinexCommand {

    ApiClient binanceApi;

    ApiClient bitfinexApi;

    BinanceApiWebSocketClient socketClient;

    AtomicBoolean finished = new AtomicBoolean(false);

    public BuyFromBinanceSellInBitfinexCommand(ApiClient binanceApi, ApiClient bitfinexApi, BinanceApiWebSocketClient socketClient) {
        this.binanceApi = binanceApi;
        this.bitfinexApi = bitfinexApi;
        this.socketClient = socketClient;
    }

    public boolean invoke() {
        ArbOrderEntry highestBinanceAsk = getHighestNthAsk(binanceApi.getOrderBook("NEOETH"), 1);

        ArbOrderEntry highestBitfinexAsk = getHighestNthAsk(bitfinexApi.getOrderBook("NEOETH"), 3);

        if(highestBitfinexAsk.price  >=  highestBinanceAsk.price * 1.0033){
            String orderId = buyFromBinance(highestBinanceAsk);
            this.socketClient.onUserDataUpdateEvent("listenKey",
            response -> {
                if(responseTypeIsOrderTradeUpdate(response)){
                    if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                        sellInBitfinexInMarketRate(highestBinanceAsk.amount);

                        finished.set(true);
                    }
                }
            });

            while(!finished.get()){
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {

                }
            }
        }
        return true;
    }

    private String buyFromBinance(ArbOrderEntry highestBinanceAsk) {

        return binanceApi.addArbOrder(new NewArbOrderLimit("NEOETH", ARBTradeAction.BUY, highestBinanceAsk.amount
        ,highestBinanceAsk.price ));
    }

    private void sellInBitfinexInMarketRate(double amount) {
            bitfinexApi.addArbOrder(new NewArbOrderMarket("NEOETH", ARBTradeAction.SELL, amount));
    }

    private boolean responseTypeIsOrderTradeUpdate(UserDataUpdateEvent response) {
        return response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE;
    }

    private boolean currentOrderHasFilled(String orderId, OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Long.toString(orderTradeUpdateEvent.getOrderId()).equals(orderId) &&
                orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED;
    }

    private ArbOrderEntry getHighestNthAsk(ArbOrders binanceOrderBook, int n) {
        List<ArbOrderEntry> asks = binanceOrderBook.asks;
        List<ArbOrderEntry> sortedAsks = asks.stream().sorted(Comparator.comparingDouble(ask -> ask.price)).collect(Collectors.toList());
        return sortedAsks.get(sortedAsks.size() - n);

    }
}
