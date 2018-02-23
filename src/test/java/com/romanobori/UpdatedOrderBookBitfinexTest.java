package com.romanobori;

import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.OrderbookEntry;
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
    public void shouldBeTheSame() throws InterruptedException, APIException {
        BitfinexClientApi bitfinexClientApi = new BitfinexClientApi(new BitfinexClient(apiKey, secret));
        BitfinexApiBroker bitfinexClientBroker = new BitfinexApiBroker();

        bitfinexClientBroker.connect();
        BitfinexOrderBookUpdated updated = new BitfinexOrderBookUpdated(bitfinexClientApi
                , bitfinexClientBroker);

        Thread.sleep(1000 * 60 * 2);

        ArbOrders orderBookFromBitfinex = bitfinexClientApi.getOrderBook("NEOETH").sortByPrice();

        ArbOrders orderBook = updated.getOrderBook().sortByPrice();

        System.out.println(orderBookFromBitfinex.bids);

        System.out.println(orderBook.bids);

        System.out.println(orderBookFromBitfinex.asks);

        System.out.println(orderBook.asks);
    }

    @Test
    public void shouldRemoveFromBids(){
        BitfinexApiBroker bitfinexApiBroker = mock(BitfinexApiBroker.class);

        BitfinexOrderBookManagerStub bitfinexOrderBookManagerStub = createBitfinexOrderbookManagerStub(bitfinexApiBroker, new OrderbookEntry(0.2, 0, 1.0));

        when(bitfinexApiBroker.getOrderbookManager()).thenReturn(bitfinexOrderBookManagerStub);

        BitfinexClientApi bitfinexClientApi = mock(BitfinexClientApi.class);

        mockOrderBok(bitfinexClientApi, Arrays.asList(new ArbOrderEntry(0.2, 30.0)), Arrays.asList());

        BitfinexOrderBookUpdated updated = new BitfinexOrderBookUpdated(bitfinexClientApi
                , bitfinexApiBroker);

        ArbOrders newArbOrders = updated.getOrderBook();

        assertThat(newArbOrders.bids.size(), is(0));
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
                , bitfinexApiBroker);

        ArbOrders newArbOrders = updated.getOrderBook();

        assertThat(newArbOrders.bids.size(), is(1));

        assertThat(newArbOrders.bids.get(0).amount, is( 2.2));

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
                , bitfinexApiBroker);

        ArbOrders newArbOrders = updated.getOrderBook();

        assertThat(newArbOrders.bids.size(), is(2));

        assertTrue(newArbOrders.bids.contains(new ArbOrderEntry(0.3, 2.2)));
        assertTrue(newArbOrders.bids.contains(new ArbOrderEntry(0.2, 30.0)));
    }

    private void mockOrderBok(BitfinexClientApi bitfinexClientApi, List<ArbOrderEntry> bids, List<ArbOrderEntry> asks) {
        when(bitfinexClientApi.getOrderBook("NEOETH"))
                .thenReturn(new ArbOrders(
                        bids,
                        asks
                ));
    }


}
