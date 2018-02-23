package support;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.OrderbookConfiguration;
import com.github.jnidzwetzki.bitfinex.v2.entity.OrderbookEntry;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;

import java.util.function.BiConsumer;

public class BitfinexOrderBookManagerStub extends OrderbookManager{

    private OrderbookEntry entry;

    public BitfinexOrderBookManagerStub(BitfinexApiBroker bitfinexApiBroker, OrderbookEntry entry) {
        super(bitfinexApiBroker);
        this.entry = entry;
    }


    public void registerOrderbookCallback(final OrderbookConfiguration orderbookConfiguration,
                                          final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback) throws APIException {
        callback.accept(null, entry);
    }



}
