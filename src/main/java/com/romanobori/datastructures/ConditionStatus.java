package com.romanobori.datastructures;

public class ConditionStatus {
    Boolean isPassed;
    double binancePrice;
    double bitfinexPrice;


    public ConditionStatus(Boolean isPassed, double binancePrice, double bitfinexPrice) {
        this.isPassed = isPassed;
        this.binancePrice = binancePrice;
        this.bitfinexPrice = bitfinexPrice;
    }

    public Boolean isPassed() {
        return isPassed;
    }

    public double getBinancePrice() {
        return binancePrice;
    }

    public double getBitfinexPrice() {
        return bitfinexPrice;
    }
}
