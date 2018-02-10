package com.romanobori;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;

import java.util.concurrent.atomic.AtomicBoolean;

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
        ArbOrderEntry highestBinanceAsk = getHighestAsk(binanceApi.getOrderBook("NEOETH"));

        ArbOrderEntry highestBitfinexAsk = getHighestAsk(bitfinexApi.getOrderBook("NEOETH"));

        if(highestBitfinexAsk.price  >=  highestBinanceAsk.price * 1.0033){
            String orderId = buyFromBinance(highestBinanceAsk);
            this.socketClient.onUserDataUpdateEvent("listenKey",
                    handleResponse(highestBitfinexAsk, orderId));

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
        return binanceApi.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.BUY,
                highestBinanceAsk.amount, highestBinanceAsk.price));
    }

    private BinanceApiCallback<UserDataUpdateEvent> handleResponse(ArbOrderEntry highestBitfinexAsk, String orderId) {
        return response -> {
            if(responseTypeIsOrderTradeUpdate(response)){
                if(currentOrderHasFilled(orderId, response.getOrderTradeUpdateEvent())){
                    sellInBitfinex(highestBitfinexAsk);

                    finished.set(true);
                }
            }
};
    }

    private void sellInBitfinex(ArbOrderEntry highestBitfinexAsk) {
        bitfinexApi.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.SELL,
                highestBitfinexAsk.amount, highestBitfinexAsk.price));
    }

    private boolean responseTypeIsOrderTradeUpdate(UserDataUpdateEvent response) {
        return response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE;
    }

    private boolean currentOrderHasFilled(String orderId, OrderTradeUpdateEvent orderTradeUpdateEvent) {
        return Long.toString(orderTradeUpdateEvent.getOrderId()).equals(orderId) &&
                orderTradeUpdateEvent.getOrderStatus() == OrderStatus.FILLED;
    }

    private ArbOrderEntry getHighestAsk(ArbOrders binanceOrderBook) {
        return binanceOrderBook.asks.get(0);
    }
}
