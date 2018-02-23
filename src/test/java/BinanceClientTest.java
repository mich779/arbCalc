import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.RawOrderbookManager;
import com.romanobori.BitfinexClientApi;
import com.romanobori.BitfinexOrderBookUpdated;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.function.BiConsumer;

public class BinanceClientTest {
//
//
    private static String apiKey;
    private static String apiSecret;
    private final BinanceApiRestClient client = new BinanceApiRestClientImpl(apiKey, apiSecret);


    @BeforeClass
    public static void setup() throws IOException {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        Properties p = PropertyHandler.loadProps("src/test/resources/props");
        apiKey = p.getProperty("BINANCE_API_KEY");
        apiSecret = p.getProperty("BINANCE_API_SECRET");
    }


    @Test
    public void getListenKey() {
        System.out.println(client.startUserDataStream());
    }

    @Test
    public void streamTest(){

        BinanceApiWebSocketClient webSocketClient = new BinanceApiWebSocketClientImpl();

        webSocketClient.onDepthEvent("neobtc", System.out::println);

//        webSocketClient.onUserDataUpdateEvent("y6uSBWKH4UxNRfyfuiBtUY7NxMBc1qTeGRsop5JWj0ORagGm8fN65Y2oyXJQ"
//        , System.out::println);
        while(true){

        }

    }
//    @Test
//    public void pr(){
//        System.out.println(apiKey);
//    }`
//    @Test
//    public void getOpenOrders(){
//        System.out.println(
//                client.getOrderBook("NEOBTC", 1000)
//        );
//    }

    @Test
    public void consumeOrderBookTest() throws APIException {

        BitfinexApiBroker bitfinexApiBroker = new BitfinexApiBroker();
        bitfinexApiBroker.connect();
        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                BitfinexCurrencyPair.BTC_USD, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexApiBroker.getOrderbookManager();

        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("Got entry (%s) for orderbook (%s)\n", entry, orderbookConfig);
        };

        orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);


        while(true){

        }
    }

    @Test
    public void orderBookRaw() throws APIException {

        BitfinexApiBroker bitfinexClient = new BitfinexApiBroker();

        final RawOrderbookConfiguration orderbookConfiguration = new RawOrderbookConfiguration(
                BitfinexCurrencyPair.BTC_USD);

        final RawOrderbookManager rawOrderbookManager = bitfinexClient.getRawOrderbookManager();

        final BiConsumer<RawOrderbookConfiguration, RawOrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("Got entry (%s) for orderbook (%s)\n", entry, orderbookConfig);
        };

        rawOrderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        rawOrderbookManager.subscribeOrderbook(orderbookConfiguration);

        while(true){

        }
    }

    @Test
    public void romanoCode() throws APIException {

        BitfinexApiBroker bitfinexClient = new BitfinexApiBroker();

        final OrderbookConfiguration orderbookConfiguration = new OrderbookConfiguration(
                BitfinexCurrencyPair.BTC_USD, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final OrderbookManager orderbookManager = bitfinexClient.getOrderbookManager();
        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("price: (%s), amount: (%s), count: (%s) --- for orderbook (%s)\n",
                    entry.getPrice(), entry.getAmount(), entry.getCount(), orderbookConfig);
        };

        orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);


        while(true){

        }
    }



//
//    @Test
//    public void getMyOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        System.out.println(
//                client.getOpenOrders(new OrderRequest("VIBEETH"))
//        );
//    }
//    @Test
//    public void newOrder(){
//        client.newOrder(new NewOrder("VIBEETH", OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC,
//                "126", "0.0021550"));
//    }
//
//    @Test
//    public void removeOrder(){
//        client.cancelOrder(new CancelOrderRequest("VIBEETH",new Long(1273711)));
//    }
}
