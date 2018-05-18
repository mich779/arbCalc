package com.romanobori.commands;

import com.romanobori.ArbContext;
import com.romanobori.BinanceOrderBookUpdated;
import com.romanobori.BitfinexOrderBookUpdated;
import com.romanobori.LimitOrderDetails;
import com.romanobori.datastructures.ArbOrderEntry;
import com.romanobori.datastructures.ConditionStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SellBitfinexBuyBinanceCommandTest {

    private BinanceOrderBookUpdated binanceOrderBookUpdated = mock(BinanceOrderBookUpdated.class);
    private BitfinexOrderBookUpdated bitfinexOrderBookUpdated = mock(BitfinexOrderBookUpdated.class);
    private ArbContext context;

    @Before
    public void setUp() throws Exception{
        context = new ArbContext(
                "NEOBTC",
                "",
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    public void shouldNotKeepOrderWhen_priceGapIsNotProfitable() throws Exception{
        when(binanceOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(0.5, 0.2));
        ArbCommand arbCommand = new SellBitfinexBuyBinanceCommand(0, context);

        ConditionStatus conditionStatus = arbCommand.keepOrderCondition().apply(
                new LimitOrderDetails("order1", 0.5, 0.2));

        assertFalse(conditionStatus.isPassed());
    }


    @Test
    public void shouldNotKeepOrderWhen_orderIsMuchHigherThenLowestAsk()throws Exception {
        when(bitfinexOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(2, 0.2));
        when(binanceOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(0.4, 0.2));
        ArbCommand arbCommand = new SellBitfinexBuyBinanceCommand(0, context);

        ConditionStatus conditionStatus = arbCommand.keepOrderCondition().apply(
                new LimitOrderDetails("order1", 2*1.006, 0.2));

        assertFalse(conditionStatus.isPassed());
    }


    @Test
    public void shouldNotKeepOrderWhen_marketAmountIsSmaller()throws Exception {
        when(bitfinexOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(2.0, 0.7));
        when(binanceOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(0.5, 0.5));
        ArbCommand arbCommand = new SellBitfinexBuyBinanceCommand(0, context);

        ConditionStatus conditionStatus = arbCommand.keepOrderCondition().apply(
                new LimitOrderDetails("order1", 2.0, 0.6));

        assertFalse(conditionStatus.isPassed());
    }


    @Test
    public void shouldKeepOrderWhen_priceIsProfitableAndMarketAmountIsNotSmaller()throws Exception {
        when(bitfinexOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(2.0, 0.7));
        when(binanceOrderBookUpdated.getLowestAsk()).thenReturn(new ArbOrderEntry(0.5, 0.5));
        ArbCommand arbCommand = new SellBitfinexBuyBinanceCommand(0, context);

        ConditionStatus conditionStatus = arbCommand.keepOrderCondition().apply(
                new LimitOrderDetails("order1", 2.0, 0.5));

        assertTrue(conditionStatus.isPassed());
    }
}