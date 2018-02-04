import com.bitfinex.client.Action;
import com.bitfinex.client.BitfinexClient;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BitfinexClientTest {

    private final String apiKey = "e58UImoX6j1JL2iweJv0Z8owJiNCwffpOC5z4P6gCU6";
    private final String secret = "ZAZaB3zyib8m8SKrHsWpzaN4dN8unPs2K59I7gQ71yx";

    //private final String apiKey = System.getenv("api.key");
    @Test
    public void getBalances() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.
                        getBalances());
    }

    //private final String secret = System.getenv("api.secret");
    private final BitfinexClient bitfinexClient = new BitfinexClient(apiKey, secret);

    @Test
    public void getOpenOrders() throws IOException {
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
                bitfinexClient.addOrder("NEOBTC", 0.9, 0.5, Action.sell)
        );
    }

    @Test
    public void cancelOrder() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.cancelOrder("7758452027")
        );
    }

    @Test
    public void cancelAllOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.cancellAllOrders()
        );
    }

    @Test
    public void withdrawal() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                bitfinexClient.withdrawal("1.0")
        );
    }

}
