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
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BuyBinanceSellBitfinexCommand extends ArbCommand {
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

    public BuyBinanceSellBitfinexCommand(String symbol, String binanceKey, String binanceSecret, String bitfinexKey, String bitfinexSecret, int count,
                                         BitfinexCurrencyPair bitfinexCurrencyPair) {
        super(count);
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        BitfinexClient bitfinexClient1 = new BitfinexClient(bitfinexKey, bitfinexSecret);
        BitfinexApiBroker broker = new BitfinexApiBroker(bitfinexKey, bitfinexSecret);
        this.bitfinexClient = new BitfinexClientApi(bitfinexClient1);
        this.bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                new BitfinexClientApi(bitfinexClient1),
                broker,
                symbol,
                bitfinexCurrencyPair
        );
        this.bitfinexCurrencyPair = bitfinexCurrencyPair;
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
        return Long.toString(
                binanceClient.newOrder(
                        new NewOrder(symbol, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "0.2", Double.toString(binanceOrderBookUpdated.getHighestBid()))
                ).getOrderId());
    }

    @Override
    Supplier<ConditionStatus> condition() {
        return () -> {
            double binanceHighestBid = binanceOrderBookUpdated.getHighestBid();
            double bitfinexHighestBid = bitfinexOrderBookUpdated.getHighestBid();

            return new ConditionStatus(binanceHighestBid * 1.003508 <= bitfinexHighestBid,
            binanceHighestBid, bitfinexHighestBid);
        };
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
    ArbCommand buildAnotherCommand(int newCount) {
        return new BuyBinanceSellBitfinexCommand(
                symbol, binanceKey, binanceSecret, bitfinexKey, bitfinexSecret, newCount, bitfinexCurrencyPair
        );
    }

    @Override
    String type() {
        return "BuyBinanceSellBitfinexCommand";
    }
}
