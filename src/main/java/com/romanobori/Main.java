package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, APIException {

        Properties p = PropertyHandler.loadProps("src/main/resources/props");
        String bitfinexApiKey= p.getProperty("BITFINEX_API_KEY");
        String bitfinexApiSecret = p.getProperty("BITFINEX_API_SECRET");
        String binanceApiKey = p.getProperty("BINANCE_API_KEY");
        String binanceApiSecret = p.getProperty("BINANCE_API_SECRET");

        BinanceApiRestClient binanceApi = new BinanceApiRestClientImpl(binanceApiKey, binanceApiSecret);


        ApiClient binanceClient = new BinanceApiClient(binanceApi, 10);

        BitfinexClientApi bitfinexClient = new BitfinexClientApi(new BitfinexClient(bitfinexApiKey, bitfinexApiSecret));

        BinanceApiWebSocketClient stream = new BinanceApiWebSocketClientImpl();

        BitfinexApiBroker apiBroker = new BitfinexApiBroker(bitfinexApiKey, bitfinexApiSecret);
        apiBroker.connect();
        BitfinexOrderBookUpdated updatedBitfinex = new BitfinexOrderBookUpdated(bitfinexClient, apiBroker, "");

        BinanceOrderBookUpdated updatedBinance = new BinanceOrderBookUpdated("NEOETH");

        ArbApplication arbApplication = new ArbApplication(binanceClient, bitfinexClient, stream, updatedBinance, updatedBitfinex, "7YdXxaCFkdVBLuxl3h8xc0T1KSb0E8bZxcu86Cr9FjupbqPhfiPK88sTVUJH");

        arbApplication.run();
    }
}
