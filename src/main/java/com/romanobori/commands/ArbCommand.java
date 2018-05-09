package com.romanobori.commands;


import com.binance.api.client.exception.BinanceApiException;
import com.romanobori.ArbContext;
import com.romanobori.OrderSuccessCallback;
import com.romanobori.datastructures.ConditionStatus;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class ArbCommand {
    protected int count;
    protected ArbContext context;

    public ArbCommand(int count, ArbContext context) {
        this.count = count;
        this.context = context;
    }

    public void execute(BlockingQueue<ArbCommand> commandsQueue) throws ExecutionException, InterruptedException {
        ConditionStatus conditionStatus = placeOrderCondition().get();
        if(conditionStatus.isPassed()) {

            LimitOrderDetails limitOrderDetails = firstOrder(conditionStatus);

            System.out.println(format(
                    "the condition has passed , " +
                            "binance value is %f, bitfinex value is %f order id is %s for command %s and the "
                    , conditionStatus.getBinancePrice(), conditionStatus.getBitfinexPrice()
                    ,limitOrderDetails.getOrderId(), type()));
            AtomicBoolean firstOrderComplete = new AtomicBoolean(false);

            Future<Boolean> future = orderComplete(limitOrderDetails.getOrderId(),
                    limitOrderDetails, firstOrderComplete);

            getOrderSuccessCallback().register(
                    limitOrderDetails.getOrderId(), secondOrder(limitOrderDetails.getAmount()), firstOrderComplete);

            Boolean success = future.get();
            if(success) {
                System.out.println("command passed : " + type());
                if (count > 10) {
                    commandsQueue.add(buildAnotherCommand(count - 1));
                }
            }else{
                System.out.println(format("building command after cancellation %s", type()));
                commandsQueue.add(buildAnotherCommand(count));
            }
        }else {
            Thread.sleep(1000);
            System.out.println(format("building another command with type %s", type()));
            commandsQueue.add(buildAnotherCommand(count));
        }
    }

    private Future<Boolean> orderComplete(String orderId, LimitOrderDetails limitOrderDetails, AtomicBoolean firstOrderComplete) {
        ConditionKeeperThread thread = new ConditionKeeperThread(
                keepOrderCondition(), cancelOrder(), orderId, firstOrderComplete, limitOrderDetails);

        final ExecutorService pool = Executors.newFixedThreadPool(1);

        return pool.submit(thread);


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

    abstract Runnable secondOrder(double amount);

    abstract OrderSuccessCallback getOrderSuccessCallback();

    abstract ArbCommand buildAnotherCommand(int count);

    abstract String type();
}
