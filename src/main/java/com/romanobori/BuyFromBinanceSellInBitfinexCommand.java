package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

class BuyFromBinanceSellInBitfinexCommand {

    ApiClient binanceApi;

    ApiClient bitfinexApi;

    BinanceApiWebSocketClient socketClient;

    AtomicBoolean finished = new AtomicBoolean(false);

    BinanceOrderBookUpdated binanceOrderBookUpdated;

    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    public BuyFromBinanceSellInBitfinexCommand(ApiClient binanceApi, ApiClient bitfinexApi, BinanceApiWebSocketClient socketClient, BinanceOrderBookUpdated binanceOrderBookUpdated, BitfinexOrderBookUpdated bitfinexOrderBookUpdated) {
        this.binanceApi = binanceApi;
        this.bitfinexApi = bitfinexApi;
        this.socketClient = socketClient;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
    }

    public boolean invoke(String binanceListenKey) throws InterruptedException, ExecutionException {
        ArbOrderEntry highestBinanceBid = getHighestNthAsk(binanceOrderBookUpdated.getOrderBook(), 1);

        ArbOrderEntry highestBitfinexBid = getHighestNthAsk(bitfinexOrderBookUpdated.getOrderBook(), 1);

        if(highestBinanceBid.price * 1.001 <= highestBitfinexBid.price){

            System.out.println("contidion matched !!!");
            String orderId = buyFromBinance(highestBinanceBid, 0.2);
            BuyFromBinanceSellInBitfinexCommandThread t = new BuyFromBinanceSellInBitfinexCommandThread(highestBinanceBid.price , orderId, "NEOETH", binanceApi, binanceOrderBookUpdated);
            final ExecutorService pool = Executors.newFixedThreadPool(1);

            Future<Boolean> future = pool.submit(t);
            this.socketClient.onUserDataUpdateEvent(binanceListenKey,
            response -> {
                if(responseTypeIsOrderTradeUpdate(response)){
                    if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                        sellInBitfinexInMarketRate(highestBinanceBid.amount);
                        t.setFinished();
                    }
                }
            });
            boolean success = future.get();
            if(success){
                System.out.println("success !!");
                return true;
            }
            else{
                System.out.println("didnt buy even thougth passed the condition in the first place");
                Thread.sleep(1000);
                return invoke(binanceListenKey);
            }
        }

        System.out.println("didnt match contidion");
        Thread.sleep(3000);
        return invoke(binanceListenKey);
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
        List<ArbOrderEntry> bids = binanceOrderBook.bids;
        List<ArbOrderEntry> sortedAsks = bids.stream().sorted(Comparator.comparingDouble(ask -> ask.price)).collect(Collectors.toList());
        return sortedAsks.get(sortedAsks.size() - n);

    }
}
