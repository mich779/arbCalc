import com.bitfinex.client.BitfinexClient;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Properties;

public class BitfinexClientTest {

    private static String apiKey = System.getenv("BITFINEX_API_KEY");
    private static String secret = System.getenv("BITFINEX_API_SECRET");


    @BeforeClass
    public static void setup() throws IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BITFINEX_API_KEY");
        secret  = p.getProperty("BITFINEX_API_SECRET");

    }
//    @Test
//    public void getBalances() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient.
//                        getBalances());
//    }
//
    private final BitfinexClient bitfinexClient = new BitfinexClient(apiKey, secret);
//    @Test
//    public void getBookOrder() throws IOException {
//        System.out.println(
//                bitfinexClient
//                        .getOrderBook("NEOETH")
//        );
//    }
//
//    @Test
//    public void getMyOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient
//                .getMyActiveOrders()
//        );
//    }
//
//
//    @Test
//    public void addOrder() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient.addOrder("NEOBTC", 0.5, 0.5, Action.sell)
//        );
//    }
//
//    @Test
//    public void cancelOrder() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient.cancelOrder("7758452027")
//        );
//    }
//
//    @Test
//    public void cancelAllOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient.cancellAllOrders()
//        );
//    }
//
//    @Test
//    public void withdrawal() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                bitfinexClient.withdrawal("","","")
//        );
//    }
//
}
