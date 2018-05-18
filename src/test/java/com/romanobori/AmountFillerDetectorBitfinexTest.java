package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrderState;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.util.Observer;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AmountFillerDetectorBitfinexTest {

    ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);

    AmountFillerDetectorObservable amountFillerDetectorBitfinex;

    private BitfinexApiBroker bitfinexApiBroker;


    private OrderManager orderManager;
    @Before
    public void setUp() {
        orderManager = mock(OrderManager.class);
        bitfinexApiBroker = mock(BitfinexApiBroker.class);
        when(bitfinexApiBroker.getOrderManager()).thenReturn(orderManager);
        amountFillerDetectorBitfinex = new AmountFillerDetectorBitfinex(bitfinexApiBroker);

    }

    private void setExchangeOrder(ExchangeOrder exchangeOrder){
        doAnswer((Answer) invocation -> {
            Consumer<ExchangeOrder> arg = (Consumer<ExchangeOrder>) invocation.getArguments()[0];
            arg.accept(exchangeOrder);
            return null;
        }).when(orderManager).registerCallback(any());
    }

    @Test
    public void shouldNotPerformSecondOrderIfStateIsNotPartialOrComplete() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_ACTIVE);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        Observer observer = mock(Observer.class);

        amountFillerDetectorBitfinex.register(limitOrderDetails, (amount) -> {}, observer);

        verify(observer, never()).update(any(), any());

    }

    @Test
    public void shouldNotPerformAction_IfOrderIdIsNotTheSame() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_EXECUTED);
        exchangeOrder.setOrderId(123456L);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        Observer observer = mock(Observer.class);

        amountFillerDetectorBitfinex.register(limitOrderDetails, (amount) -> {}, observer);

        verify(observer, never()).update(any(), any());
    }


    @Test
    public void shouldPerformUpdate_WhenComplete() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_EXECUTED);
        exchangeOrder.setOrderId(1234);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        Observer observer = mock(Observer.class);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer, observer);

        verify(observer).update(any(), eq("FILLED"));
        verify(consumer).accept(eq(1.0));
    }

    @Test
    public void shouldUpdatePartialWithTheRightAmount() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_PARTIALLY_FILLED);
        exchangeOrder.setOrderId(1234);
        exchangeOrder.setAmount(0.8);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        Observer observer = mock(Observer.class);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer, observer);

        verify(observer).update(any(), eq("PARTIAL"));
        verify(consumer).accept(AdditionalMatchers.eq(0.2, 0.005));


    }


}