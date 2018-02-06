package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.bitfinex.client.BitfinexClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiClientBitfinexTest {


    BitfinexClient bitfinexClient;
    ApiClient client;
    @Before
    public void setup(){
        bitfinexClient = mock(BitfinexClient.class);
        client = new BitfinexClientApi(bitfinexClient);

    }

    @Test
    public void getOpenOrdersTest() {
        when(bitfinexClient.getOpenOrders("NEOBTC")).thenReturn(
        "{\"bids\":" +
                "[{\"price\":\"0.12334\",\"amount\":\"76.2\",\"timestamp\":\"1517938950.0\"}]," +
                "\"asks\":[{\"price\":\"0.12408\",\"amount\":\"16.62475748\",\"timestamp\":\"1517938950.0\"}]}");


        ArbOrders openOrders = client.getOpenOrders("NEOBTC");

        List<ArbOrderEntry> asks = openOrders.asks;
        List<ArbOrderEntry> bids = openOrders.bids;

        assertEquals(asks.size(), 1);
        assertEquals(bids.size(), 1);

        assertTrue(bids.contains(new ArbOrderEntry(0.12334, 76.2)));
        assertTrue(asks.contains(new ArbOrderEntry(0.12408, 16.62475748)));
    }

    @Test
    public void getWallet(){
        when(bitfinexClient.getBalances())
                .thenReturn("[{\"type\":\"exchange\",\"currency\":\"btc\"," +
                        "\"amount\":\"0.00008475\",\"available\":\"0.00008475\"}]");


        ArbWallet wallet = client.getWallet();

        List<ArbWalletEntry> entries = wallet.entries;

        assertEquals(entries.size(), 1);
        assertTrue(entries.contains(new ArbWalletEntry(
                "btc", 0.00008475, 0.00008475
        )));

    }


}
