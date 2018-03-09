package com.romanobori;

import java.util.function.Supplier;

public class ArbCommandBuyBinanceSellBitfinex extends ArbCommand {
    public ArbCommandBuyBinanceSellBitfinex(ArbPredicate predicate) {
        super(predicate);
    }

    @Override
    Supplier<String> firstOrder() {
        return binanceApi.addArbOrder(new NewArbOrderLimit("NEOETH", ARBTradeAction.BUY, amount
                ,highestBinanceAsk.price ));
    }

    @Override
    Supplier<Boolean> condition() {
        return () -> highestBinanceBid.price * 1.003 <= highestBitfinexBid.price;
    }

    @Override
    Runnable cancelOrder() {
        binanceApi.cancelOrder(this.symbol, orderId);
    }

    @Override
    Runnable secondOrder() {
        return () -> bitfinexApi.addArbOrder(new NewArbOrderMarket("NEOETH", ARBTradeAction.SELL, amount));
    }

    @Override
    OrderSuccessCallback getOrderSuccessCallback() {
        return new OrderSuccessCallbackBinance(
                socketClient
        );
    }
}
