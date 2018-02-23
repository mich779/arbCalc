package com.romanobori;

import org.junit.Before;

import java.io.IOException;
import java.util.Properties;

public class UpdatedOrderBookBinanceTest {

    private static String apiKey;
    private static String apiSecret;

    @Before
    public void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");
    }

//    @Test
//    public void isUpdatedWithBinance() throws InterruptedException, FileNotFoundException {
//
//        BinanceApiWebSocketClient socketClient = new BinanceApiWebSocketClientImpl();
//
//        BinanceApiRestClient binanceClient = new BinanceApiRestClientImpl(apiKey, apiSecret);
//
//        BinanceOrderBookUpdated updated = new BinanceOrderBookUpdated(socketClient, binanceClient);
//
//
//        Thread.sleep(1000 * 60 * 2);
//
//        OrderBook updatedOrderBook = updated.getUpdatedOrderBook();
//
//        OrderBook fromBinance = binanceClient.getOrderBook("NEOETH", 100);
//
//        PrintWriter outOurs = new PrintWriter("src/test/resources/our");
//
//        PrintWriter them = new PrintWriter("src/test/resources/them");
//
//        outOurs.println(updatedOrderBook);
//
//        them.println(fromBinance);
//
//    }

}
