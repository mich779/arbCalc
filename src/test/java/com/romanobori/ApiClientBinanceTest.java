package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.romanobori.client.ApiClient;
import com.romanobori.client.BinanceApiClient;
import com.romanobori.datastructures.*;
import com.romanobori.utils.PropertyHandler;
import org.junit.Before;
import org.junit.Test;
import support.BinanceApiRestClientStub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiClientBinanceTest {

    private static String apiKey;
    private static String apiSecret;

    private BinanceApiRestClient binanceApi;
    private ApiClient client;

    @Before
    public void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");

        binanceApi = mock(BinanceApiRestClient.class);
        client = new BinanceApiClient(binanceApi, 1000);
    }
    @Test
    public void getOrdersTest(){

        OrderBook book = createOrderBook();

        when(binanceApi.getOrderBook("NEOBTC", 1000))
                .thenReturn(book);

        ArbOrders orderBook = client.getOrderBook("NEOBTC");


        assertEquals(orderBook.getBids().size(), 1);
        assertEquals(orderBook.getAsks().size(), 1);

        assertTrue(orderBook.getBids().contains(new ArbOrderEntry(0.1, 5.0)));

        assertTrue(orderBook.getAsks().contains(new ArbOrderEntry(0.2, 5.0)));


    }

    @Test
    public void getMyOrdersTest(){

        List<Order> openOrders = createOpenOrders();
        when(binanceApi.getOpenOrders(any()))
                .thenReturn(openOrders);

        assertTrue(client.getMyOrders().contains(createArbOpenOrder()));
    }

    @Test
    public void addOrderTestLimit(){
        BinanceApiRestClientStub binanceClient = new BinanceApiRestClientStub();
        ApiClient client = new BinanceApiClient(binanceClient, 100);

        client.addArbOrder(new NewArbOrderLimit("NEOETH", ARBTradeAction.BUY, 0.2, 100));

        NewOrder latestOrder = binanceClient.getLatestOrder();

        assertThat(latestOrder.getSymbol(), is("NEOETH"));

        assertThat(latestOrder.getType(), is(OrderType.LIMIT));

        assertThat(latestOrder.getPrice(), is("100.0"));

    }

    @Test
    public void addOrderTestMarket(){
        BinanceApiRestClientStub binanceClient = new BinanceApiRestClientStub();
        ApiClient client = new BinanceApiClient(binanceClient, 100);

        client.addArbOrder(new NewArbOrderMarket("NEOETH", ARBTradeAction.SELL, 0.2));

        NewOrder latestOrder = binanceClient.getLatestOrder();

        assertThat(latestOrder.getSymbol(), is("NEOETH"));

        assertThat(latestOrder.getType(), is(OrderType.MARKET));

        assertThat(latestOrder.getQuantity(), is("0.2"));

    }


    @Test
    public void getWalletTest(){

        Account account = new Account();
        AssetBalance assetBalance = new AssetBalance();
        assetBalance.setAsset("BTC");
        assetBalance.setFree("4.0");
        assetBalance.setLocked("0.5");
        account.setBalances(Arrays.asList(assetBalance));
        when(binanceApi.getAccount()).thenReturn(account);

        ArbWalletEntry arbWalletEntry = new ArbWalletEntry("BTC", 4.5, 4.0);

        assertTrue(client.getWallet().getEntries().contains(arbWalletEntry));
    }


    private List<Order> createOpenOrders(){

        List<Order> openOrders = new ArrayList<>();
        Order openOrder = createOpenOrder();
        openOrders.add(openOrder);

        return openOrders;
    }

    private Order createOpenOrder(){
        Order openOrder = new Order();
        openOrder.setSymbol("VIBEETH");
        openOrder.setOrderId(new Long(12));
        openOrder.setPrice("12");
        openOrder.setOrigQty("12");
        openOrder.setExecutedQty("0");
        openOrder.setType(OrderType.LIMIT);
        openOrder.setSide(OrderSide.SELL);
        openOrder.setTime(new Long(12));

        return openOrder;
    }
    private MyArbOrder createArbOpenOrder(){
        MyArbOrder openOrder = new MyArbOrder("VIBEETH","12", Double.parseDouble("12"),
                Double.parseDouble("12"), Double.parseDouble("0"),ARBTradeAction.SELL,Long.parseLong("12"));
        return openOrder;
    }


    private OrderBook createOrderBook() {
        OrderBookEntry asks = new OrderBookEntry();
        asks.setPrice("0.2");
        asks.setQty("5");
        OrderBookEntry bids = new OrderBookEntry();
        bids.setPrice("0.1");
        bids.setQty("5");

        OrderBook book = new OrderBook();
        book.setAsks(Arrays.asList(asks));
        book.setBids(Arrays.asList(bids));
        return book;
    }


}