package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.google.common.collect.ImmutableMap;
import com.romanobori.datastructures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinanceApiClient implements ApiClient {

    BinanceApiRestClient binanceApi;
    int orderBookLimit;
    Map<String, String> currencyToAdress = ImmutableMap.of("BTC", "", "NEO", "");
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


        ArbOrders orders = new ArbOrders(arbOrderEntryListBids, arbOrderEntryListAsks);
        orders.setLastUpdateId(orderBook.getLastUpdateId());

        return orders;
    }


    @Override
    public List<MyArbOrder> getMyOrders() {

        List<MyArbOrder> arbOpenOrders = new ArrayList<>();
        List<Order> binanceOpenOrders = binanceApi.getOpenOrders(new OrderRequest("VIBEETH"));

        for(Order order: binanceOpenOrders){
            arbOpenOrders.add(new MyArbOrder(order.getSymbol(), order.getOrderId().toString(),
                    Double.parseDouble(order.getPrice()), Double.parseDouble(order.getOrigQty()),
                    Double.parseDouble(order.getExecutedQty()),
                    getAction(order)
                    ,order.getTime()
            ));
        }

        return arbOpenOrders;
    }

    private ARBTradeAction getAction(Order order) {
        return order.getSide() == OrderSide.BUY  ? ARBTradeAction.BUY : ARBTradeAction.SELL;
    }

    private OrderSide getSide(NewArbOrder order) {
        return order.getAction()== ARBTradeAction.BUY  ? OrderSide.BUY : OrderSide.SELL;
    }

    @Override
    public String addArbOrder(NewArbOrderMarket order) {
        return  Long.toString(binanceApi.newOrder(
                new NewOrder(order.getSymbol(), getSide(order), OrderType.MARKET,
                        TimeInForce.GTC,
                        Double.toString(order.getQuantity())))
                .getOrderId());
    }

    @Override
    public String addArbOrder(NewArbOrderLimit order) {
         return  Long.toString(binanceApi.newOrder(
                new NewOrder(order.getSymbol(), getSide(order), OrderType.LIMIT,
                        TimeInForce.GTC,
                        Double.toString(order.getQuantity()),
                        Double.toString(order.getPrice())))
                .getOrderId());
    }


    @Override
    public void cancelOrder(String symbol, String orderId) {
        binanceApi.cancelOrder(new CancelOrderRequest(symbol, orderId));
    }

    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(ArbWalletEntry withdrawalDetails) {
        binanceApi.withdraw(withdrawalDetails.getCurrency(), currencyToAdress.get(withdrawalDetails.getCurrency())
                ,Double.toString(withdrawalDetails.getAmount()), "");
    }

    @Override
    public ArbWallet getWallet() {
        List<AssetBalance> balances = binanceApi.getAccount().getBalances();

        List<ArbWalletEntry> arbBalances = new ArrayList<>();
        for(AssetBalance assetBalance: balances){
            Double free = Double.parseDouble(assetBalance.getFree());
            arbBalances.add(new ArbWalletEntry(assetBalance.getAsset(),  free+Double.parseDouble(assetBalance.getLocked()), free));
        }
        return new ArbWallet(arbBalances);
    }

    public void setBinanceApi(BinanceApiRestClient binanceApi) {
        this.binanceApi = binanceApi;
    }
}

