package support;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.*;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BinanceApiWebSocketClientStub implements BinanceApiWebSocketClient {

    UserDataUpdateEvent updateEvent;
    private BinanceApiCallback<UserDataUpdateEvent> callback;

    @Override
    public void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback) {

    }

    @Override
    public void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback) {

    }

    @Override
    public void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback) {

    }

    @Override
    public void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback) {
        ExecutorService executorService =
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());

        executorService.submit(() -> {
                    try {
                        Thread.sleep(1000 * 10);
                        callback.onResponse(updateEvent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void onAllMarketTickersEvent(BinanceApiCallback<List<AllMarketTickersEvent>> callback) {

    }

    public void setUpdateEvent(UserDataUpdateEvent updateEvent) {
        this.updateEvent = updateEvent;
    }

}
