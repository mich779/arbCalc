package com.romanobori;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArbOrders {

    List<ArbOrderEntry> bids;
    List<ArbOrderEntry> asks;
    private long lastUpdateId;
    public ArbOrders(List<ArbOrderEntry> bids, List<ArbOrderEntry> asks){
        this.bids = bids;
        this.asks = asks;
    }

    public ArbOrders(List<ArbOrderEntry> bids, List<ArbOrderEntry> asks, long lastUpdateId) {
        this.bids = bids;
        this.asks = asks;
        this.lastUpdateId = lastUpdateId;
    }

    public ArbOrders sortByPrice(){
        List<ArbOrderEntry> sortedBids = bids.stream()
                .sorted(Comparator.comparingDouble(entry -> entry.price))
                .collect(Collectors.toList());

        List<ArbOrderEntry> sortedAsks = asks.stream()
                .sorted(Comparator.comparingDouble(entry -> entry.price))
                .collect(Collectors.toList());

        return new ArbOrders(sortedBids, sortedAsks);
    }

    public void setLastUpdateId(long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public long getLastUpdateId() {
        return lastUpdateId;
    }

    @Override
    public String toString() {
        return "ArbOrders{" +
                "bids=" + bids +
                ", asks=" + asks +
                '}';
    }
}
