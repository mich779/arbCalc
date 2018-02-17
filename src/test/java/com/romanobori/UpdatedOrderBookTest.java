package com.romanobori;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static org.mockito.Mockito.mock;

public class UpdatedOrderBookTest {

    private static String apiKey;
    private static String apiSecret;

    @Before
    public void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");
    }

    @Test
    public void isUpdatedWithBinance() throws InterruptedException, FileNotFoundException {

        BinanceApiWebSocketClient socketClient = new BinanceApiWebSocketClientImpl();

        BinanceApiRestClient binanceClient = new BinanceApiRestClientImpl(apiKey, apiSecret);

        BinanceOrderBookUpdated updated = new BinanceOrderBookUpdated(socketClient, binanceClient);


        Thread.sleep(1000 * 60 * 1);

        OrderBook updatedOrderBook = updated.getUpdatedOrderBook();

        OrderBook fromBinance = binanceClient.getOrderBook("NEOETH", 100);

        PrintWriter outOurs = new PrintWriter("src/test/resources/our");

        PrintWriter them = new PrintWriter("src/test/resources/them");

        outOurs.println(updatedOrderBook);

        them.println(fromBinance);

    }
    
}
