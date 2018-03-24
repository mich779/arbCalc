package com.romanobori.commands;


import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import com.romanobori.BinanceOrderBookUpdated;
import com.romanobori.BitfinexClientApi;
import com.romanobori.BitfinexOrderBookUpdated;
import com.romanobori.OrderSuccessCallback;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ArbCommand {
    int count;

    public ArbCommand(int count) {
        this.count = count;
    }

    public void execute(BlockingQueue<ArbCommand> commandsQueue) throws ExecutionException, InterruptedException {
        if(condition().get()) {
            String orderId = firstOrder();

            AtomicBoolean firstOrderComplete = new AtomicBoolean(false);

            Future<Boolean> future = orderComplete(orderId, firstOrderComplete);

            getOrderSuccessCallback().register(
                    orderId, secondOrder(), firstOrderComplete);

            Boolean success = future.get();
            if(success) {
                if (count > 3) {
                    commandsQueue.add(buildAnotherCommand(count - 1));
                }
            }
        }else {
            commandsQueue.add(buildAnotherCommand(count));
        }
    }

    private Future<Boolean> orderComplete(String orderId, AtomicBoolean firstOrderComplete) {
        ConditionKeeperThread thread = new ConditionKeeperThread(
                condition(), cancelOrder(), orderId, firstOrderComplete);

        final ExecutorService pool = Executors.newFixedThreadPool(1);

        return pool.submit(thread);
    }


    abstract String firstOrder();

    abstract Supplier<Boolean> condition();

    abstract Consumer<String> cancelOrder();

    abstract Runnable secondOrder();

    abstract OrderSuccessCallback getOrderSuccessCallback();

    abstract ArbCommand buildAnotherCommand(int count);
}
