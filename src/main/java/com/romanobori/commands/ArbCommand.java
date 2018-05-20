package com.romanobori.commands;


import com.binance.api.client.exception.BinanceApiException;
import com.romanobori.ArbContext;
import com.romanobori.datastructures.ConditionStatus;
import com.romanobori.datastructures.LimitOrderDetails;
import com.romanobori.state.AmountFillerDetectorObservable;
import com.romanobori.state.OrderConditionObserver;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class ArbCommand {
    private int count;
    ArbContext context;

    ArbCommand(int count, ArbContext context) {
        this.count = count;
        this.context = context;
    }

    public void execute(BlockingQueue<ArbCommand> commandsQueue) throws ExecutionException, InterruptedException {
        ConditionStatus conditionStatus = placeOrderCondition().get();
        if (conditionStatus.isPassed()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            LimitOrderDetails limitOrderDetails = firstOrder(conditionStatus);

            printIfConditionHasPassed(conditionStatus, limitOrderDetails);
            OrderConditionObserver orderConditionObserver = countDownIfConditionBreak(limitOrderDetails.getOrderId(), limitOrderDetails, countDownLatch);
            getAmountFillerDetector().register(limitOrderDetails, secondOrder(), orderConditionObserver);

            countDownLatch.await();
            commandsQueue.add(buildAnotherCommand(count));

        } else {
            System.out.println(String.format("building another command %s", type()));
            Thread.sleep(1000);
            commandsQueue.add(buildAnotherCommand(count));
        }
    }

    private void printIfConditionHasPassed(ConditionStatus conditionStatus, LimitOrderDetails limitOrderDetails) {
        System.out.println(format(
                "the condition has passed , " +
                        "binance value is %f, bitfinex value is %f order id is %s for command %s and the "
                , conditionStatus.getBinancePrice(), conditionStatus.getBitfinexPrice()
                , limitOrderDetails.getOrderId(), type()));
    }

    private OrderConditionObserver countDownIfConditionBreak(String orderId, LimitOrderDetails limitOrderDetails, CountDownLatch countDownLatch) {
        OrderConditionObserver thread = new OrderConditionObserver(keepOrderCondition(), cancelOrder(), orderId, countDownLatch, limitOrderDetails);

        final ExecutorService pool = Executors.newFixedThreadPool(1);

        pool.submit(thread);
        return thread;
    }

    boolean cancel(Runnable task) {
        try {
            task.run();
        } catch (BinanceApiException e) {
            return false;
        }

        return true;
    }

    private boolean isUnknownOrderException(BinanceApiException e) {
        return e.getError().getCode() == 2011;
    }


    abstract LimitOrderDetails firstOrder(ConditionStatus conditionStatus);

    abstract Supplier<ConditionStatus> placeOrderCondition();

    abstract Function<LimitOrderDetails, ConditionStatus> keepOrderCondition();

    abstract Function<String, Boolean> cancelOrder();

    abstract Consumer<Double> secondOrder();

    abstract AmountFillerDetectorObservable getAmountFillerDetector();

    abstract ArbCommand buildAnotherCommand(int count);

    abstract String type();
}
