package com.romanobori;

import com.romanobori.commands.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Properties properties = PropertyHandler.loadProps("src/test/resources/props");

        String binanceKey = properties.getProperty("BINANCE_API_KEY");
        String binanceSecret = properties.getProperty("BINANCE_API_SECRET");
        String bitfinexKey = properties.getProperty("BITFINEX_API_KEY");
        String bitfinexSecret = properties.getProperty("BITFINEX_API_SECRET");
        String symbol = "NEOBTC";
        CommandsRunner commandsRunner = new CommandsRunner();
        commandsRunner.start(
                Arrays.asList(
                        new BuyBinanceSellBitfinexCommand(
                                symbol,binanceKey, binanceSecret,
                                bitfinexKey, bitfinexSecret,10
                        ),
                        new BuyBitfinexSellBinanceCommand(
                            10, binanceKey, binanceSecret, symbol,
                            bitfinexKey, bitfinexSecret),
                        new SellBitfinexBuyBinanceCommand(
                                10, symbol, binanceKey, binanceSecret,
                                bitfinexKey, bitfinexSecret
                        ),
                        new SellBitfinexBuyBinanceCommand(
                                10, symbol, binanceKey, binanceSecret,
                                bitfinexKey, bitfinexSecret
                        )
                )
        );
    }
}
