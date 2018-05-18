package com.romanobori;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import support.BinanceApiWebSocketClientStub;

import java.util.Observer;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AmountFillerDetectorBinanceTest {

    private AmountFillerDetectorBinance amountFillerDetectorBinance;
    private BinanceApiWebSocketClientStub socketClient;
    private Observer observer;
    @Before
    public void setUp(){
        socketClient = new BinanceApiWebSocketClientStub();
        amountFillerDetectorBinance = new AmountFillerDetectorBinance(socketClient, "");
        observer = mock(Observer.class);

    }

    @Test
    public void shouldSendFillAmount() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.FILLED);
        orderTradeUpdateEvent.setOrderId(1234L);
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.5");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
            new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer).update(any(), eq("FILLED"));
    }

    @Test
    public void shouldSendPartialAmount() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);
        OrderTradeUpdateEvent orderTradeUpdateEvent = new OrderTradeUpdateEvent();
        orderTradeUpdateEvent.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
        orderTradeUpdateEvent.setOrderId(1234L);
        orderTradeUpdateEvent.setQuantityLastFilledTrade("0.3");
        userDataUpdateEvent.setOrderTradeUpdateEvent(orderTradeUpdateEvent);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer).update(any(), eq("PARTIAL"));
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
        verify(observer, never()).update(any(), any());

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

        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer, never()).update(any(), any());
    }

    @Test
    public void shouldNotContinueIfNodOrderTradeUpdate() {
        UserDataUpdateEvent userDataUpdateEvent = new UserDataUpdateEvent();
        userDataUpdateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_UPDATE);
        socketClient.setUpdateEvent(userDataUpdateEvent);

        amountFillerDetectorBinance.register(
                new LimitOrderDetails("1234", 0.5, 1.0),
                (num) -> {},
                observer
        );

        verify(observer, never()).update(any(), any());
    }


}