package com.romanobori;

import java.util.List;

public abstract class ApiClient {

    abstract public ArbOrders getOrderBook(String symbol);
    abstract public List<MyArbOrder> getMyOrders();
    abstract public String addArbOrder(NewArbOrderMarket order);
    abstract public String addArbOrder(NewArbOrderLimit order);
    public abstract void cancelOrder(String symbol, String orderId);

    abstract public void cancelAllOrders();
    abstract public void withdrawal(ArbWalletEntry withdrawalDetails);

    abstract public ArbWallet getWallet();

    boolean isOrderDone(String orderId){

        return getMyOrders().stream().anyMatch(order ->
                order.orderId.equals(orderId) &&
                        order.executedQuantity == order.origQuantity
            );


    }
}
