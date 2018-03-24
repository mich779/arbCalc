package com.romanobori.commands;


import com.romanobori.OrderSuccessCallback;
import com.romanobori.datastructures.ConditionStatus;

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
        ConditionStatus conditionStatus = condition().get();
        if(conditionStatus.isPassed()) {
            System.out.println(String.format(
                    "the condition has passed , " +
                    "binance value is %f, bitfinex value is %f for command %s"
                    , conditionStatus.getBinancePrice(), conditionStatus.getBitfinexPrice(), type()));
            String orderId = firstOrder();
            AtomicBoolean firstOrderComplete = new AtomicBoolean(false);

            Future<Boolean> future = orderComplete(orderId, firstOrderComplete);

            getOrderSuccessCallback().register(
                    orderId, secondOrder(), firstOrderComplete);

            Boolean success = future.get();
            if(success) {
                System.out.println("command passed : " + type());
                if (count > 10) {
                    commandsQueue.add(buildAnotherCommand(count - 1));
                }
            }
        }else {
            Thread.sleep(3000);
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

    abstract Supplier<ConditionStatus> condition();

    abstract Consumer<String> cancelOrder();

    abstract Runnable secondOrder();

    abstract OrderSuccessCallback getOrderSuccessCallback();

    abstract ArbCommand buildAnotherCommand(int count);

    abstract String type();
}
