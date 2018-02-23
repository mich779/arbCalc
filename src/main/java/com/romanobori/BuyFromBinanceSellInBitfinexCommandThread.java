package com.romanobori;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BuyFromBinanceSellInBitfinexCommandThread implements Callable<Boolean> {

    String orderId;

    String symbol;

    ApiClient binanceApi;

    ApiClient bitfinexApi;

    AtomicBoolean shouldRun = new AtomicBoolean(true);

    double priceInBinance;

    public BuyFromBinanceSellInBitfinexCommandThread(double priceInBinance, String orderId, String symbol, ApiClient binanceApi, ApiClient bitfinexApi) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.binanceApi = binanceApi;
        this.bitfinexApi = bitfinexApi;
        this.priceInBinance = priceInBinance;
    }



    private ArbOrderEntry getHighestNthAsk(ArbOrders binanceOrderBook, int n) {
        List<ArbOrderEntry> asks = binanceOrderBook.asks;
        List<ArbOrderEntry> sortedAsks = asks.stream().sorted(Comparator.comparingDouble(ask -> ask.price)).collect(Collectors.toList());
        return sortedAsks.get(sortedAsks.size() - n);

    }

    public void setFinished(){
        shouldRun.set(false);
    }

    @Override
    public Boolean call() throws Exception {
        while(shouldRun.get()) {
            
            ArbOrderEntry highestBitfinexAsk = getHighestNthAsk(bitfinexApi.getOrderBook("NEOETH"), 1);

            if (!(highestBitfinexAsk.price >= priceInBinance * 1.0033)) {
                binanceApi.cancelOrder(this.symbol, orderId);
                return Boolean.FALSE;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        return Boolean.TRUE;
    }
}
