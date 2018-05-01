package com.romanobori.commands;


import com.binance.api.client.exception.BinanceApiException;
import com.romanobori.OrderSuccessCallback;
import com.romanobori.datastructures.ConditionStatus;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class ArbCommand {
    int count;

    public ArbCommand(int count) {
        this.count = count;
    }

    public void execute(BlockingQueue<ArbCommand> commandsQueue) throws ExecutionException, InterruptedException {
        ConditionStatus conditionStatus = placeOrderCondition().get();
        if (conditionStatus.isPassed()) {

            LimitOrderDetails limitOrderDetails = firstOrder();

            System.out.println(format(
                    "the condition has passed , " +
                            "binance value is %f, bitfinex value is %f order id is %s for command %s and the "
                    , conditionStatus.getBinancePrice(), conditionStatus.getBitfinexPrice()
                    , limitOrderDetails.getOrderId(), type()));
            AtomicBoolean firstOrderComplete = new AtomicBoolean(false);

            Future<Boolean> future = orderComplete(limitOrderDetails.getOrderId(),
                    limitOrderDetails.getPrice(), firstOrderComplete);

            getOrderSuccessCallback().register(
                    limitOrderDetails.getOrderId(), secondOrder(), firstOrderComplete);

            Boolean success = future.get();
            if (success) {
                System.out.println("command passed : " + type());
                if (count > 10) {
                    commandsQueue.add(buildAnotherCommand(count - 1));
                }
            } else {
                System.out.println(format("building command after cancellation %s", type()));
                commandsQueue.add(buildAnotherCommand(count));
            }
        } else {
            Thread.sleep(1000);
            System.out.println(format("building another command with type %s", type()));
            commandsQueue.add(buildAnotherCommand(count));
        }
    }

    private Future<Boolean> orderComplete(String orderId, Double price, AtomicBoolean firstOrderComplete) {
        ConditionKeeperThread thread = new ConditionKeeperThread(
                keepOrderCondition(), cancelOrder(), orderId, firstOrderComplete, price);

        final ExecutorService pool = Executors.newFixedThreadPool(1);

        return pool.submit(thread);


    }


    abstract LimitOrderDetails firstOrder();

    abstract Supplier<ConditionStatus> placeOrderCondition();

    abstract Function<Double, ConditionStatus> keepOrderCondition();

    abstract Consumer<String> cancelOrder();

    abstract Runnable secondOrder();

    abstract OrderSuccessCallback getOrderSuccessCallback();

    abstract ArbCommand buildAnotherCommand(int count);

    abstract String type();

    public void tryUntilSuccess(Runnable task, int numOfTries) {
        if (numOfTries == 0) {
            return;
        }
        try {
            task.run();
        } catch (BinanceApiException e) {
            tryUntilSuccess(task, numOfTries - 1);
        }
    }
}
