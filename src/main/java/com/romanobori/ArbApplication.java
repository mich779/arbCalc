package com.romanobori;

public class ArbApplication {

    public ArbApplication(ApiClient binanceApiClient, ApiClient bitfinextClient) {

    }

    public void run(ApiClient binanceApiClient, ApiClient bitfinextClient){

        ArbOrders binanceOrderBook = binanceApiClient.getOrderBook("NEOETH");

        ArbOrders bitfinexOrderBook = bitfinextClient.getOrderBook("NEOETH");

        ArbOrderEntry highestBinanceAsk = binanceOrderBook.asks.get(0);

        ArbOrderEntry highestBitfinexAsk = bitfinexOrderBook.asks.get(0);

        if(highestBitfinexAsk.price * 1.0033 >=  highestBinanceAsk.price){
            binanceApiClient.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.BUY,
                    highestBinanceAsk.amount, highestBinanceAsk.price));
        }


    }
}
