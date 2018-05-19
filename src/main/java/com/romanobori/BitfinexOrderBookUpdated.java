package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ArbOrders;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;

public class BitfinexOrderBookUpdated {
    private static final String BIDS  = "BIDS";
    private static final String ASKS  = "ASKS";
    private long lastUpdate;
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    private BitfinexClientApi bitfinexClientApi;
    private BitfinexApiBroker bitfinexClient;
    private BitfinexCurrencyPair bitfinexCurrencyPair;
    private static final int updateInterval = 5 * 60 * 1000;
    private final String symbol;

    public BitfinexOrderBookUpdated(String symbol,
                                    BitfinexClientApi bitfinexClientApi,
                                    BitfinexApiBroker bitfinexClient,
                                    BitfinexCurrencyPair bitfinexCurrencyPair) throws APIException {
        this.symbol = symbol;
        this.bitfinexClientApi = bitfinexClientApi;
        this.bitfinexClient = bitfinexClient;
        this.bitfinexCurrencyPair = bitfinexCurrencyPair;
        initializeNewOrderbookFromBitfinex(symbol);
        startDepthEventStreaming();

    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    private void initializeNewOrderbookFromBitfinex(String symbol) {

        lastUpdate = System.currentTimeMillis();

        ArbOrders orderBook = bitfinexClientApi.getOrderBook(symbol);

        this.depthCache = new HashMap<>();

        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (ArbOrderEntry ask : orderBook.getAsks()) {
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getAmount()));
        }
        depthCache.put(ASKS, asks);

        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (ArbOrderEntry bid : orderBook.getBids()) {
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getAmount()));
        }
        depthCache.put(BIDS, bids);
    }

    /**
     * Begins streaming of depth events.
     */
    private void startDepthEventStreaming() {
        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                this.bitfinexCurrencyPair, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexClient.getOrderbookManager();

        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            if(isTimeToUpdate()){
                initializeNewOrderbookFromBitfinex(symbol);
            }else {
                updateOrderBookWithEntry(entry);
            }
        };

        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        } catch (APIException e) {
            throw new RuntimeException(e);
        }
        orderbookManager.subscribeOrderbook(orderbookConfiguration);
    }

    private void updateOrderBookWithEntry(OrderbookEntry entry) {
        if (entry.getCount() > 0.0) {
            if(entry.getAmount() > 0) {
                updateOrderBook(getBids(), entry);
            }else{
                updateOrderBook(getAsks(), entry);
            }
        } else if (entry.getCount() == 0.0) {
            if(entry.getAmount() == 1.0){
                remove(entry.getPrice(), depthCache.get(BIDS));
            }else if ( entry.getAmount() == -1.0){
                remove(entry.getPrice(), depthCache.get(ASKS));
            }
        }
    }


    private boolean isTimeToUpdate(){
        return System.currentTimeMillis() - lastUpdate > updateInterval;
    }


    private boolean isTimeToCheckOrderBook(long createdTime) {
        return (System.currentTimeMillis() - createdTime)%(1000*60) == 0;
    }

    private boolean doesBestBidsAndBestAskMatch(BitfinexOrderBookUpdated newOrderBook) {
        return newOrderBook.getBestAsk().equals(this.getBestAsk()) &&
                newOrderBook.getBestBid().equals(this.getBestBid());
    }

    private void remove(double price, NavigableMap<BigDecimal, BigDecimal> bids) {
        bids.remove(new BigDecimal(price));
    }

    /**
     * Updates an order book (bids or asks) with a delta received from the server.
     *
     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
     */
    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, OrderbookEntry orderBookDeltas) {
            lastOrderBookEntries.put(new BigDecimal(orderBookDeltas.getPrice()),
                    new BigDecimal(Math.abs(orderBookDeltas.getAmount())));
    }

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASKS);
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BIDS);
    }

    /**
     * @return the best ask in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
        return getAsks().lastEntry();
    }

    /**
     * @return the best bid in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
        return getBids().firstEntry();
    }

    /**
     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
     */
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
        return depthCache;
    }

    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public ArbOrderEntry getLowestAsk(){
        return new ArbOrderEntry(getBestAsk().getKey().doubleValue(),
                getBestAsk().getValue().doubleValue());
    }

    public ArbOrderEntry getHighestBid(){
        return new ArbOrderEntry(getBestBid().getKey().doubleValue(),getBestBid().getValue().doubleValue());
    }
}
