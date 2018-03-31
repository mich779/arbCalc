package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SellBinanceBuyBitfinexCommand extends ArbCommand {
    String binanceKey;
    String binanceSecret;
    String symbol;
    String bitfinexKey;
    String bitfinexSecret;
    BinanceOrderBookUpdated binanceOrderBookUpdated;
    BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    BinanceApiRestClientImpl binanceClient;
    BitfinexClientApi bitfinexClient;
    String binanceListeningKey;
    BinanceApiWebSocketClientImpl socketClient;


    public SellBinanceBuyBitfinexCommand(int count, String binanceKey, String binanceSecret, String symbol, String bitfinexKey, String bitfinexSecret, BinanceOrderBookUpdated binanceOrderBookUpdated, BitfinexOrderBookUpdated bitfinexOrderBookUpdated, String binanceListeningKey) {
        super(count);
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;
        this.symbol = symbol;
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        BitfinexClient bitfinexClient1 = new BitfinexClient(bitfinexKey, bitfinexSecret);
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        this.bitfinexClient = new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret));
        this.binanceListeningKey = binanceListeningKey;
        this.socketClient = new BinanceApiWebSocketClientImpl();
    }

    @Override
    String firstOrder() {
        return Long.toString(binanceClient.newOrder(new NewOrder(
                symbol,
                OrderSide.SELL,
                OrderType.LIMIT,
                TimeInForce.GTC,
                "0.2",
                Double.toString(binanceOrderBookUpdated.getLowestAsk())
        )).getOrderId());
    }

    @Override
    Supplier<ConditionStatus> condition() {

        return () -> {
            double bitfinexLowestAsk = bitfinexOrderBookUpdated.getLowestAsk();
            double binanceLowestAsk = binanceOrderBookUpdated.getLowestAsk();
            return new ConditionStatus(
                    bitfinexLowestAsk * 1.002505 <= binanceLowestAsk,
                    binanceLowestAsk,
                    bitfinexLowestAsk

            );

        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId) -> binanceClient.cancelOrder( new CancelOrderRequest(
                symbol, Long.parseLong(orderId)
        ));
    }

    @Override
    Runnable secondOrder() {

        return () -> bitfinexClient.addArbOrder(new NewArbOrderMarket(
                symbol, ARBTradeAction.BUY, 0.2
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback()
    {
        return new OrderSuccessCallbackBinance(socketClient, binanceListeningKey);
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {
        return new SellBinanceBuyBitfinexCommand(
                count, binanceKey, binanceSecret, symbol, bitfinexKey, bitfinexSecret,
                binanceOrderBookUpdated, bitfinexOrderBookUpdated, binanceListeningKey);
    }

    @Override
    String type() {
        return "SellBinanceBuyBitfinexCommand";
    }
}
