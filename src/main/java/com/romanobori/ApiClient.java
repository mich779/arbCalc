package com.romanobori;

import java.util.List;

public abstract class ApiClient {

    abstract public ArbOrders getOrderBook(String symbol);
    abstract public List<MyArbOrder> getMyOrders();
    abstract public String addArbOrder(NewArbOrder order);
    abstract public void cancelOrder(MyArbOrder order);
    abstract public void cancelAllOrders();
    abstract public void withdrawal(ArbWalletEntry withdrawalDetails);

    abstract public ArbWallet getWallet();

    boolean isOrderDone(String orderId){
        List<MyArbOrder> myOrders = getMyOrders();

        for(MyArbOrder order : myOrders){
            if(order.orderId.equals(orderId)){
                if(order.executedQuantity == order.origQuantity){
                    return true;
                }else {
                   return false;
                }
            }
        }

        return false;
    }
}
