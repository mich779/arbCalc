package com.romanobori;

import java.util.List;

public interface ApiClient {

    public ArbOrders getOrderBook(String symbol);
    public List<MyArbOrder> getMyOrders();
    public void addArbOrder(NewArbOrder order);
    public void cancelOrder(MyArbOrder order);
    public void cancelAllOrders();
    public void withdrawal(ArbWalletEntry withdrawalDetails);

    public ArbWallet getWallet();

}
