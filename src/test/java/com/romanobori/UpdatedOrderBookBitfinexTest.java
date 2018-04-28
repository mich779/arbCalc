package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.OrderbookEntry;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ArbOrders;
import org.junit.BeforeClass;
import org.junit.Test;
import support.BitfinexOrderBookManagerStub;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdatedOrderBookBitfinexTest {

    private static String apiKey = System.getenv("BITFINEX_API_KEY");
    private static String secret = System.getenv("BITFINEX_API_SECRET");


    @BeforeClass
    public static void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BITFINEX_API_KEY");
        secret  = p.getProperty("BITFINEX_API_SECRET");
    }

    private BitfinexOrderBookManagerStub createBitfinexOrderbookManagerStub(BitfinexApiBroker bitfinexApiBroker, OrderbookEntry entry) {
        return new BitfinexOrderBookManagerStub(
                bitfinexApiBroker,
                entry
        );
    }

    private void mockOrderBok(BitfinexClientApi bitfinexClientApi, List<ArbOrderEntry> bids, List<ArbOrderEntry> asks) {
        when(bitfinexClientApi.getOrderBook("NEOETH"))
                .thenReturn(new ArbOrders(
                        bids,
                        asks
                ));
    }


}
