package com.romanobori;

import java.util.List;

public interface ApiClient {

    public ArbOrders getOrderBook(String symbol);
    public List<MyArbOrder> getMyOrders();
    public void addArbOrder(NewArbOrder order);
    public void cancelOrder(long orderId);
    public void cancelAllOrders();
    public void withdrawal(long withrawalId);
    public ArbWallet getWallet();

}
