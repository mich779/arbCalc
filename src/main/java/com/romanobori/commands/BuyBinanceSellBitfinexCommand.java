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
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Function;
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

    public BuyBinanceSellBitfinexCommand(String symbol,
                                         String binanceKey,
                                         String binanceSecret,
                                         String bitfinexKey,
                                         String bitfinexSecret,
                                         int count,
                                         BinanceOrderBookUpdated binanceOrderBookUpdated,
                                         BitfinexOrderBookUpdated bitfinexOrderBookUpdated, String binanceListeningKey) {
        super(count);
        this.binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        BitfinexClient bitfinexClient1 = new BitfinexClient(bitfinexKey, bitfinexSecret);
        this.bitfinexClient = new BitfinexClientApi(bitfinexClient1);
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.symbol = symbol;
        this.socketClient = new BinanceApiWebSocketClientImpl();
        this.binanceKey = binanceKey;
        this.binanceSecret = binanceSecret;
        this.bitfinexKey = bitfinexKey;
        this.bitfinexSecret = bitfinexSecret;
        this.binanceListeningKey = binanceListeningKey;
    }

    @Override
    LimitOrderDetails firstOrder() {
      ArbOrderEntry bestBid =   binanceOrderBookUpdated.getHighestBid();
      String orderId = Long.toString(
              binanceClient.newOrder(
                      new NewOrder(symbol, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "0.2", Double.toString(bestBid.getPrice()))
              ).getOrderId());

        return new LimitOrderDetails(orderId, bestBid.getPrice());
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double binanceHighestBidPrice = binanceOrderBookUpdated.getHighestBid().getPrice();
            double bitfinexHighestBidPrice = bitfinexOrderBookUpdated.getHighestBid().getPrice();

            return new ConditionStatus(binanceHighestBidPrice * 1.003508 <= bitfinexHighestBidPrice,
            binanceHighestBidPrice, bitfinexHighestBidPrice);
        };
    }

    @Override
    Function<Double,ConditionStatus> keepOrderCondition() {
        return (myBidPrice) -> {

            double bitfinexHighestBid = bitfinexOrderBookUpdated.getHighestBid().getPrice();

            return new ConditionStatus(myBidPrice * 1.003508 <= bitfinexHighestBid
                    && myBidPrice == binanceOrderBookUpdated.getHighestBid().getPrice() /// TODO: change restriction
,                    myBidPrice, bitfinexHighestBid);
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
                symbol, binanceKey, binanceSecret, bitfinexKey, bitfinexSecret, newCount, binanceOrderBookUpdated, bitfinexOrderBookUpdated,
                binanceListeningKey);
    }

    @Override
    String type() {
        return "BuyBinanceSellBitfinexCommand";
    }
}
