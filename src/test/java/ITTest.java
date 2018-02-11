import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.manager.TradeManager;
import com.romanobori.*;
import org.junit.Test;
import support.BinanceApiWebSocketClientStub;
import support.ClientsCreator;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ITTest {


    @Test
    public void shouldBuyBitfinexAfterStreamDecideSellSuccessInBinance(){

        BinanceApiWebSocketClientStub streamClient = createStreamClient();


        ClientsCreator clientsCreator = new ClientsCreator().Scenrio_sellInBitfinexBuyInBinance();
        ApiClientStub binanceApiClient = clientsCreator.getBinanceApiClient();
        ApiClientStub bitfinextClient = clientsCreator.getBitfinextClient();

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient, streamClient);

        application.run();

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        NewArbOrder bitfinexOrder = bitfinextClient.getLatestOrder();

        assertThat(binanceOrder, is(new NewArbOrderLimit("NEOETH", ARBTradeAction.BUY, 0.2, 0.2)));
        assertThat(bitfinexOrder, is(new NewArbOrderMarket("NEOETH", ARBTradeAction.SELL, 0.2)));

    }

    @Test
    public void shouldNotDoAnything(){
        BinanceApiWebSocketClientStub streamClient = createStreamClient();


        ClientsCreator clientsCreator = new ClientsCreator().Scenrio_notWorthExchanging();
        ApiClientStub binanceApiClient = clientsCreator.getBinanceApiClient();
        ApiClientStub bitfinextClient = clientsCreator.getBitfinextClient();

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient, streamClient);

        application.run();

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        NewArbOrder bitfinexOrder = bitfinextClient.getLatestOrder();

        assertNull(binanceOrder);
        assertNull(bitfinexOrder);
    }



    private BinanceApiWebSocketClientStub createStreamClient() {
        BinanceApiWebSocketClientStub streamClient = new BinanceApiWebSocketClientStub();

        UserDataUpdateEvent updateEvent =
                new UserDataUpdateEvent();

        updateEvent.setEventType(UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE);

        updateEvent.setEventTime(System.currentTimeMillis());

        OrderTradeUpdateEvent tradeUpdateEvent = new OrderTradeUpdateEvent();

        tradeUpdateEvent.setEventType("executionReport");

        tradeUpdateEvent.setEventTime(System.currentTimeMillis());

        tradeUpdateEvent.setSymbol("NEOETH");

        tradeUpdateEvent.setSide(OrderSide.BUY);

        tradeUpdateEvent.setType(OrderType.LIMIT);

        tradeUpdateEvent.setOrderStatus(OrderStatus.FILLED);

        tradeUpdateEvent.setOriginalQuantity("0.2");

        tradeUpdateEvent.setQuantityLastFilledTrade("0.2");

        tradeUpdateEvent.setOrderId(100L);

        updateEvent.setOrderTradeUpdateEvent(tradeUpdateEvent);
        streamClient.setUpdateEvent(updateEvent);
        return streamClient;
    }

}
