package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;

public class ArbApplication {

    ApiClient binanceApiClient;
    ApiClient bitfinextClient;
    BinanceApiWebSocketClient streamClientBinance;
    public ArbApplication(ApiClient binanceApiClient, ApiClient bitfinextClient, BinanceApiWebSocketClient streamClient) {
        this.binanceApiClient = binanceApiClient;
        this.bitfinextClient = bitfinextClient;
        this.streamClientBinance = streamClient;
    }

    public void run(){

        boolean success = new BuyFromBinanceSellInBitfinexCommand(binanceApiClient, bitfinextClient, streamClientBinance).invoke();

    }




}
