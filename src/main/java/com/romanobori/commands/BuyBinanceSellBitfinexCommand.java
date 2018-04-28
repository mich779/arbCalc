package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.romanobori.ArbContext;
import com.romanobori.OrderSuccessCallback;
import com.romanobori.OrderSuccessCallbackBinance;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BuyBinanceSellBitfinexCommand extends ArbCommand {
    private ArbContext context;

    //private double rate = 1.003508;
    private double rate = 1.001508;

    public BuyBinanceSellBitfinexCommand(int count, ArbContext context){
        super(count);
        this.context = context;

    }

    @Override
    LimitOrderDetails firstOrder() {
      ArbOrderEntry bestBid =   context.getBinanceOrderBookUpdated().getHighestBid();
      String orderId = Long.toString(
              context.getBinanceClient().newOrder(
                      new NewOrder(context.getSymbol(), OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "0.2", Double.toString(bestBid.getPrice()))
              ).getOrderId());

        return new LimitOrderDetails(orderId, bestBid.getPrice());
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double binanceHighestBidPrice = context.getBinanceOrderBookUpdated().getHighestBid().getPrice();
            double bitfinexHighestBidPrice = context.getBitfinexOrderBookUpdated().getHighestBid().getPrice();
            return new ConditionStatus(binanceHighestBidPrice * rate <= bitfinexHighestBidPrice,
            binanceHighestBidPrice, bitfinexHighestBidPrice);
        };
    }

    @Override
    Function<Double, ConditionStatus> keepOrderCondition() {
        return (myBidPrice) -> {

            double bitfinexHighestBid = context.getBitfinexOrderBookUpdated().getHighestBid().getPrice();

            return new ConditionStatus(myBidPrice * rate <= bitfinexHighestBid
                    && myBidPrice == context.getBinanceOrderBookUpdated().getHighestBid().getPrice() /// TODO: change restriction
,                    myBidPrice, bitfinexHighestBid);
        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId) -> context.getBinanceClient().cancelOrder(new CancelOrderRequest(context.getSymbol(), orderId));
    }

    @Override
    Runnable secondOrder() {
        return () -> context.getBitfinexClientApi().addArbOrder(new NewArbOrderMarket(
                context.getSymbol(), ARBTradeAction.SELL, 0.2
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBinance(context.getBinanceSocketClient(), context.getBinanceListeningKey());
    }

    @Override
    ArbCommand buildAnotherCommand(int newCount) {
        return new BuyBinanceSellBitfinexCommand(
                newCount, context);
    }

    @Override
    String type() {
        return "BuyBinanceSellBitfinexCommand";
    }
}
