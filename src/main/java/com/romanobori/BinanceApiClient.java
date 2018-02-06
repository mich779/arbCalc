package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.util.ArrayList;
import java.util.List;

public class BinanceApiClient implements ApiClient {

    BinanceApiRestClient binanceApi;
    int orderBookLimit;

    public BinanceApiClient(BinanceApiRestClient binanceApi, int orderBookLimit) {
        this.binanceApi = binanceApi;
        this.orderBookLimit = orderBookLimit;
    }

    @Override
    public ArbOrders getOrderBook(String symbol) {

        OrderBook orderBook = binanceApi.getOrderBook(symbol, orderBookLimit);
        List<ArbOrderEntry> arbOrderEntryListBids =  new ArrayList<>();
        List<ArbOrderEntry> arbOrderEntryListAsks =  new ArrayList<>();


        for(OrderBookEntry order: orderBook.getBids()){
            arbOrderEntryListBids.add(new ArbOrderEntry(Double.parseDouble(order.getPrice()),
                    Double.parseDouble(order.getQty())));
        }

        for(OrderBookEntry order: orderBook.getAsks()){
            arbOrderEntryListAsks.add(new ArbOrderEntry(Double.parseDouble(order.getPrice()),
                    Double.parseDouble(order.getQty())));
        }

     //   arbOrderEntryListAsks.add(new ArbOrderEntry(Double.parseDouble("0.1"),Double.parseDouble("3")));
     //   arbOrderEntryListBids.add(new ArbOrderEntry(Double.parseDouble("0.1"),Double.parseDouble("3")));
        return new ArbOrders(arbOrderEntryListBids, arbOrderEntryListAsks);
    }


    @Override
    public List<MyArbOrder> getMyOrders() {
        return null;
    }

    @Override
    public void addArbOrder(NewArbOrder order) {

    }

    @Override
    public void cancelOrder(long orderId) {

    }

    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(long withrawalId) {

    }

    @Override
    public ArbWallet getWallet() {
        return null;
    }
}
