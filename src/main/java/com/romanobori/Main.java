package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.romanobori.commands.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException , APIException{
        Properties properties = PropertyHandler.loadProps("src/test/resources/props");

        String binanceKey = properties.getProperty("BINANCE_API_KEY");
        String binanceSecret = properties.getProperty("BINANCE_API_SECRET");
        String bitfinexKey = properties.getProperty("BITFINEX_API_KEY");
        String bitfinexSecret = properties.getProperty("BITFINEX_API_SECRET");
        String symbol = "NEOBTC";
        BitfinexCurrencyPair symbolBitfinex = BitfinexCurrencyPair.NEO_BTC;
        BitfinexOrderBookUpdated bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                symbol,
                new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret)),
                new BitfinexApiBroker(bitfinexKey, bitfinexSecret),
                symbolBitfinex
        );
        BinanceApiRestClient binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        String binanceListeningKey = binanceClient.startUserDataStream();
        BinanceOrderBookUpdated binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        CommandsRunner commandsRunner = new CommandsRunner();
        commandsRunner.start(
                Arrays.asList(
                        new BuyBinanceSellBitfinexCommand(
                                symbol,
                                binanceKey,
                                binanceSecret,
                                bitfinexKey,
                                bitfinexSecret,
                                10,
                                binanceOrderBookUpdated,
                                bitfinexOrderBookUpdated,
                                binanceListeningKey
                        ),
                        new BuyBitfinexSellBinanceCommand(
                            10,
                                binanceKey,
                                binanceSecret,
                                symbol,
                                bitfinexKey,
                                bitfinexSecret,
                                binanceOrderBookUpdated,
                                bitfinexOrderBookUpdated),
                        new SellBitfinexBuyBinanceCommand(
                                10,
                                symbol,
                                binanceKey,
                                binanceSecret,
                                bitfinexKey,
                                bitfinexSecret,
                                binanceOrderBookUpdated,
                                bitfinexOrderBookUpdated
                        ),
                        new SellBinanceBuyBitfinexCommand(
                                10,
                                binanceKey,
                                binanceSecret,
                                symbol,
                                bitfinexKey,
                                bitfinexSecret
                                ,binanceOrderBookUpdated,
                                bitfinexOrderBookUpdated,
                                binanceListeningKey
                        )
                )
        );
    }
}
