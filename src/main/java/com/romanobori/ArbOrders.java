package com.romanobori;

import java.util.List;

public class ArbOrders {

    List<ArbOrderEntry> bids;
    List<ArbOrderEntry> asks;

    public ArbOrders(List<ArbOrderEntry> bids, List<ArbOrderEntry> asks){
        this.bids = bids;
        this.asks = asks;
    }
}
