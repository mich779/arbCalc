import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BinanceClientTest {


    String apiKey = "XXXXX";
    String apiSecret = "XXXX";
    private final BinanceApiRestClient client = new BinanceApiRestClientImpl(apiKey, apiSecret);

    @Test
    public void getOpenOrders(){
        System.out.println(
                client.getOrderBook("NEOBTC", 1000)
        );
    }

    @Test
    public void getMyOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        System.out.println(
                client.getOpenOrders(new OrderRequest("VIBEETH"))
        );
    }
    @Test
    public void newOrder(){
        client.newOrder(new NewOrder("INSBTC", OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC,
                "203.87", "0.0012023"));
    }

    @Test
    public void removeOrder(){
        client.cancelOrder(new CancelOrderRequest("INSBTC",new Long(1187423)));
    }
}
