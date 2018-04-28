package com.romanobori;

import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;

public class ArbContext {
    String symbol;
    private String binanceListeningKey;
    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    private BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    private BinanceApiRestClientImpl binanceClient;
    private BitfinexClientApi bitfinexClientApi;
    private BinanceApiWebSocketClientImpl binanceSocketClient = new BinanceApiWebSocketClientImpl();
    private BitfinexApiBroker bitfinexApiBroker;
    private BinanceUpdatedWallet binanceUpdatedWallet;
    public ArbContext(String symbol,
                      String binanceKey,
                      String binanceSecret,
                      String bitfinexKey,
                      String bitfinexSecret,
                      String binanceListeningKey,
                      BinanceOrderBookUpdated binanceOrderBookUpdated,
                      BitfinexOrderBookUpdated bitfinexOrderBookUpdated) throws APIException {
        this.symbol = symbol;
        this.binanceListeningKey = binanceListeningKey;
        this.binanceOrderBookUpdated = binanceOrderBookUpdated;
        this.bitfinexOrderBookUpdated = bitfinexOrderBookUpdated;
        binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        bitfinexClientApi = new BitfinexClientApi(
                new BitfinexClient(bitfinexKey, bitfinexSecret)
        );
        bitfinexApiBroker = new BitfinexApiBroker(bitfinexKey, bitfinexSecret);
        bitfinexApiBroker.connect();
        binanceUpdatedWallet = new BinanceUpdatedWallet(binanceSocketClient,
                new BinanceApiClient(binanceClient, 10),
                binanceListeningKey);
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

    public BinanceApiRestClientImpl getBinanceClient() {
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
}
