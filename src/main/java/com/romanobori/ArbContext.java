package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;

public class ArbContext {
    String symbol;
    private String binanceListeningKey;
    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    private BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    private BinanceApiRestClient binanceClient;
    private BitfinexClientApi bitfinexClientApi;
    private BinanceApiWebSocketClientImpl binanceSocketClient = new BinanceApiWebSocketClientImpl();
    private BitfinexApiBroker bitfinexApiBroker;
    private BinanceUpdatedWallet binanceUpdatedWallet;
    private BitfinexUpdatedWallet bitfinexUpdatedWallet;
    public ArbContext(String symbol,
                      String binanceListeningKey,
                      BinanceOrderBookUpdated binanceOrderBookUpdated,
                      BitfinexOrderBookUpdated bitfinexOrderBookUpdated,
                      BinanceApiRestClient binanceClient,
                      BitfinexClientApi bitfinexClientApi,
                      BitfinexApiBroker bitfinexApiBroker,
                      BinanceUpdatedWallet binanceUpdatedWallet,
                      BitfinexUpdatedWallet bitfinexUpdatedWallet) throws APIException {
        this.symbol = symbol;
        this.binanceListeningKey = binanceListeningKey;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        this.binanceClient = binanceClient;
        this.bitfinexClientApi = bitfinexClientApi;
        this.bitfinexApiBroker = bitfinexApiBroker;
        this.binanceUpdatedWallet = binanceUpdatedWallet;
        this.bitfinexUpdatedWallet = bitfinexUpdatedWallet;
    }


    public String getSymbol() {
        return symbol;
    }

    public String getBinanceListeningKey() {
        return binanceListeningKey;
    }

    public BinanceOrderBookUpdated getBinanceOrderBookUpdated() {
        return binanceOrderBookUpdated;
    }

    public BitfinexOrderBookUpdated getBitfinexOrderBookUpdated() {
        return bitfinexOrderBookUpdated;
    }

    public BinanceApiWebSocketClientImpl getBinanceSocketClient() {
        return binanceSocketClient;
    }

    public BinanceApiRestClient getBinanceClient() {
        return binanceClient;
    }

    public BitfinexClientApi getBitfinexClientApi() {
        return bitfinexClientApi;
    }

    public BitfinexApiBroker getBitfinexApiBroker() {
        return bitfinexApiBroker;
    }

    public BinanceUpdatedWallet getBinanceUpdatedWallet() {
        return binanceUpdatedWallet;
    }

    public BitfinexUpdatedWallet getBitfinexUpdatedWallet() {
        return bitfinexUpdatedWallet;
    }
}
