package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExchangeOrderState;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        doAnswer(invocation -> {
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
        AmountChangedObserver observer = mock(AmountChangedObserver.class);
        Consumer<Double> consumer = mock(Consumer.class);

        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer, observer);

        verify(consumer, never()).accept(any());

    }

    @Test
    public void shouldNotPerformAction_IfOrderIdIsNotTheSame() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_EXECUTED);
        exchangeOrder.setOrderId(123456L);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        AmountChangedObserver observer = mock(AmountChangedObserver.class);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer , observer);
        verify(consumer, never()).accept(any());
    }


    @Test
    public void shouldPerformUpdate_WhenComplete() {
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setState(ExchangeOrderState.STATE_EXECUTED);
        exchangeOrder.setOrderId(1234);
        exchangeOrder.setAmount(0.0);
        setExchangeOrder(exchangeOrder);
        LimitOrderDetails limitOrderDetails = new LimitOrderDetails("1234", 0.5, 1.0);
        AmountChangedObserver observer = mock(AmountChangedObserver.class);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer, observer);

        verify(observer).updateInfo(eq("FILLED"), eq(0.0));
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
        AmountChangedObserver observer = mock(AmountChangedObserver.class);
        Consumer<Double> consumer = mock(Consumer.class);
        amountFillerDetectorBitfinex.register(limitOrderDetails, consumer, observer);

        verify(consumer).accept(AdditionalMatchers.eq(0.2, 0.005));

        AmountFillerDetectorBitfinex amountFillerDetectorBitfinexCast = (AmountFillerDetectorBitfinex) amountFillerDetectorBitfinex;

        verify(observer).updateInfo(eq("PARTIAL"), AdditionalMatchers.eq(0.8, 0.005));
        assertThat(amountFillerDetectorBitfinexCast.getLeftAmount().get(), is(0.8));
    }


}