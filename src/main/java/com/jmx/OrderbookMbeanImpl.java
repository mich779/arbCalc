package com.jmx;

import com.romanobori.BinanceOrderBookUpdated;
import com.romanobori.BitfinexOrderBookUpdated;

public class OrderbookMbeanImpl implements OrderbookMXBean {

    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    private BitfinexOrderBookUpdated bitfinexOrderBookUpdated;

    public OrderbookMbeanImpl(BinanceOrderBookUpdated binanceOrderBookUpdated, BitfinexOrderBookUpdated bitfinexOrderBookUpdated) {
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
    }

    @Override
    public double getBinanceHighestBid() {
        return binanceOrderBookUpdated.getHighestBid().getPrice();
    }


    @Override
    public double getBitfinexHighestBid() {
        return bitfinexOrderBookUpdated.getHighestBid().getPrice();
    }


    @Override
    public double getBinanceLowestAsk() {
        return binanceOrderBookUpdated.getLowestAsk().getPrice();
    }


    @Override
    public double getBitfinexLowestAsk() {
        return bitfinexOrderBookUpdated.getLowestAsk().getPrice();
    }

}
