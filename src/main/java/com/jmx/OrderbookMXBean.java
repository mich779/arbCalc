package com.jmx;

public interface OrderbookMXBean {

    public double getBinanceHighestBid();
    public double getBitfinexHighestBid();
    public double getBinanceLowestAsk();
    public double getBitfinexLowestAsk();

}
