import com.romanobori.*;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.io.IOException;
import java.util.Arrays;

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

        application.run(1, binanceApiClient, bitfinextClient);

        verify(binanceApiClient, times(0)).addArbOrder(any());
    }
}
