import com.romanobori.ApiClient;
import com.romanobori.ArbApplication;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class ITTest {

    @Test
    public void shouldNotExchange() throws IOException {

        ApiClient binanceApiClient = mock(ApiClient.class);

        ApiClient bitfinextClient = mock(ApiClient.class);

        ArbApplication application = new ArbApplication(binanceApiClient, bitfinextClient);

        application.run(1);



    }
}
