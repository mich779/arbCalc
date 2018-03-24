import com.bitfinex.client.Action;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderManager;
import com.romanobori.BitfinexClientApi;
import com.romanobori.BitfinexOrderBookUpdated;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;
import org.junit.Test;

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
//   }



    @Test
    public void shouldThrow() {

    }
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
    @Test
    public void addOrder() throws Exception{
        System.out.println(
                bitfinexClient.addOrder("NEOBTC", 0.2, 0.0078, Action.sell)
        );
    }
//
    @Test
    public void cancelOrder() throws Exception{

        BitfinexApiBroker client = new BitfinexApiBroker(apiKey, secret);
        client.connect();
        OrderManager orderManager = client.getOrderManager();

        orderManager.cancelOrder(9743259202l);
        // cid 51321223288/**/
    }



    @Test
    public void printEvertXSeconds() throws InterruptedException, APIException {
        BitfinexApiBroker bitfinexClient = new BitfinexApiBroker(apiKey, secret);
        bitfinexClient.connect();
        BitfinexOrderBookUpdated bitfinexOrderBookUpdated= new BitfinexOrderBookUpdated(
                new BitfinexClientApi(new BitfinexClient(apiKey, secret)),
                bitfinexClient,
                "NEOBTC", BitfinexCurrencyPair.NEO_BTC);

        while(true){
            System.out.println(bitfinexOrderBookUpdated.getHighestBid());
            Thread.sleep(500);
        }
    }
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
