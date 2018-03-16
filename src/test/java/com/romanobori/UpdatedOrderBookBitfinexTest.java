package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
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

    @Test
    public void shouldRemoveFromBids(){
        BitfinexApiBroker bitfinexApiBroker = mock(BitfinexApiBroker.class);

        BitfinexOrderBookManagerStub bitfinexOrderBookManagerStub = createBitfinexOrderbookManagerStub(bitfinexApiBroker, new OrderbookEntry(0.2, 0, 1.0));

        when(bitfinexApiBroker.getOrderbookManager()).thenReturn(bitfinexOrderBookManagerStub);

        BitfinexClientApi bitfinexClientApi = mock(BitfinexClientApi.class);

        mockOrderBok(bitfinexClientApi, Arrays.asList(new ArbOrderEntry(0.2, 30.0)), Arrays.asList());

        BitfinexOrderBookUpdated updated = new BitfinexOrderBookUpdated(bitfinexClientApi
                , bitfinexApiBroker, "");

        ArbOrders orderBook = updated.orderBook;

        assertThat(orderBook.getBids().size(), is(0));
    }

    private BitfinexOrderBookManagerStub createBitfinexOrderbookManagerStub(BitfinexApiBroker bitfinexApiBroker, OrderbookEntry entry) {
        return new BitfinexOrderBookManagerStub(
                bitfinexApiBroker,
                entry
        );
    }

    @Test
    public void shouldUpdateBids() {
        BitfinexApiBroker bitfinexApiBroker = mock(BitfinexApiBroker.class);

        BitfinexOrderBookManagerStub bitfinexOrderBookManagerStub = createBitfinexOrderbookManagerStub(bitfinexApiBroker, new OrderbookEntry(0.2, 1.0, 2.2));

        when(bitfinexApiBroker.getOrderbookManager()).thenReturn(bitfinexOrderBookManagerStub);

        BitfinexClientApi bitfinexClientApi = mock(BitfinexClientApi.class);

        mockOrderBok(bitfinexClientApi, Arrays.asList(new ArbOrderEntry(0.2, 30.0)),
                Collections.EMPTY_LIST);

        BitfinexOrderBookUpdated updated = new BitfinexOrderBookUpdated(bitfinexClientApi
                , bitfinexApiBroker, "NEOETH");

        updated.subscribe();

        ArbOrders newArbOrders = updated.orderBook;

        assertThat(newArbOrders.getBids().size(), is(1));

        assertThat(newArbOrders.getBids().get(0).getAmount(), is( 2.2));

    }

    @Test
    public void shouldAddNewEntity() {
        BitfinexApiBroker bitfinexApiBroker = mock(BitfinexApiBroker.class);

        BitfinexOrderBookManagerStub bitfinexOrderBookManagerStub = createBitfinexOrderbookManagerStub(bitfinexApiBroker, new OrderbookEntry(0.3, 1.0, 2.2));

        when(bitfinexApiBroker.getOrderbookManager()).thenReturn(bitfinexOrderBookManagerStub);

        BitfinexClientApi bitfinexClientApi = mock(BitfinexClientApi.class);

        mockOrderBok(bitfinexClientApi, new ArrayList<>(Arrays.asList(new ArbOrderEntry(0.2, 30.0))),
                new ArrayList<>(Collections.EMPTY_LIST));

        BitfinexOrderBookUpdated updated = new BitfinexOrderBookUpdated(bitfinexClientApi
                , bitfinexApiBroker, "NEOETH");
        updated.subscribe();

        ArbOrders newArbOrders = updated.orderBook;

        assertThat(newArbOrders.getBids().size(), is(2));

        assertTrue(newArbOrders.getBids().contains(new ArbOrderEntry(0.3, 2.2)));
        assertTrue(newArbOrders.getBids().contains(new ArbOrderEntry(0.2, 30.0)));
    }

    private void mockOrderBok(BitfinexClientApi bitfinexClientApi, List<ArbOrderEntry> bids, List<ArbOrderEntry> asks) {
        when(bitfinexClientApi.getOrderBook("NEOETH"))
                .thenReturn(new ArbOrders(
                        bids,
                        asks
                ));
    }


}
