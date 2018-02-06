package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.bitfinex.client.BitfinexClient;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ApiClientBitfinexTest {


    BitfinexClient bitfinexClient;
    ApiClient client;
    @Before
    public void setup(){
        bitfinexClient = mock(BitfinexClient.class);
        client = new BitfinexClientApi(bitfinexClient);

    }

    @Test
    public void getOpenOrdersTest(){

    }


}
