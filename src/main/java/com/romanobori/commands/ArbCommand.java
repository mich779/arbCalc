package com.romanobori.commands;


import com.romanobori.OrderSuccessCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ArbCommand {

    public void execute(){
        if(condition().get()) {
            String orderId = firstOrder();

            ConditionKeeperThread thread = new ConditionKeeperThread(
                    condition(), cancelOrder(), orderId);

            final ExecutorService pool = Executors.newFixedThreadPool(1);

            Future<Boolean> future = pool.submit(thread);

            getOrderSuccessCallback().register(
                    orderId, secondOrder(), thread
            );

            try {
                Boolean ans = future.get();
            } catch (InterruptedException e) {
                
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    abstract String firstOrder();

    abstract Supplier<Boolean> condition();

    abstract Consumer<String> cancelOrder();

    abstract Runnable secondOrder();

    abstract OrderSuccessCallback getOrderSuccessCallback();
}
