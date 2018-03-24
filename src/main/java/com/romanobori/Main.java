package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.romanobori.commands.BuyBinanceSellBitfinexCommand;
import com.romanobori.commands.BuyBitfinexSellBinanceCommand;
import com.romanobori.commands.CommandsRunner;
import com.romanobori.commands.SellBitfinexBuyBinanceCommand;

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
                                bitfinexKey, bitfinexSecret,10, BitfinexCurrencyPair.NEO_BTC
                        ),
                        new BuyBitfinexSellBinanceCommand(
                            10, binanceKey, binanceSecret, symbol,
                            bitfinexKey, bitfinexSecret,  BitfinexCurrencyPair.NEO_BTC),
                        new SellBitfinexBuyBinanceCommand(
                                10, symbol, binanceKey, binanceSecret,
                                bitfinexKey, bitfinexSecret, BitfinexCurrencyPair.NEO_BTC
                        ),
                        new SellBitfinexBuyBinanceCommand(
                                10, symbol, binanceKey, binanceSecret,
                                bitfinexKey, bitfinexSecret,BitfinexCurrencyPair.NEO_BTC
                        )
                )
        );
    }
}
