package support;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.account.request.AllOrdersRequest;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.*;

import java.util.List;

public class BinanceApiRestClientStub implements BinanceApiRestClient {


    private NewOrder newOrder ;
    @Override
    public void ping() {

    }

    @Override
    public Long getServerTime() {
        return null;
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return null;
    }

    @Override
    public OrderBook getOrderBook(String symbol, Integer limit) {
        return null;
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol, String fromId, Integer limit, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol) {
        return null;
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return null;
    }

    @Override
    public TickerStatistics get24HrPriceStatistics(String symbol) {
        return null;
    }

    @Override
    public List<TickerPrice> getAllPrices() {
        return null;
    }

    @Override
    public List<BookTicker> getBookTickers() {
        return null;
    }

    @Override
    public NewOrderResponse newOrder(NewOrder order) {
        this.newOrder = order;
        NewOrderResponse response = new NewOrderResponse();
        response.setOrderId(100l);
        return response;
    }

    @Override
    public void newOrderTest(NewOrder order) {

    }

    @Override
    public Order getOrderStatus(OrderStatusRequest orderStatusRequest) {
        return null;
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {

    }

    @Override
    public List<Order> getOpenOrders(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public List<Order> getAllOrders(AllOrdersRequest orderRequest) {
        return null;
    }

    @Override
    public Account getAccount(Long recvWindow, Long timestamp) {
        return null;
    }

    @Override
    public Account getAccount() {
        return null;
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit, Long fromId, Long recvWindow, Long timestamp) {
        return null;
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit) {
        return null;
    }

    @Override
    public List<Trade> getMyTrades(String symbol) {
        return null;
    }

    @Override
    public void withdraw(String asset, String address, String amount, String name) {

    }

    @Override
    public DepositHistory getDepositHistory(String asset) {
        return null;
    }

    @Override
    public WithdrawHistory getWithdrawHistory(String asset) {
        return null;
    }

    @Override
    public DepositAddress getDepositAddress(String asset) {
        return null;
    }

    @Override
    public String startUserDataStream() {
        return null;
    }

    @Override
    public void keepAliveUserDataStream(String listenKey) {

    }

    @Override
    public void closeUserDataStream(String listenKey) {

    }

    public NewOrder getLatestOrder() {
        return newOrder;
    }
}
