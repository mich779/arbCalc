package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ArbOrders;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class BitfinexOrderBookUpdated {
    BitfinexApiBroker bitfinexClient;
    ArbOrders orderBook;
    String symbol;

    public BitfinexOrderBookUpdated(BitfinexClientApi bitfinexClientApi, BitfinexApiBroker bitfinexClient, String symbol) {
        orderBook = bitfinexClientApi.getOrderBook("NEOETH");
        this.bitfinexClient = bitfinexClient;
        this.symbol = symbol;
    }

    public void subscribe() {
        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                BitfinexCurrencyPair.NEO_ETH, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexClient.getOrderbookManager();
        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            if (entry.getCount() > 0.0) {
                setAmountOrAddByAmountSign(entry);
            } else if (entry.getCount() == 0.0) {
                filterAllEntriesWithPricebyAmount(entry.getPrice(), entry.getAmount());
            }
        };

        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        } catch (APIException e) {
            throw new RuntimeException(e);
        }
        orderbookManager.subscribeOrderbook(orderbookConfiguration);
    }

    private void setAmountOrAddByAmountSign(OrderbookEntry entry) {
        if (entry.getAmount() > 0) {
            orderBook.bids = setAmountOrAdd(entry, orderBook.bids);
        } else {
            orderBook.asks = setAmountOrAdd(new OrderbookEntry(entry.getPrice()
                            , entry.getCount(), Math.abs(entry.getAmount()))
                    , orderBook.asks);
        }
    }

    private void filterAllEntriesWithPricebyAmount(double price, double amount) {
        if (amount == 1.0) {
            orderBook.bids = removePriceFromList(price, orderBook.bids);
        } else if (amount == -1){
            orderBook.asks = removePriceFromList(price, orderBook.asks);
        }
    }

    private List<ArbOrderEntry> removePriceFromList(double price, List<ArbOrderEntry> bids) {
        return bids
                .stream()
                .filter(e -> e.price != price)
                .collect(Collectors.toList());
    }

    private List<ArbOrderEntry> setAmountOrAdd(OrderbookEntry entry, List<ArbOrderEntry> lst) {
        double price = entry.getPrice();
        if (listHasPrice(lst, price)) {
            updateLst(lst, entry.getAmount(), price);
        } else {
            addToList(entry, lst);
        }
        return lst;
    }

    private boolean listHasPrice(List<ArbOrderEntry> lst, double price) {
        return lst.stream().anyMatch(arbOrderEntry -> arbOrderEntry.price == price);
    }

    private void addToList(OrderbookEntry entry, List<ArbOrderEntry> lst) {
        lst.add(new ArbOrderEntry(entry.getPrice(), entry.getAmount()));
    }

    private void updateLst(List<ArbOrderEntry> lst, double amount, double price) {
        ArbOrderEntry arbOrderEntry = lst.stream().filter(entry -> entry.price == price).findFirst().get();

        arbOrderEntry.setAmount(amount);
    }

    public double getLowestAsk() {
        return getMin(orderBook.asks);
    }

    public double getHighestAsk() {
        return getMax(orderBook.asks);
    }

    public double getMinBid() {
        return getMin(orderBook.bids);
    }

    public double getHighestBid() {
        return getMax(orderBook.bids);
    }

    private double getMax(List<ArbOrderEntry> entries) {
        Optional<ArbOrderEntry> max = entries.stream()
                .max(Comparator.comparingDouble(entry -> entry.price));
        return getOrException(max);
    }

    private double getMin(List<ArbOrderEntry> entries) {
        Optional<ArbOrderEntry> min = entries.stream()
                .min(Comparator.comparingDouble(entry -> entry.price));
        return getOrException(min);
    }

    private double getOrException(Optional<ArbOrderEntry> min) {
        if (!min.isPresent()) {
            throw new RuntimeException("asd");
        } else {
            return min.get().price;
        }
    }

    public void setOrderBook(ArbOrders orderBook) {
        this.orderBook = orderBook;
    }
}
