package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.romanobori.ArbContext;
import com.romanobori.OrderSuccessCallback;
import com.romanobori.OrderSuccessCallbackBitfinex;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SellBitfinexBuyBinanceCommand extends ArbCommand {

    ArbContext context;
    private double rate = 1.002504;
    //private double rate = 1.001504;


    public SellBitfinexBuyBinanceCommand(int count, ArbContext context) {
        super(count);
        this.context = context;

    }

    @Override
    LimitOrderDetails firstOrder() {
        ArbOrderEntry bestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk();
        String orderId = context.getBitfinexClientApi().addArbOrder( new NewArbOrderLimit(
                context.getSymbol(), ARBTradeAction.SELL, 0.2, bestAsk.getPrice()
        ));
        return new LimitOrderDetails(orderId,bestAsk.getPrice());
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double binanceLowestAsk = context.getBinanceOrderBookUpdated().getLowestAsk().getPrice();
            double bitfinexLowestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice();

            return new ConditionStatus(
                    binanceLowestAsk * rate <= bitfinexLowestAsk,
                    binanceLowestAsk, bitfinexLowestAsk);
        };
    }

    @Override
    Function<Double,ConditionStatus> keepOrderCondition() {
        return (myAskPrice) -> {
            double binanceLowestAsk = context.getBinanceOrderBookUpdated().getLowestAsk().getPrice();
            double bitfinexLowestAsk = myAskPrice;
            return new ConditionStatus(
                    binanceLowestAsk * rate <= bitfinexLowestAsk
                    && myAskPrice == context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice(),
                    binanceLowestAsk, bitfinexLowestAsk);
        };
    }

    @Override
    Consumer<String> cancelOrder() {
        return (orderId ->{
            try {
                OrderManager orderManager = context.getBitfinexApiBroker().getOrderManager();
                orderManager.cancelOrder(Long.parseLong(orderId));
            } catch (APIException e) {
                throw new RuntimeException("could not connect to bitfinex client");
            }
        });
    }

    @Override
    Runnable secondOrder() {
        return () -> context.getBinanceClient().newOrder(new NewOrder(
                context.getSymbol(), OrderSide.BUY, OrderType.MARKET, TimeInForce.GTC, "0.2"
        ));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBitfinex(context.getBitfinexApiBroker());
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {

        return new SellBitfinexBuyBinanceCommand(
                count, context);
    }

    @Override
    String type() {
        return "SellBitfinexBuyBinanceCommand";
    }
}
