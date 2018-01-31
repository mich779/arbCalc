import com.bitfinex.client.Action;
import com.bitfinex.client.BitfinexClient;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BitfinexClientTest {

    private final String apiKey = System.getenv("api.key");
    private final String secret = System.getenv("api.secret");
    private final BitfinexClient bitfinexClient = new BitfinexClient(apiKey, secret);

    @Test
    public void getBalances() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.
                        getBalances());
    }

    @Test
    public void getTicker() throws IOException {
        System.out.println(
                bitfinexClient
                        .getOpenOrders("NEOETH")
        );
    }

    @Test
    public void getMyOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient
                .getMyActiveOrders()
        );
    }


    @Test
    public void addOrder() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.addOrder("NEOBTC", 1, 0.01405, Action.buy)
        );
    }

}
