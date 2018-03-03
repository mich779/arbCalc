package support;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.*;
import com.binance.api.client.domain.market.CandlestickInterval;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class BinanceApiWebSocketClientStub implements BinanceApiWebSocketClient {

    private UserDataUpdateEvent updateEvent;

    public BinanceApiWebSocketClientStub(UserDataUpdateEvent updateEvent) {
        this.updateEvent = updateEvent;
    }

    @Override
    public void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback) {
        throw new NotImplementedException();
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
        callback.onResponse(this.updateEvent);
    }

    @Override
    public void onAllMarketTickersEvent(BinanceApiCallback<List<AllMarketTickersEvent>> callback) {
        throw new NotImplementedException();
    }

    public void setUpdateEvent(UserDataUpdateEvent updateEvent) {
        this.updateEvent = updateEvent;
    }

}
