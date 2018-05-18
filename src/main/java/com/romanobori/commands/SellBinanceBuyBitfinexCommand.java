package com.romanobori.commands;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.romanobori.*;
import com.romanobori.datastructures.ARBTradeAction;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.NewArbOrderMarket;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SellBinanceBuyBitfinexCommand extends ArbCommand {
    private double rate = 1.003508;
    //private double rate = 1.001508;

    public SellBinanceBuyBitfinexCommand(int count, ArbContext context) {
        super(count, context);
    }

    @Override
    LimitOrderDetails firstOrder(ConditionStatus conditionStatus) {
        double binancePrice = conditionStatus.getBinancePrice();
        double amount = conditionStatus.getAmount();
        String orderId = Long.toString(context.getBinanceClient().newOrder(new NewOrder(
                context.getSymbol(),
                OrderSide.SELL,
                OrderType.LIMIT,
                TimeInForce.GTC,
                Double.toString(amount),
                Double.toString(binancePrice)
        )).getOrderId());

        return new LimitOrderDetails(orderId, binancePrice, amount);
    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {

        return () -> {
            ArbOrderEntry lowestAskBitfinex = context.getBitfinexOrderBookUpdated().getLowestAsk();
            double lowestAskBitfinexPrice = lowestAskBitfinex.getPrice();
            double binanceLowestAsk = context.getBinanceOrderBookUpdated().getLowestAsk().getPrice();
            BinanceUpdatedWallet binanceUpdatedWallet = context.getBinanceUpdatedWallet();

            double amount = Math.min(Math.min(
                    CommonFunctions.round(binanceUpdatedWallet.getFreeAmount("NEO"), 2),
                    CommonFunctions.round(lowestAskBitfinex.getAmount() * 0.75, 2)), 0.2);

            return (amount < 0.2) ? new ConditionStatus(false, 0.0, 0.0, 0.0) :
                    new ConditionStatus(
                            lowestAskBitfinexPrice * rate <= binanceLowestAsk,
                            binanceLowestAsk,
                            lowestAskBitfinexPrice,
                            amount
                    );
        };
    }

    @Override
    Function<LimitOrderDetails, ConditionStatus> keepOrderCondition() {
        return (limitOrderDetails) -> {
            double bitfinexLowestAsk = context.getBitfinexOrderBookUpdated().getLowestAsk().getPrice();
            double binanceLowestAsk = limitOrderDetails.getPrice();
            return new ConditionStatus(
                    bitfinexLowestAsk * rate <= binanceLowestAsk
                            && limitOrderDetails.getPrice() == context.getBinanceOrderBookUpdated().getLowestAsk().getPrice(),
                    binanceLowestAsk,
                    bitfinexLowestAsk,
                    0.0
            );
        };
    }

    @Override
    Function<String, Boolean> cancelOrder() {
        return (orderId) -> cancel(() -> context.getBinanceClient().cancelOrder(new CancelOrderRequest(
                context.getSymbol(), Long.parseLong(orderId))));
    }

    @Override
    Consumer<Double> secondOrder() {

        return (amount) -> context.getBitfinexClientApi().addArbOrder(new NewArbOrderMarket(
                context.getSymbol(), ARBTradeAction.BUY, amount
        ));
    }

    @Override
    AmountFillerDetectorObservable getAmountFillerDetector() {
        return new AmountFillerDetectorBinance(context.getBinanceSocketClient(), context.getBinanceListeningKey());
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
