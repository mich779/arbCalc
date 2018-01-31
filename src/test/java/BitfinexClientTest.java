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
                bitfinexClient.addOrder("NEOBTC", 0.9, 0.5, Action.sell)
        );
    }
    //{"id":7758187639,"cid":54017786253,"cid_date":"2018-01-31","gid":null,"symbol":"neobtc","exchange":"bitfinex","price":"0.01401","avg_execution_price":"0.0","side":"buy","type":"exchange limit","timestamp":"1517410817.815381643","is_live":true,"is_cancelled":false,"is_hidden":false,"oco_order":null,"was_forced":false,"original_amount":"1.0","remaining_amount":"1.0","executed_amount":"0.0","src":"api","order_id":7758187639}

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
}
