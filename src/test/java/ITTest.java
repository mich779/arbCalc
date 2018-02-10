import com.romanobori.*;
import org.junit.Test;
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

        application.run();

        verify(binanceApiClient, times(0)).addArbOrder(any());
    }


    @Test
    public void shouldBuyFromBinance(){

        ClientsCreator clientsCreator = new ClientsCreator().Scenrio_sellInBitfinexBuyInBinance();
        ApiClientStub binanceApiClient = clientsCreator.getBinanceApiClient();
        ApiClientStub bitfinextClient = clientsCreator.getBitfinextClient();

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run();

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        assertThat(binanceOrder, is(new NewArbOrder("NEOETH", ARBTradeAction.BUY, 0.2, 0.2)));

    }

    @Test
    public void shouldSellInBitfinexIf_BuySucceedInBinance(){
        ClientsCreator clientsCreator = new ClientsCreator().Scenrio_sellInBitfinexBuyInBinance();
        ApiClientStub binanceApiClient = clientsCreator.getBinanceApiClient();
        ApiClientStub bitfinextClient = clientsCreator.getBitfinextClient();

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run();

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        NewArbOrder bitfinexOrder = bitfinextClient.getLatestOrder();

        assertThat(binanceOrder, is(new NewArbOrder("NEOETH", ARBTradeAction.BUY, 0.2, 0.2)));

        assertThat(bitfinexOrder, is(new NewArbOrder("NEOETH", ARBTradeAction.SELL, 0.2, 0.2 * 1.004)));

    }

    @Test
    public void shouldNotSellInBitfinexIf_BuyDidntSucceedInBinance(){
        ClientsCreator clientsCreator = new ClientsCreator().Scenrio_sellInBitfinexBuyInBinance();
        ApiClientStub binanceApiClient = clientsCreator.getBinanceApiClient();
        ApiClientStub bitfinextClient =  clientsCreator.getBitfinextClient();

        binanceApiClient.setOrderSuccess(false);
        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run();

        NewArbOrder binanceOrder = binanceApiClient.getLatestOrder();

        NewArbOrder bitfinexOrder = bitfinextClient.getLatestOrder();

        assertThat(binanceOrder, is(new NewArbOrder("NEOETH", ARBTradeAction.BUY, 0.2, 0.2)));

        assertNull(bitfinexOrder);
    }

    @Test
    public void shouldSellInBitfinexAfter_BuySuccessInBinance(){
        
    }
}
