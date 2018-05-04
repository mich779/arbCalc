package com.romanobori.commands;

import com.binance.api.client.BinanceApiRestClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.romanobori.*;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuyBinanceSellBitfinexCommandTest {


    private BinanceOrderBookUpdated binanceOrderBookUpdated;
    private BitfinexOrderBookUpdated bitfinexOrderBookUpdated;
    private BinanceApiRestClient binanceApiRestClient;
    private BitfinexClientApi bitfinexClientApi;
    private BitfinexApiBroker bitfinexApiBroker;
    private BinanceUpdatedWallet binanceUpdatedWallet;
    private BitfinexUpdatedWallet bitfinexUpdatedWallet;

    @Before
    public void setUp(){
        binanceOrderBookUpdated = mock(BinanceOrderBookUpdated.class);
        bitfinexOrderBookUpdated = mock(BitfinexOrderBookUpdated.class);
        binanceApiRestClient = mock(BinanceApiRestClient.class);
        bitfinexClientApi = mock(BitfinexClientApi.class);
        bitfinexApiBroker = mock(BitfinexApiBroker.class);
        binanceUpdatedWallet = mock(BinanceUpdatedWallet.class);
        bitfinexUpdatedWallet = mock(BitfinexUpdatedWallet.class);
    }

    @Test
    public void shouldPass() throws Exception{

        when(binanceOrderBookUpdated.getHighestBid()).thenReturn(
                new ArbOrderEntry(0.5, 2)
        );

        when(bitfinexOrderBookUpdated.getHighestBid()).thenReturn(
                new ArbOrderEntry(0.6, 0.3)
        );

        when(binanceUpdatedWallet.getFreeAmount(eq("BTC"))).thenReturn(
                0.11
        );

        ArbContext context = new
                ArbContext("BTC",
                "LISTENING_KEY",
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                binanceApiRestClient,
                bitfinexClientApi,
                bitfinexApiBroker,
                binanceUpdatedWallet,
                bitfinexUpdatedWallet
                );

        BuyBinanceSellBitfinexCommand buyBinanceSellBitfinexCommand = new BuyBinanceSellBitfinexCommand(10, context);

        ConditionStatus conditionStatus = buyBinanceSellBitfinexCommand.placeOrderCondition().get();

        assertTrue(conditionStatus.isPassed());
    }

    @Test
    public void shouldNotPass() throws Exception {
        when(binanceOrderBookUpdated.getHighestBid()).thenReturn(
                new ArbOrderEntry(0.5, 2)
        );

        when(bitfinexOrderBookUpdated.getHighestBid()).thenReturn(
                new ArbOrderEntry(0.6, 0.3)
        );

        when(binanceUpdatedWallet.getFreeAmount(eq("BTC"))).thenReturn(
                0.09
        );

        ArbContext context = new
                ArbContext("BTC",
                "LISTENING_KEY",
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                binanceApiRestClient,
                bitfinexClientApi,
                bitfinexApiBroker,
                binanceUpdatedWallet,
                bitfinexUpdatedWallet
        );

        BuyBinanceSellBitfinexCommand buyBinanceSellBitfinexCommand = new BuyBinanceSellBitfinexCommand(10, context);

        ConditionStatus conditionStatus = buyBinanceSellBitfinexCommand.placeOrderCondition().get();

        assertFalse(conditionStatus.isPassed());
    }


}