package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class BitfinexOrderBookUpdated {
    BitfinexApiBroker bitfinexClient;
    ArbOrders orderBook;
    int counter;

    public BitfinexOrderBookUpdated(BitfinexClientApi bitfinexClientApi, BitfinexApiBroker bitfinexClient ){
        orderBook = bitfinexClientApi.getOrderBook("NEOETH");
        this.bitfinexClient = bitfinexClient;
        subscribe();
    }

    private void subscribe(){
        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                BitfinexCurrencyPair.NEO_ETH, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexClient.getOrderbookManager();
        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {

            if(entry.getCount() > 0){
                setAmountOrAdd(entry, entry.getAmount() > 0 ? orderBook.bids : orderBook.asks, entry.getPrice());
                counter ++;
            }else{
                if(entry.getAmount() == 1.0){
                    orderBook.bids.removeIf(e -> e.price == entry.getPrice());
                }else{
                    orderBook.asks.removeIf(e -> e.price == entry.getPrice());
                }
            }
        };

        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        } catch (APIException e) {
            throw new RuntimeException(e);
        }
        orderbookManager.subscribeOrderbook(orderbookConfiguration);


    }

    private void setAmountOrAdd(OrderbookEntry entry, List<ArbOrderEntry> lst, double price) {
        Optional<ArbOrderEntry> arbEntry = lst.stream()
                .filter(arbOrderEntry -> arbOrderEntry.price == price)
                .findFirst();

        if(arbEntry.isPresent()){
            ArbOrderEntry arbOrderEntry = arbEntry.get();
            arbOrderEntry.setAmount(entry.getAmount());
        }else{
            lst.add(new ArbOrderEntry(entry.getPrice(), entry.getAmount()));
        }
    }

    public int getCounter() {
        return counter;
    }

    public ArbOrders getOrderBook() {
        return orderBook;
    }
}
