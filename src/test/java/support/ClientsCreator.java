package support;

import com.romanobori.ApiClientStub;
import com.romanobori.ArbOrderEntry;
import com.romanobori.ArbOrders;

import java.util.Arrays;

public class ClientsCreator {
    private ApiClientStub binanceApiClient;
    private ApiClientStub bitfinextClient;

    public ApiClientStub getBinanceApiClient() {
        return binanceApiClient;
    }

    public ApiClientStub getBitfinextClient() {
        return bitfinextClient;
    }

    public ClientsCreator Scenrio_sellInBitfinexBuyInBinance() {
        binanceApiClient = new ApiClientStub();


        bitfinextClient = new ApiClientStub();

        binanceApiClient.setOrderBook(
                new ArbOrders(
                        Arrays.asList(new ArbOrderEntry(0.2, 0.2)),
                        Arrays.asList(new ArbOrderEntry(0.2, 0.2))
                )
        );

        bitfinextClient.setOrderBook(
                new ArbOrders(
                        Arrays.asList(new ArbOrderEntry(0.2 , 0.2)),
                        Arrays.asList(new ArbOrderEntry(0.2 * 1.005, 0.2),
                                new ArbOrderEntry(0.2 * 1.0045, 0.2),
                                new ArbOrderEntry(0.2 * 1.004, 0.2))
                )
        );
        return this;
    }

    public ClientsCreator Scenrio_notWorthExchanging() {
        binanceApiClient = new ApiClientStub();


        bitfinextClient = new ApiClientStub();

        binanceApiClient.setOrderBook(
                new ArbOrders(
                        Arrays.asList(new ArbOrderEntry(0.2, 0.2)),
                        Arrays.asList(new ArbOrderEntry(0.2, 0.2))
                )
        );

        bitfinextClient.setOrderBook(
                new ArbOrders(
                        Arrays.asList(new ArbOrderEntry(0.2 , 0.2)),
                        Arrays.asList(new ArbOrderEntry(0.2 * 1.005, 0.2),
                                new ArbOrderEntry(0.2 * 1.0045, 0.2),
                                new ArbOrderEntry(0.2 * 1.003, 0.2))
                )
        );
        return this;
    }
}