package com.romanobori;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import support.BinanceApiWebSocketClientStub;

import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AmountFillerDetectorBinanceTest {

    private AmountFillerDetectorBinance amountFillerDetectorBinance;
    private BinanceApiWebSocketClientStub socketClient;
    private AmountChangedObserver observer;
    @Before
    public void setUp(){
        socketClient = new BinanceApiWebSocketClientStub();
        amountFillerDetectorBinance = new AmountFillerDetectorBinance(socketClient, "");
        observer = mock(AmountChangedObserver.class);

    }

    @Test
    public void shouldSendFillAmount() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.FILLED);
        orderTradeUpdateEvent.setOrderId(1234L);
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.5");
        orderTradeUpdateEvent.setAccumulatedQuantity("0.5");
        orderTradeUpdateEvent.setOriginalQuantity("1.0");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
            new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer).updateInfo( eq("FILLED"), eq(0.0));
    }

    @Test
    public void shouldSendPartialAmount() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
        orderTradeUpdateEvent.setOrderId(1234L);
        orderTradeUpdateEvent.setAccumulatedQuantity("0.5");
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.3");
        orderTradeUpdateEvent.setOriginalQuantity("1.0");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer).updateInfo(eq("PARTIAL"), eq(0.2));
    }

    @Test
    public void shouldNotPerformBuyIfNotPartialOrFilled() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.CANCELED);
        orderTradeUpdateEvent.setOrderId(1234L);
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.3");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        Consumer<Double> consumer = mock(Consumer.class);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                consumer,
                observer
        );

        verify(consumer, never()).accept(any());
    }

    @Test
    public void shouldNotContinueIfOrderIdNotTheSame() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
        orderTradeUpdateEvent.setOrderId(1234567L);
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.3");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        socketClient.setUpdateEvent(userDataUpdateEvent);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                consumer,
                observer
        );

        verify(consumer, never()).accept(any());
    }

    @Test
    public void shouldNotContinueIfNodOrderTradeUpdate() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_UPDATE);
        socketClient.setUpdateEvent(userDataUpdateEvent);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                consumer ,
                observer
        );

        verify(consumer , never()).accept(any());
    }


}