package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.romanobori.commands.ArbCommand;
import com.romanobori.commands.ArbCommandBuyBinanceSellBitfinex;
import com.romanobori.commands.CommandsRunner;

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

        CommandsRunner commandsRunner = new CommandsRunner();
        ArbCommand arbCommand = new ArbCommandBuyBinanceSellBitfinex(
                "NEOETH",binanceKey, binanceSecret,
                bitfinexKey, bitfinexSecret,10
        );
        commandsRunner.start(
                Arrays.asList(
                    arbCommand
                )
        );
    }
}
