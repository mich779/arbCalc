package com.romanobori.commands;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArbCommandBuyBinanceSellBitfinex extends ArbCommand {
    BinanceApiRestClient binanceClient;
    BinanceOrderBookUpdated binanceOrderBookUpdated;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    ApiClient bitfinexClient;
    String symbol;
    BinanceApiWebSocketClient socketClient;

    public ArbCommandBuyBinanceSellBitfinex(BinanceApiRestClient binanceClient, ApiClient bitfinexClient, BinanceOrderBookUpdated binanceOrderBookUpdated, BitfinexOrderBookUpdated bitfinexOrderBookUpdated, String symbol, BinanceApiWebSocketClient socketClient) {
        this.binanceClient = binanceClient;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        this.bitfinexClient = bitfinexClient;
        this.symbol = symbol;
        this.socketClient = socketClient;
    }

    @Override
    String firstOrder() {
        return Long.toString(
                binanceClient.newOrder(
                new NewOrder(symbol, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "0.2")
        ).getOrderId());
    }

    @Override
    Supplier<Boolean> condition() {
        return () -> binanceOrderBookUpdated.getLowestAsk() * 1.003 <= bitfinexOrderBookUpdated.getLowestAsk();
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId) -> binanceClient.cancelOrder(new CancelOrderRequest(this.symbol, orderId));
    }

    @Override
    Runnable secondOrder() {
        return () -> bitfinexClient.addArbOrder(new NewArbOrderMarket(
                symbol, ARBTradeAction.SELL, 0.2
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBinance(socketClient);
    }
}
