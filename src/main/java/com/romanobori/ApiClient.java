package com.romanobori;

import com.romanobori.datastructures.*;

import java.util.List;

public interface ApiClient {
    ArbOrders getOrderBook(String symbol);
    List<MyArbOrder> getMyOrders();
    String addArbOrder(NewArbOrderMarket order);
    String addArbOrder(NewArbOrderLimit order);
    void cancelOrder(String symbol, String orderId);

    void cancelAllOrders();
    void withdrawal(ArbWalletEntry withdrawalDetails);

    ArbWallet getWallet();
}
