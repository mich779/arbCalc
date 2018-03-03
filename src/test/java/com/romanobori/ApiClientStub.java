package com.romanobori;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import support.BinanceApiWebSocketClientStub;

import java.util.List;

public class ApiClientStub implements ApiClient {

    NewArbOrder order;

    String orderId;

    boolean orderSuccess = true;

    String canceledOrder = null;

    BinanceApiWebSocketClientStub streamClient;



    public ApiClientStub(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public ArbOrders getOrderBook(String symbol) {
        throw new NotImplementedException();
    }

    @Override
    public List<MyArbOrder> getMyOrders() {
        throw new NotImplementedException();
    }

    @Override
    public String addArbOrder(NewArbOrderMarket order) {
        this.order = order;
        return this.orderId;
    }

    @Override
    public String addArbOrder(NewArbOrderLimit order) {
        this.order = order;
        return this.orderId;
    }

    @Override
    public void cancelOrder(String symbol, String orderId) {
        this.canceledOrder = orderId;
    }


    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(ArbWalletEntry withdrawalDetails) {

    }

    @Override
    public ArbWallet getWallet() {

        return null;
    }

    public  NewArbOrder getLatestOrder(){
        return order;
    }

    public void setOrderSuccess(boolean orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public String getCanceledOrder() {
        return canceledOrder;
    }
}
