package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderLimit;

import java.util.function.Function;
import java.util.function.Supplier;

public class BuyBitfinexSellBinanceCommand extends ArbCommand {

    private double rate = 1.002504;

    //private double rate = 1.001504;
    public BuyBitfinexSellBinanceCommand(int count, ArbContext context) {
        super(count, context);
    }

    @Override
    LimitOrderDetails firstOrder(ConditionStatus conditionStatus) {
        double amount = conditionStatus.getAmount();
        String orderId = context.getBitfinexClientApi().addArbOrder(new NewArbOrderLimit(
                context.getSymbol(), ARBTradeAction.BUY, amount, conditionStatus.getBitfinexPrice()
        ));
        return new LimitOrderDetails(orderId, conditionStatus.getBitfinexPrice(), amount);
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double bitfinexHighestBid = context.getBitfinexOrderBookUpdated().getHighestBid().getPrice();
            ArbOrderEntry highestBidBinance = context.getBinanceOrderBookUpdated().getHighestBid();
            double binanceHighestBid = highestBidBinance.getPrice();

            BitfinexUpdatedWallet bitfinexUpdatedWallet = context.getBitfinexUpdatedWallet();

            double amount =
                    Math.min(
                            Math.min(
                                    CommonFunctions.round(bitfinexUpdatedWallet.getFreeAmount("btc") / bitfinexHighestBid, 2),
                                    CommonFunctions.round(highestBidBinance.getAmount(), 2)), 0.2);

            return (amount < 0.2) ? new ConditionStatus(false, 0.0, 0.0, 0.0) :
                    new ConditionStatus(
                            bitfinexHighestBid * rate <= binanceHighestBid,
                            binanceHighestBid, bitfinexHighestBid, amount
                    );
        };
    }

    @Override
    Function<Double, ConditionStatus> keepOrderCondition() {
        return (myBidPrice) -> {
            double bitfinexHighestBid = myBidPrice;
            double binanceHighestBid = context.getBinanceOrderBookUpdated().getHighestBid().getPrice();
            return new ConditionStatus(
                    bitfinexHighestBid * rate <= binanceHighestBid
                            && myBidPrice == context.getBitfinexOrderBookUpdated().getHighestBid().getPrice(),
                    binanceHighestBid, bitfinexHighestBid, 0.0);
        };
    }

    @Override
    Function<String, Boolean> cancelOrder() {
        return (orderId -> {
            try {
                OrderManager orderManager = context.getBitfinexApiBroker().getOrderManager();
                orderManager.cancelOrder(Long.parseLong(orderId));
            } catch (APIException e) {
                return false;
            }
            return true;
        });
    }

    @Override
    Runnable secondOrder(double amount) {
        return () -> context.getBinanceClient()
                .newOrder(
                        new NewOrder(
                                context.getSymbol(),
                                OrderSide.SELL,
                                OrderType.MARKET,
                                TimeInForce.GTC,
                                Double.toString(amount)));

    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBitfinex(context.getBitfinexApiBroker());
    }

    @Override
    ArbCommand buildAnotherCommand(int count) {
        return new BuyBitfinexSellBinanceCommand(count, context);
    }

    @Override
    String type() {
        return "BuyBitfinexSellBinanceCommand";
    }
}
