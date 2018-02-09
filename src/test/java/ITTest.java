import com.romanobori.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ITTest {

    @Test
    public void shouldNotExchange() throws IOException {

        ApiClient binanceApiClient = spy(new ApiClientStub(new ArbOrders(
                Arrays.asList(new ArbOrderEntry(0.2, 0.2)),
                Arrays.asList(new ArbOrderEntry(0.2, 0.2))
        )));


        ApiClient bitfinextClient = spy(new ApiClientStub(new ArbOrders(
                Arrays.asList(new ArbOrderEntry(0.2, 0.2)),
                Arrays.asList(new ArbOrderEntry(0.2, 0.2))
        )));

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run(binanceApiClient, bitfinextClient);

        verify(binanceApiClient, times(0)).addArbOrder(any());
    }


    @Test
    public void shouldBuyFromBinanceAndSellInBitfinex(){

        ApiClientStub binanceApiClient = new ApiClientStub();


        ApiClientStub bitfinextClient = new ApiClientStub();

        binanceApiClient.setOrderBook(
                new ArbOrders(
                Arrays.asList(new ArbOrderEntry(0.2, 0.2)),
                Arrays.asList(new ArbOrderEntry(0.2, 0.2))
                )
        );

        bitfinextClient.setOrderBook(
                new ArbOrders(
                        Arrays.asList(new ArbOrderEntry(0.2 , 0.2)),
                        Arrays.asList(new ArbOrderEntry(0.2 * 1.004, 0.2))
                )
        );

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run(binanceApiClient, bitfinextClient);

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        assertEquals(binanceOrder,new NewArbOrder("NEOETH", ARBTradeAction.BUY, 0.2, 0.2));
    }
}
