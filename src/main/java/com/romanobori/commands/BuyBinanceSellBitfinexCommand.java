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

public class BuyBinanceSellBitfinexCommand extends ArbCommand {

    private double rate = 1.003508;
    //private double rate = 1.001508;

    public BuyBinanceSellBitfinexCommand(int count, ArbContext context) {
        super(count, context);

    }

    @Override
    LimitOrderDetails firstOrder(ConditionStatus conditionStatus) {
        double binancePrice = conditionStatus.getBinancePrice();
        double amount = conditionStatus.getAmount();
        String orderId = Long.toString(
                context.getBinanceClient().newOrder(
                        new NewOrder(
                                context.getSymbol(),
                                OrderSide.BUY,
                                OrderType.LIMIT,
                                TimeInForce.GTC,
                                Double.toString(amount),
                                Double.toString(binancePrice))
                ).getOrderId());

        return new LimitOrderDetails(orderId, binancePrice, amount);

    }

    @Override
    Supplier<ConditionStatus> placeOrderCondition() {
        return () -> {
            double binanceHighestBidPrice = context.getBinanceOrderBookUpdated().getHighestBid().getPrice();
            ArbOrderEntry highestBidBitfinex = context.getBitfinexOrderBookUpdated().getHighestBid();
            double bitfinexHighestBidPrice = highestBidBitfinex.getPrice();
            BinanceUpdatedWallet binanceUpdatedWallet = context.getBinanceUpdatedWallet();
            double amount =
                    Math.min(
                            Math.min(CommonFunctions.round(binanceUpdatedWallet.getFreeAmount("BTC") / binanceHighestBidPrice, 2),
                                    CommonFunctions.round(highestBidBitfinex.getAmount() * 0.75, 2)), 0.2);

            return (amount < 0.2) ? new ConditionStatus(false, 0.0, 0.0, 0.0) :
                    new ConditionStatus(
                            binanceHighestBidPrice * rate <= bitfinexHighestBidPrice,
                            binanceHighestBidPrice,
                            bitfinexHighestBidPrice, amount);
        };
    }

    @Override
    Function<LimitOrderDetails, ConditionStatus> keepOrderCondition() {
        return (limitOrderDetails) -> {

            double bitfinexHighestBid = context.getBitfinexOrderBookUpdated().getHighestBid().getPrice();

            return new ConditionStatus(limitOrderDetails.getPrice() * rate <= bitfinexHighestBid
                    && limitOrderDetails.getPrice() == context.getBinanceOrderBookUpdated().getHighestBid().getPrice() /// TODO: change restriction
                    , limitOrderDetails.getPrice(), bitfinexHighestBid, 0.0);
        };
    }

    @Override
    Function<String, Boolean> cancelOrder() {
        return (orderId) ->
                cancel(() -> context.getBinanceClient().cancelOrder(new CancelOrderRequest(context.getSymbol(), orderId)));
    }

    @Override
    Consumer<Double> secondOrder() {
        return (amount) -> context.getBitfinexClientApi().addArbOrder(new NewArbOrderMarket(
                context.getSymbol(), ARBTradeAction.SELL, amount
        ));
    }

    @Override
    AmountFillerDetector getAmountFillerDetector() {
        return new AmountFillerDetectorBinance(context.getBinanceSocketClient(), context.getBinanceListeningKey());
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
