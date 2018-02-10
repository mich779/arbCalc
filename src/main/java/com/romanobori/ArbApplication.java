package com.romanobori;

public class ArbApplication {

    ApiClient binanceApiClient;
    ApiClient bitfinextClient;
    public ArbApplication(ApiClient binanceApiClient, ApiClient bitfinextClient) {
        this.binanceApiClient = binanceApiClient;
        this.bitfinextClient = bitfinextClient;
    }

    public void run(){

        ArbOrderEntry highestBinanceAsk = getHighestAsk(binanceApiClient.getOrderBook("NEOETH"));

        ArbOrderEntry highestBitfinexAsk = getHighestAsk(bitfinextClient.getOrderBook("NEOETH"));

        buyFromBinanceAndSellInBitfinex(binanceApiClient, bitfinextClient, highestBinanceAsk, highestBitfinexAsk);

    }

    private void buyFromBinanceAndSellInBitfinex(ApiClient binanceApiClient, ApiClient bitfinextClient, ArbOrderEntry highestBinanceAsk, ArbOrderEntry highestBitfinexAsk) {
        if(highestBitfinexAsk.price  >=  highestBinanceAsk.price * 1.0033){
            String orderId = binanceApiClient.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.BUY,
                    highestBinanceAsk.amount, highestBinanceAsk.price));

            if(binanceApiClient.isOrderDone(orderId)) {

                bitfinextClient.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.SELL,
                        highestBitfinexAsk.amount, highestBitfinexAsk.price));
            }
        }
    }

    private ArbOrderEntry getHighestAsk(ArbOrders binanceOrderBook) {
        return binanceOrderBook.asks.get(0);
    }
}
