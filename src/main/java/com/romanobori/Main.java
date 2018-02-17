package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;

import java.util.concurrent.ExecutionException;

public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        BinanceApiRestClient binanceApi = new BinanceApiRestClientImpl("", "");

        BitfinexClient bitfinexClient = new BitfinexClient("", "");


        ApiClient binanceClient = new BinanceApiClient(binanceApi, 10);

        ApiClient bitfinexClien = new BitfinexClientApi(bitfinexClient);

        BinanceApiWebSocketClient stream = new BinanceApiWebSocketClientImpl();

        ArbApplication arbApplication = new ArbApplication(binanceClient, bitfinexClien, stream, "7YdXxaCFkdVBLuxl3h8xc0T1KSb0E8bZxcu86Cr9FjupbqPhfiPK88sTVUJH");

        arbApplication.run();
    }
}
