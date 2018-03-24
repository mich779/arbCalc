package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ArbOrders;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BitfinexOrderBookUpdatedTest {

    @Test
    public void getLowestAsk() {
        BitfinexClientApi bitfinexClient = mock(BitfinexClientApi.class);
        when(bitfinexClient.getOrderBook("NEOETH")).thenReturn(new ArbOrders(
                Collections.EMPTY_LIST,
                Arrays.asList(
                        new ArbOrderEntry(0.2, 0.2),
                        new ArbOrderEntry(0.3, 0.3)
                )
        ));
        BitfinexOrderBookUpdated bitfinexOrderBookUpdated = new BitfinexOrderBookUpdated(bitfinexClient, null, null, BitfinexCurrencyPair.NEO_ETH);

        assertThat(bitfinexOrderBookUpdated.getLowestAsk(), is(0.2));
    }

}