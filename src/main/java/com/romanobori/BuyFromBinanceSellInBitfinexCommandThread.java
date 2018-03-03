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

    ArbOrderBookUpdated binanceOrderBookUpdated;

    AtomicBoolean shouldRun = new AtomicBoolean(true);

    double priceInBinance;

    public BuyFromBinanceSellInBitfinexCommandThread(double priceInBinance, String orderId, String symbol, ApiClient binanceApi, ArbOrderBookUpdated binanceOrderBookUpdated) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.binanceApi = binanceApi;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.priceInBinance = priceInBinance;
    }



    private ArbOrderEntry getHighestNthAsk(ArbOrders binanceOrderBook, int n) {
        List<ArbOrderEntry> bids = binanceOrderBook.bids;
        List<ArbOrderEntry> sortedAsks = bids.stream().sorted(Comparator.comparingDouble(bid -> bid.price)).collect(Collectors.toList());
        return sortedAsks.get(sortedAsks.size() - n);

    }

    public void setFinished(){
        shouldRun.set(false);
    }

    @Override
    public Boolean call() throws Exception {
        while(shouldRun.get()) {
            
            ArbOrderEntry highestBitfinexBid = getHighestNthAsk(binanceOrderBookUpdated.getOrderBook(), 1);

            if (!(priceInBinance * 1.003 <= highestBitfinexBid.price)) {
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
