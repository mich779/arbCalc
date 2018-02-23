import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.function.BiConsumer;

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



    @Test
    public void randomName2() throws URISyntaxException, APIException, IOException {
        Properties p = PropertyHandler.loadProps("src/test/resources/props");

        BitfinexApiBroker bitfinexClient = new BitfinexApiBroker(p.getProperty("BITFINEX_API_KEY"),
                p.getProperty("BITFINEX_API_SECRET"));
        bitfinexClient.connect();

//        final TradeManager tradeManager = bitfinexClient.getTradeManager();
//
//        tradeManager.registerCallback((trade) -> {
//            System.out.format("Got trade callback (%s)\n", trade);
//        });
// The consumer will be called on all received ticks for the symbol
        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                BitfinexCurrencyPair.BTC_USD, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexClient.getOrderbookManager();
        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("price: (%s), amount: (%s), count: (%s) --- for orderbook (%s)\n counter\n",
                    entry.getPrice(),entry.getAmount(),entry.getCount(), orderbookConfig);
        };

        orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        orderbookManager.subscribeOrderbook(orderbookConfiguration);

        while(true){

        }
    }




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
