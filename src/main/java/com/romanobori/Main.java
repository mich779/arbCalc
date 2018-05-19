package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.jmx.CreateAndRegisterMBeanInMBeanServer;
import com.jmx.MbeanEntity;
import com.jmx.OrderbookMXBean;
import com.jmx.OrderbookMbeanImpl;
import com.romanobori.client.BinanceApiClient;
import com.romanobori.client.BitfinexClientApi;
import com.romanobori.commands.BuyBinanceSellBitfinexCommand;
import com.romanobori.commands.BuyBitfinexSellBinanceCommand;
import com.romanobori.commands.SellBinanceBuyBitfinexCommand;
import com.romanobori.commands.SellBitfinexBuyBinanceCommand;
import com.romanobori.orderbook.BinanceOrderBookUpdated;
import com.romanobori.orderbook.BitfinexOrderBookUpdated;
import com.romanobori.utils.PropertyHandler;
import com.romanobori.wallet.BinanceUpdatedWallet;
import com.romanobori.wallet.BitfinexUpdatedWallet;

import java.util.Arrays;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception{


        Properties properties = PropertyHandler.loadProps("src/test/resources/props");

        String binanceKey = properties.getProperty("BINANCE_API_KEY");
        String binanceSecret = properties.getProperty("BINANCE_API_SECRET");
        String bitfinexKey = properties.getProperty("BITFINEX_API_KEY");
        String bitfinexSecret = properties.getProperty("BITFINEX_API_SECRET");
        String symbol = "NEOBTC";
        BitfinexCurrencyPair symbolBitfinex = BitfinexCurrencyPair.NEO_BTC;
        BitfinexApiBroker bitfinexApiBroker = new BitfinexApiBroker(bitfinexKey, bitfinexSecret);
        bitfinexApiBroker.connect();
        BitfinexClientApi bitfinexClientApi = new BitfinexClientApi(new BitfinexClient(bitfinexKey, bitfinexSecret));
        BitfinexOrderBookUpdated bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(
                symbol,
                bitfinexClientApi,
                bitfinexApiBroker,
                symbolBitfinex
        );

        BinanceApiRestClient binanceClient = new BinanceApiRestClientImpl(binanceKey, binanceSecret);
        String binanceListeningKey = binanceClient.startUserDataStream();
        BinanceOrderBookUpdated binanceOrderBookUpdated = new BinanceOrderBookUpdated(symbol);
        BinanceApiWebSocketClient binanceSocketClient = new BinanceApiWebSocketClientImpl();
        BinanceUpdatedWallet binanceUpdatedWallet = new BinanceUpdatedWallet(binanceSocketClient,
                new BinanceApiClient(binanceClient, 10),
                binanceListeningKey);
        BitfinexUpdatedWallet bitfinexUpdatedWallet = new BitfinexUpdatedWallet(bitfinexApiBroker);

        ArbContext context = new ArbContext(
                symbol,
                binanceListeningKey,
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                binanceClient,
                bitfinexClientApi,
                bitfinexApiBroker,
                binanceUpdatedWallet,
                bitfinexUpdatedWallet);

        createMbeans(bitfinexOrderBookUpdated, binanceOrderBookUpdated);

        CommandsRunner commandsRunner = new CommandsRunner();

        commandsRunner.start(
                Arrays.asList(
                        new BuyBinanceSellBitfinexCommand(10, context),
                        new BuyBitfinexSellBinanceCommand(10, context),
                        new SellBitfinexBuyBinanceCommand(10, context),
                        new SellBinanceBuyBitfinexCommand(10,context)
                )
        );
    }

    private static void createMbeans(BitfinexOrderBookUpdated bitfinexOrderBookUpdated, BinanceOrderBookUpdated binanceOrderBookUpdated) throws Exception {
        OrderbookMXBean orderbookMXBean = new OrderbookMbeanImpl(binanceOrderBookUpdated, bitfinexOrderBookUpdated);
        CreateAndRegisterMBeanInMBeanServer.register(
                Arrays.asList(new MbeanEntity("com.javacodegeeks.snippets.enterprise:type=OrderbookMbean", orderbookMXBean))
        );
    }
}
