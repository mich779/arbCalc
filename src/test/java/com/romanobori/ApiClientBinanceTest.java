package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiClientBinanceTest {


    private BinanceApiRestClient binanceApi;
    private ApiClient client;

    @Before
    public void setup(){
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

        Order openOrder = createOpenOrder();
        List<Order> openOrders = new ArrayList<>();
        openOrders.add(openOrder);
        when(binanceApi.getOpenOrders(any()))
                .thenReturn(openOrders);
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
