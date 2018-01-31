import com.bitfinex.client.BitfinexClient;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BitfinexClientTest {

    private final String apiKey = "dkV7FlyJPGWUUIYHtJpPgnNyZxmQbt5AmQkmrNFPjKT";
    private final String secret = "G74K0E9603RfBeSX87hqomtYMDXGXd2kafJi8vpwySg";
    private final BitfinexClient bitfinexClient = new BitfinexClient(apiKey, secret);

    @Test
    public void getBalances() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                new BitfinexClient(apiKey, secret).
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
                bitfinexClient.addOrder("NEOBTC", 1.0, 1.0)
        );
    }
}
