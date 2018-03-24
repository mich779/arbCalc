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
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BuyBitfinexSellBinanceCommand extends ArbCommand {

    private BinanceApiRestClient binanceClient;
    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    private BitfinexClientApi bitfinexClient;
    private String symbol;
    private BinanceApiWebSocketClient socketClient;
    private String binanceKey;
    private String binanceSecret;
    private String bitfinexKey;
    private String bitfinexSecret;
    private String binanceListeningKey;
    BitfinexCurrencyPair bitfinexCurrencyPair;
    public BuyBitfinexSellBinanceCommand(int count, String binanceKey, String binanceSecret, String symbol, String bitfinexKey, String bitfinexSecret, BitfinexCurrencyPair bitfinexCurrencyPair) {
        super(count);
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        this.bitfinexClient = new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret));
        this.bitfinexCurrencyPair = bitfinexCurrencyPair;
        this.bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                this.bitfinexClient,
                new BitfinexApiBroker(bitfinexKey, bitfinexSecret),
                symbol, bitfinexCurrencyPair
        );
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
                symbol, ARBTradeAction.BUY, 0.2, bitfinexOrderBookUpdated.getHighestBid()
        ));
    }

    @Override
    Supplier<ConditionStatus> condition() {
        return () -> {
            double bitfinexHighestBid = bitfinexOrderBookUpdated.getHighestBid();
            double binanceHighestBid = binanceOrderBookUpdated.getHighestBid();
            return new ConditionStatus(
                    bitfinexHighestBid * 1.002504 <= binanceHighestBid,
                    binanceHighestBid, bitfinexHighestBid
            );
        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId -> {
            BitfinexApiBroker client = new BitfinexApiBroker(bitfinexKey, bitfinexSecret);
            try {
                client.connect();
                OrderManager orderManager = client.getOrderManager();
                orderManager.cancelOrder(Long.parseLong(orderId));
            } catch (APIException e) {
                throw new RuntimeException("could not connect to bitfinex client");
            }
        });
    }

    @Override
    Runnable secondOrder() {
        return () -> binanceClient
                .newOrder(
                        new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, "0.2"));

    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBitfinex(new BitfinexApiBroker(this.bitfinexKey, this.bitfinexSecret));
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {
        return new BuyBitfinexSellBinanceCommand(count, binanceKey, binanceSecret, symbol, bitfinexKey, bitfinexSecret, bitfinexCurrencyPair);
    }

    @Override
    String type() {
        return "BuyBitfinexSellBinanceCommand";
    }
}
