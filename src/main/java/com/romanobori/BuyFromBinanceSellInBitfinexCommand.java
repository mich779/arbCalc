package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.sun.scenario.effect.ImageData;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
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

    public boolean invoke(String listenKey) throws InterruptedException, ExecutionException {
        ArbOrderEntry highestBinanceAsk = getHighestNthAsk(binanceApi.getOrderBook("NEOETH"), 1);

        ArbOrderEntry highestBitfinexAsk = getHighestNthAsk(bitfinexApi.getOrderBook("NEOETH"), 1);

        if(highestBitfinexAsk.price  >=  highestBinanceAsk.price * 1.0033){
            String orderId = buyFromBinance(highestBinanceAsk, 1.0);
            BuyFromBinanceSellInBitfinexCommandThread t = new BuyFromBinanceSellInBitfinexCommandThread(highestBinanceAsk.price , orderId, "NEOETH", binanceApi, bitfinexApi);
            final ExecutorService pool = Executors.newFixedThreadPool(10);

            Future<Boolean> future = pool.submit(t);
            this.socketClient.onUserDataUpdateEvent(listenKey,
            response -> {
                if(responseTypeIsOrderTradeUpdate(response)){
                    if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                        sellInBitfinexInMarketRate(highestBinanceAsk.amount);
                        t.setFinished();
                    }
                }
            });
            boolean success = future.get();
            if(success){
                return true;
            }
            else{
                Thread.sleep(1000);
                return new BuyFromBinanceSellInBitfinexCommand(binanceApi, bitfinexApi, socketClient).invoke(listenKey);
            }
        }
        Thread.sleep(1000);
        return new BuyFromBinanceSellInBitfinexCommand(binanceApi, bitfinexApi, socketClient).invoke(listenKey);
    }

    private String buyFromBinance(ArbOrderEntry highestBinanceAsk, double amount) {

        return binanceApi.addArbOrder(new NewArbOrderLimit("NEOETH", ARBTradeAction.BUY, amount
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
