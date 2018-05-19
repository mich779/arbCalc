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

    private double precision = 0.005;
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
            double amount = Math.min(CommonFunctions.round(binanceUpdatedWallet.getFreeAmount("BTC") / binanceHighestBidPrice, 2),
                                    CommonFunctions.round(highestBidBitfinex.getAmount() * 0.75, 2));

            return (amount < 0.2) ? new ConditionStatus(false, 0.0, 0.0, 0.0) :
                    new ConditionStatus(
                            isPriceGapProfitable(bitfinexHighestBidPrice, binanceHighestBidPrice),
                            binanceHighestBidPrice,
                            bitfinexHighestBidPrice, amount);
        };
    }

    @Override
    Function<LimitOrderDetails, ConditionStatus> keepOrderCondition() {
        return (limitOrder) -> {

            ArbOrderEntry bitfinexHighestBid = context.getBitfinexOrderBookUpdated().getHighestBid();

            return new ConditionStatus(isPriceGapProfitable(bitfinexHighestBid.getPrice(), limitOrder.getPrice())
                    && isOrderPriceAtractive(limitOrder)
                    && isNotHigherThenMarketAmount(limitOrder.getAmount(), bitfinexHighestBid.getAmount()) /// TODO: change restriction
                    , limitOrder.getPrice(), bitfinexHighestBid.getPrice(), 0.0);
        };
    }

    private boolean isOrderPriceAtractive(LimitOrderDetails limitOrderDetails) {
        return limitOrderDetails.getPrice() >= (1-precision)*context.getBinanceOrderBookUpdated().getHighestBid().getPrice();
    }

    private boolean isPriceGapProfitable(double bitfinexHighestBid, Double price) {
        return price * rate <= bitfinexHighestBid;
    }

    private boolean isNotHigherThenMarketAmount(double limitOrderAmount, double marketAmount) {
        return limitOrderAmount <= marketAmount;
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
    AmountFillerDetectorObservable getAmountFillerDetector() {
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
