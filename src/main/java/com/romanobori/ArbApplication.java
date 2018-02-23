package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;

import java.util.concurrent.ExecutionException;

public class ArbApplication {

    ApiClient binanceApiClient;
    ApiClient bitfinextClient;
    BinanceApiWebSocketClient streamClientBinance;
    String binanceStreamListenId;
    public ArbApplication(ApiClient binanceApiClient, ApiClient bitfinextClient, BinanceApiWebSocketClient streamClient,
                          String binanceStreamListenId) {
        this.binanceApiClient = binanceApiClient;
        this.bitfinextClient = bitfinextClient;
        this.streamClientBinance = streamClient;
        this.binanceStreamListenId = binanceStreamListenId;
    }

    public void run() throws InterruptedException, ExecutionException {

        boolean success = new BuyFromBinanceSellInBitfinexCommand(binanceApiClient, bitfinextClient, streamClientBinance).invoke(this.binanceStreamListenId);

    }




}
