import com.bitfinex.client.BitfinexClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.ExecutedTrade;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexExecutedTradeSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;
import com.romanobori.PropertyHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
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
//    @Test
//    public void getBookOrder() throws IOException {
//        System.out.println(
//                bitfinexClient
//                        .getOrderBook("NEOETH")
//        );
//    }

    @Test
    public void bitfinexTest() throws APIException {
        BitfinexApiBroker bitfinexClient = new BitfinexApiBroker(apiKey, secret);

        bitfinexClient.connect();
        final BitfinexExecutedTradeSymbol symbol = new BitfinexExecutedTradeSymbol(BitfinexCurrencyPair.NEO_BTC);

        final QuoteManager quoteManager = bitfinexClient.getQuoteManager();

        final BiConsumer<BitfinexExecutedTradeSymbol, ExecutedTrade> callback = (sym, trade) -> {
            System.out.format("Got executed trade (%s) for symbol (%s)\n", trade, sym);
        };
        try {

            quoteManager.registerExecutedTradeCallback(symbol, callback);
            quoteManager.subscribeExecutedTrades(symbol);

// To unsubscribe the executed trades stream
            quoteManager.removeExecutedTradeCallback(symbol, callback);
        }catch (APIException e){
            System.out.println(e);
        }


        while(true){

        }
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
