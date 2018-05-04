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

public class SellBinanceBuyBitfinexCommand extends ArbCommand {
    ArbContext context;
    private double rate = 1.003508;
    //private double rate = 1.001508;

    public SellBinanceBuyBitfinexCommand(int count, ArbContext context) {
        super(count);
        this.context = context;
    }

    @Override
    LimitOrderDetails firstOrder() {
        ArbOrderEntry bestAsk = context.getBinanceOrderBookUpdated().getLowestAsk();
        String orderId = Long.toString(context.getBinanceClient().newOrder(new NewOrder(
                context.getSymbol(),
                OrderSide.SELL,
                OrderType.LIMIT,
                TimeInForce.GTC,
                "0.2",
                Double.toString(bestAsk.getPrice())
        )).getOrderId());

        return new LimitOrderDetails(orderId,bestAsk.getPrice());
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {

        return () -> {
            double bitfinexLowestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice();
            double binanceLowestAsk = context.getBinanceOrderBookUpdated().getLowestAsk().getPrice();

            return new ConditionStatus(
                    bitfinexLowestAsk * rate <= binanceLowestAsk,
                    binanceLowestAsk,
                    bitfinexLowestAsk
            );
        };
    }

    @Override
    Function<Double,ConditionStatus> keepOrderCondition() {
        return (myAskPrice) -> {
            double bitfinexLowestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice();
            double binanceLowestAsk = myAskPrice;
            return new ConditionStatus(
                    bitfinexLowestAsk * rate <= binanceLowestAsk
                    && myAskPrice == context.getBinanceOrderBookUpdated().getLowestAsk().getPrice(),
                    binanceLowestAsk,
                    bitfinexLowestAsk
            );
        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId) -> context.getBinanceClient().cancelOrder( new CancelOrderRequest(
                context.getSymbol(), Long.parseLong(orderId)
        ));
    }

    @Override
    Runnable secondOrder() {

        return () -> context.getBitfinexClientApi().addArbOrder(new NewArbOrderMarket(
                context.getSymbol(), ARBTradeAction.BUY, 0.2
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback()
    {
        return new OrderSuccessCallbackBinance(context.getBinanceSocketClient(), context.getBinanceListeningKey());
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {
        return new SellBinanceBuyBitfinexCommand(
                count, context);
    }

    @Override
    String type() {
        return "SellBinanceBuyBitfinexCommand";
    }
}
