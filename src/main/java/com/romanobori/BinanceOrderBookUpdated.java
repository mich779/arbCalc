package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BinanceOrderBookUpdated {
    BinanceApiWebSocketClient streamClient;
    BinanceApiRestClient client;
    OrderBook updatedOrderBook;
    public long last_u = 0;
    boolean canUpdate = false;
    int updates  = 0;

    public BinanceOrderBookUpdated(BinanceApiWebSocketClient streamClient, BinanceApiRestClient client) {
        this.streamClient = streamClient;
        this.client = client;
        register();
        this.updatedOrderBook = client.getOrderBook("NEOETH", 100);
    }

    public void register(){
        streamClient.onDepthEvent("neoeth", response -> {
            long lastUpdateId = updatedOrderBook.getLastUpdateId();
            if (response.getFinalUpdateId() < lastUpdateId)
                return;

            if(canUpdate){
                if(response.getFirstUpdateId() == this.last_u +1) {
                    update(response);
                }
            }
            else if (response.getFirstUpdateId() <= lastUpdateId + 1 && response.getFinalUpdateId() >= lastUpdateId + 1) {
                canUpdate = true;
                update(response);
            }
        });
    }

    private void update(DepthEvent response) {

        response.getAsks().forEach(ask -> updateWithNewAsk(ask, updatedOrderBook.getAsks()));

        response.getBids().forEach(bid -> updateWithNewBid(bid, updatedOrderBook.getBids()));

        this.last_u = response.getFinalUpdateId();

        updates++;

    }

    private void updateWithNewAsk(OrderBookEntry entry, List<OrderBookEntry> origAsks) {
        double quantity = Double.parseDouble(entry.getQty());
        Optional<OrderBookEntry> first = origAsks.stream()
                .filter(orderBookEntry -> orderBookEntry.getPrice().equals(entry.getPrice()))
                .findFirst();

        insertOdAdd(entry, origAsks, quantity, first);
    }
    private void updateWithNewBid(OrderBookEntry entry, List<OrderBookEntry> origBids) {
        double quantity = Double.parseDouble(entry.getQty());
        Optional<OrderBookEntry> first = origBids.stream()
                .filter(orderBookEntry -> orderBookEntry.getPrice().equals(entry.getPrice()))
                .findFirst();

        insertOdAdd(entry, origBids, quantity, first);
    }

    private void insertOdAdd(OrderBookEntry entry, List<OrderBookEntry> origBids, double quantity, Optional<OrderBookEntry> first) {
        if (quantity == 0.0) {
            first.ifPresent(origBids::remove);
        }else{
            if(first.isPresent()){
                OrderBookEntry orderBookEntry = first.get();
                orderBookEntry.setQty(entry.getQty());
            }else{
                origBids.add(entry);
            }
        }
    }

    public OrderBook getUpdatedOrderBook() {
        return updatedOrderBook;
    }
}
