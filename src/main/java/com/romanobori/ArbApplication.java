package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.romanobori.client.ApiClient;
import com.romanobori.orderbook.BinanceOrderBookUpdated;
import com.romanobori.orderbook.BitfinexOrderBookUpdated;

import java.util.concurrent.ExecutionException;

public class ArbApplication {

    private ApiClient binanceApiClient;
    private ApiClient bitfinextClient;
    private BinanceApiWebSocketClient streamClientBinance;
    private String binanceStreamListenId;
    private BinanceOrderBookUpdated updatedBinance;
    private BitfinexOrderBookUpdated updatedBitfinex;

    public ArbApplication(ApiClient binanceApiClient, ApiClient bitfinextClient, BinanceApiWebSocketClient streamClient,
                          BinanceOrderBookUpdated updatedBinance, BitfinexOrderBookUpdated updatedBitfinex, String binanceStreamListenId) {
        this.binanceApiClient = binanceApiClient;
        this.bitfinextClient = bitfinextClient;
        this.streamClientBinance = streamClient;
        this.binanceStreamListenId = binanceStreamListenId;
        this.updatedBinance = updatedBinance;
        this.updatedBitfinex = updatedBitfinex;
    }

    public void run() throws InterruptedException, ExecutionException {

    }

}
