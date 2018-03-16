package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.romanobori.commands.ArbCommand;
import com.romanobori.commands.ArbCommandBuyBinanceSellBitfinex;
import org.junit.Before;
import org.junit.Test;
import support.BinanceApiRestClientStub;
import support.BinanceApiWebSocketClientStub;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuyFromBinanceSellInBitfinexCommandTest {


    private BinanceApiWebSocketClient binanceApiWebSocketClient;
    private ApiClientStub bitfinexClient;


    @Before
    public void setup(){

        bitfinexClient = new ApiClientStub("1");

        UserDataUpdateEvent event = new UserDataUpdateEvent();

        event.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);

        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderId(100L);

        orderTradeUpdateEvent.setOrderStatus(OrderStatus.FILLED);

        event.setOrderTradeUpdateEvent(orderTradeUpdateEvent);

        binanceApiWebSocketClient = new BinanceApiWebSocketClientStub(event);
    }




    @Test
    public void shouldSucceed() throws ExecutionException, InterruptedException {

        BinanceOrderBookUpdated binanceOrderBookUpdated = mock(
                BinanceOrderBookUpdated.class
        );

        when(binanceOrderBookUpdated.getLowestAsk())
                .thenReturn(0.2);

        BitfinexOrderBookUpdated bitfinexOrderBookUpdated =
                mock(BitfinexOrderBookUpdated.class);

        when(bitfinexOrderBookUpdated.getLowestAsk())
                .thenReturn(0.2 * 1.003);

        BinanceApiRestClientStub binanceClient = new BinanceApiRestClientStub();

        bitfinexClient = new ApiClientStub("1");

        ArbCommand command = new ArbCommandBuyBinanceSellBitfinex(
                binanceClient,
                bitfinexClient,
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                "symbol",
                binanceApiWebSocketClient
        );

        command.execute();

        assertNotNull(binanceClient.getLatestOrder());

        assertNotNull(bitfinexClient.getLatestOrder());

    }

    @Test
    public void shouldNotSucceed_whenConditionNotMet() {
        BinanceOrderBookUpdated binanceOrderBookUpdated = mock(
                BinanceOrderBookUpdated.class
        );

        when(binanceOrderBookUpdated.getLowestAsk())
                .thenReturn(0.2);

        BitfinexOrderBookUpdated bitfinexOrderBookUpdated =
                mock(BitfinexOrderBookUpdated.class);

        when(bitfinexOrderBookUpdated.getLowestAsk())
                .thenReturn(0.2);

        BinanceApiRestClientStub binanceClient = new BinanceApiRestClientStub();

        bitfinexClient = new ApiClientStub("1");

        ArbCommand command = new ArbCommandBuyBinanceSellBitfinex(
                binanceClient,
                bitfinexClient,
                binanceOrderBookUpdated,
                bitfinexOrderBookUpdated,
                "symbol",
                binanceApiWebSocketClient
        );

        command.execute();

        assertNull(binanceClient.getLatestOrder());

        assertNull(bitfinexClient.getLatestOrder());

    }


}