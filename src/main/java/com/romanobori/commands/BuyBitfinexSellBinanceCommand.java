package com.romanobori.commands;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BuyBitfinexSellBinanceCommand extends ArbCommand {

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

    public BuyBitfinexSellBinanceCommand(int count) {
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
        this.binanceListeningKey = binanceClient.startUserDataStream();
    }

    @Override
    String firstOrder() {
        return bitfinexClient.addArbOrder(new NewArbOrderLimit(
                symbol, ARBTradeAction.BUY, 0.02, bitfinexOrderBookUpdated.getHighestBid()
        ));
    }

    @Override
    Supplier<Boolean> condition() {
        return () -> bitfinexOrderBookUpdated.getHighestBid() * 1.003 <= binanceOrderBookUpdated.getHighestBid();
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId) -> bitfinexClient.cancelOrder(symbol, orderId);
    }

    @Override
    Runnable secondOrder() {
        return () -> binanceClient
                .newOrder(
                        new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, "0.02"));

    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {

        return null;
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {
        return null;
    }
}
