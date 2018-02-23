package support;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.*;
import com.binance.api.client.domain.market.CandlestickInterval;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class BinanceApiWebSocketClientDepthStub implements BinanceApiWebSocketClient {

    DepthEvent depthEvent;

    public BinanceApiWebSocketClientDepthStub(DepthEvent depthEvent) {
        this.depthEvent = depthEvent;
    }

    @Override
    public void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback) {

        callback.onResponse(depthEvent);

    }

    @Override
    public void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback) {
        throw new NotImplementedException();
    }

    @Override
    public void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback) {
        throw new NotImplementedException();
    }

    @Override
    public void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback) {
        throw new NotImplementedException();
    }

    @Override
    public void onAllMarketTickersEvent(BinanceApiCallback<List<AllMarketTickersEvent>> callback) {
        throw new NotImplementedException();
    }
}
