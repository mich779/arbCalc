package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNotNull;
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

        when(binanceApi.getOrderBook("NEOBTC", 1000))
                .thenReturn(new OrderBook(
                        Arrays.asList(
                                new OrderBookEntry("0.2", "5"),new OrderBookEntry("0.2", "4"),new OrderBookEntry("0.2", "3")
                        ),
                        Arrays.asList(
                                new OrderBookEntry("0.1", "5"),new OrderBookEntry("0.1", "4"),new OrderBookEntry("0.1", "3")
                        )
                ));

        ArbOrders openOrders = client.getOpenOrders("NEOBTC");


        assertEquals(openOrders.bids.size(), 3);
        assertEquals(openOrders.asks.size(), 3);

        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 3.0)));
        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 4.0)));
        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 5.0)));

        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 3.0)));
        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 4.0)));
        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 5.0)));

    }

    @Test
    public void getMyOrdersTest(){

        when(binanceApi.getOrderBook("NEOBTC", 1000))
                .thenReturn(new OrderBook(
                        Arrays.asList(
                                new OrderBookEntry("0.2", "5"),new OrderBookEntry("0.2", "4"),new OrderBookEntry("0.2", "3")
                        ),
                        Arrays.asList(
                                new OrderBookEntry("0.1", "5"),new OrderBookEntry("0.1", "4"),new OrderBookEntry("0.1", "3")
                        )
                ));

        ArbOrders openOrders = client.getOpenOrders("NEOBTC");


        assertEquals(openOrders.bids.size(), 3);
        assertEquals(openOrders.asks.size(), 3);

        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 3.0)));
        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 4.0)));
        assertTrue(openOrders.bids.contains(new ArbOrderEntry(0.2, 5.0)));

        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 3.0)));
        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 4.0)));
        assertTrue(openOrders.asks.contains(new ArbOrderEntry(0.1, 5.0)));

    }
}
