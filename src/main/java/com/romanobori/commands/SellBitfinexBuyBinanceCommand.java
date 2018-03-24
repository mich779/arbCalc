package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SellBitfinexBuyBinanceCommand extends ArbCommand {

    BitfinexClientApi bitfinexClient;
    String symbol;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    BinanceOrderBookUpdated binanceOrderBookUpdated;
    BinanceApiRestClientImpl binanceClient;
    String bitfinexKey;
    String bitfinexSecret;
    String binanceKey;
    String binanceSecret;
    public SellBitfinexBuyBinanceCommand(int count,String symbol,  String binanceKey, String binanceSecret, String bitfinexKey, String bitfinexSecret) {
        super(count);
        this.bitfinexClient = new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret));
        this.symbol = symbol;
        this.binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        this.bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret)),
                new BitfinexApiBroker(binanceKey, bitfinexSecret),
                symbol
        );
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;
    }

    @Override
    String firstOrder() {
        return bitfinexClient.addArbOrder( new NewArbOrderLimit(
                symbol, ARBTradeAction.SELL, 0.2, bitfinexOrderBookUpdated.getLowestAsk()
        ));
    }

    @Override
    Supplier<Boolean> condition() {
        return ()-> binanceOrderBookUpdated.getLowestAsk() * 1.003 <= bitfinexOrderBookUpdated.getLowestAsk();
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId -> bitfinexClient.cancelOrder(symbol, orderId));
    }

    @Override
    Runnable secondOrder() {
        return () -> binanceClient.newOrder(new NewOrder(
                symbol, OrderSide.BUY, OrderType.MARKET, TimeInForce.GTC, "0.2"
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBitfinex(new BitfinexApiBroker(bitfinexKey, bitfinexSecret));
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {

        return new SellBitfinexBuyBinanceCommand(
                count, symbol, binanceKey, binanceSecret, bitfinexKey, bitfinexSecret);
    }
}
