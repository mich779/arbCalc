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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SellBitfinexBuyBinanceCommand extends ArbCommand {

    private double rate = 1.002504;
    //private double rate = 1.001504;


    public SellBitfinexBuyBinanceCommand(int count, ArbContext context) {
        super(count, context);

    }

    @Override
    LimitOrderDetails firstOrder(ConditionStatus conditionStatus) {
        double amount = conditionStatus.getAmount();
        String orderId = context.getBitfinexClientApi().addArbOrder(new NewArbOrderLimit(
                context.getSymbol(), ARBTradeAction.SELL, amount, conditionStatus.getBitfinexPrice()));

        return new LimitOrderDetails(orderId, conditionStatus.getBitfinexPrice(), amount);
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            ArbOrderEntry lowestAskBinance = context.getBinanceOrderBookUpdated().getLowestAsk();
            double lowestAskBinancePrice = lowestAskBinance.getPrice();
            double bitfinexLowestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice();
            BitfinexUpdatedWallet bitfinexUpdatedWallet = context.getBitfinexUpdatedWallet();
            double amount =
                    Math.min(
                            Math.min(
                                    CommonFunctions.round(bitfinexUpdatedWallet.getFreeAmount("neo"), 2),
                                    CommonFunctions.round(lowestAskBinance.getAmount() * 0.75, 2)), 0.2);

            return (amount < 0.2) ? new ConditionStatus(false, 0.0, 0.0, 0.0) :
                    new ConditionStatus(
                            lowestAskBinancePrice * rate <= bitfinexLowestAsk,
                            lowestAskBinancePrice, bitfinexLowestAsk, amount);
        };
    }

    @Override
    Function<LimitOrderDetails, ConditionStatus> keepOrderCondition() {
        return (limitOrderDetails) -> {
            double binanceLowestAsk = context.getBinanceOrderBookUpdated().getLowestAsk().getPrice();
            double bitfinexLowestAsk = limitOrderDetails.getPrice();
            return new ConditionStatus(
                    binanceLowestAsk * rate <= bitfinexLowestAsk
                            && limitOrderDetails.getPrice() == context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice(),
                    binanceLowestAsk, bitfinexLowestAsk, 0.0);
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
    Consumer<Double> secondOrder() {
        return (amount) -> context.getBinanceClient().newOrder(
                new NewOrder(
                        context.getSymbol(),
                        OrderSide.BUY,
                        OrderType.MARKET,
                        TimeInForce.GTC,
                        Double.toString(amount)
                ));
    }

    @Override
    AmountFillerDetectorObservable getAmountFillerDetector() {
        return new AmountFillerDetectorBitfinex(context.getBitfinexApiBroker());
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
