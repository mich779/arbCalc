package com.romanobori.commands;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArbCommandBuyBinanceSellBitfinex extends ArbCommand {
    private BinanceApiRestClient binanceClient;
    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    private ApiClient bitfinexClient;
    private String symbol;
    private BinanceApiWebSocketClient socketClient;
    private String binanceKey;
    private String binanceSecret;
    private String bitfinexKey;
    private String bitfinexSecret;
    private String binanceListeningKey;
    public ArbCommandBuyBinanceSellBitfinex(String symbol, String binanceKey, String binanceSecret, String bitfinexKey, String bitfinexSecret, String binanceListeningKey, int count) {
        super(count);
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        BitfinexClient bitfinexClient1 = new BitfinexClient(bitfinexKey, bitfinexSecret);
        this.bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                new BitfinexClientApi(bitfinexClient1),
                new BitfinexApiBroker(binanceKey, bitfinexSecret),
                symbol
        );
        this.bitfinexClient = new BitfinexClientApi(bitfinexClient1);
        this.symbol = symbol;
        this.socketClient = new BinanceApiWebSocketClientImpl();
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        this.binanceListeningKey = binanceListeningKey;
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
        return new OrderSuccessCallbackBinance(socketClient, binanceListeningKey);
    }

    @Override
    ArbCommand buildAnotherCommand() {
        return new ArbCommandBuyBinanceSellBitfinex(
                symbol, binanceKey, binanceSecret, bitfinexKey, bitfinexSecret, binanceListeningKey, count - 1
        );
    }
}
