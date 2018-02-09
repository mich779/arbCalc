package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import javafx.beans.binding.When;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiClientBinanceTest {

    private static String apiKey;
    private static String apiSecret;

    private BinanceApiRestClient binanceApi;
    //private BinanceApiRestClient binanceApi2 = new BinanceApiRestClientImpl(apiKey, apiSecret);
    private ApiClient client;

    @Before
    public void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");

      //  binanceApi2 = new BinanceApiRestClientImpl(apiKey, apiSecret);
        binanceApi = mock(BinanceApiRestClient.class);
        client = new BinanceApiClient(binanceApi, 1000);
    }
    @Test
    public void getOrdersTest(){

        OrderBook book = createOrderBook();

        when(binanceApi.getOrderBook("NEOBTC", 1000))
                .thenReturn(book);

        ArbOrders orderBook = client.getOrderBook("NEOBTC");


        assertEquals(orderBook.bids.size(), 1);
        assertEquals(orderBook.asks.size(), 1);

        assertTrue(orderBook.bids.contains(new ArbOrderEntry(0.1, 5.0)));

        assertTrue(orderBook.asks.contains(new ArbOrderEntry(0.2, 5.0)));


    }

    @Test
    public void getMyOrdersTest(){

        List<Order> openOrders = createOpenOrders();
        when(binanceApi.getOpenOrders(any()))
                .thenReturn(openOrders);

        assertTrue(client.getMyOrders().contains(createArbOpenOrder()));
    }

    @Test
    public void addOrderTest(){
        //Live order, commented to not order accedently
        //client.addArbOrder(new NewArbOrder("NEOETH", ARBTradeAction.SELL,0.2,0.142251));

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

        assertTrue(client.getWallet().entries.contains(arbWalletEntry));
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