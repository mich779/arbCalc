package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import support.BinanceApiWebSocketClientStub;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuyFromBinanceSellInBitfinexCommandTest {


    private BinanceApiWebSocketClient binanceApiWebSocketClient;
    private ApiClientStub binanceClient;
    private ApiClientStub bitfinexClient;


    @Before
    public void setup(){
        binanceClient = new ApiClientStub("1");

        bitfinexClient = new ApiClientStub("1");
        UserDataUpdateEvent event = new UserDataUpdateEvent();

        event.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);

        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderId(1L);

        orderTradeUpdateEvent.setOrderStatus(OrderStatus.FILLED);

        event.setOrderTradeUpdateEvent(orderTradeUpdateEvent);

        binanceApiWebSocketClient = new BinanceApiWebSocketClientStub(event);
    }

    @Test
    public void shouldSucceed() throws ExecutionException, InterruptedException {

        ArbOrderBookUpdated binanceOrderBookUpdated = prepareUpdatebookMock(
                Arrays.asList(new ArbOrderEntry(0.2 , 0.2)),
                Arrays.asList()
        );

        ArbOrderBookUpdated bitfinexOrderBookUpdated = prepareUpdatebookMock(
                Arrays.asList(new ArbOrderEntry(0.2 * 1.003, 0.2)),
                Arrays.asList());

        BuyFromBinanceSellInBitfinexCommand command =
                new BuyFromBinanceSellInBitfinexCommand(
                        binanceClient,
                        bitfinexClient,
                        binanceApiWebSocketClient,
                        binanceOrderBookUpdated,
                        bitfinexOrderBookUpdated);

        command.invoke("");

        assertNotNull(binanceClient.getLatestOrder());

        assertNotNull(bitfinexClient.getLatestOrder());

    }

    @Test
    public void shouldNotSucceed() throws ExecutionException, InterruptedException {

        ArbOrderBookUpdated binanceOrderBookUpdated = prepareUpdatebookMock(
                Arrays.asList(new ArbOrderEntry(0.2, 0.2))
                , Arrays.asList());

        ArbOrderBookUpdated bitfinexOrderBookUpdated = prepareUpdatebookMock(
                Arrays.asList(new ArbOrderEntry(0.2 * 1.002, 0.2))
                , Arrays.asList());

        BuyFromBinanceSellInBitfinexCommand command =
                new BuyFromBinanceSellInBitfinexCommand(
                        binanceClient,
                        bitfinexClient,
                        binanceApiWebSocketClient,
                        binanceOrderBookUpdated,
                        bitfinexOrderBookUpdated);

        command.invoke("");

        assertNull(binanceClient.getLatestOrder());

        assertNull(bitfinexClient.getLatestOrder());
    }

    private ArbOrderBookUpdated prepareUpdatebookMock( List<ArbOrderEntry> bids, List<ArbOrderEntry> asks) {
        ArbOrderBookUpdated orderbookUpdated = mock(ArbOrderBookUpdated.class);

        when(orderbookUpdated.getOrderBook()).thenReturn(
                new ArbOrders(
                        bids,
                        asks));
        return orderbookUpdated;
    }


}