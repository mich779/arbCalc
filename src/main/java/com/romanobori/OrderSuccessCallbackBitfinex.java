package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExecutedTrade;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexExecutedTradeSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class OrderSuccessCallbackBitfinex extends OrderSuccessCallback {

    private BitfinexApiBroker bitfinexClient;

    public OrderSuccessCallbackBitfinex(BitfinexApiBroker bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }

    @Override
    public void register(String orderId, Runnable action, AtomicBoolean orderCompletionMarker) {
        final BitfinexExecutedTradeSymbol symbol = new BitfinexExecutedTradeSymbol(BitfinexCurrencyPair.BTC_USD);

        final QuoteManager quoteManager = bitfinexClient.getQuoteManager();

        final BiConsumer<BitfinexExecutedTradeSymbol, ExecutedTrade> callback = (sym, trade) -> {
            System.out.format("Got executed trade (%s) for symbol (%s)\n", trade, sym);
        };
        try {
            quoteManager.registerExecutedTradeCallback(symbol, callback);
            quoteManager.subscribeExecutedTrades(symbol);

// To unsubscribe the executed trades stream
            quoteManager.removeExecutedTradeCallback(symbol, callback);
            quoteManager.unsubscribeExecutedTrades(symbol);
        }catch (APIException e){

        }
    }
}
