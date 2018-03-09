package com.romanobori;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public abstract class ArbCommand {

    ArbPredicate predicate;
    ArbOrderBookUpdated updated1;
    ArbOrderBookUpdated updated2;

    public ArbCommand(ArbPredicate predicate) {
        this.predicate = predicate;
    }


    public void execute(){
        if(condition().get()) {
            String orderId = firstOrder().get();


            ConditionKeeperThread thread = new ConditionKeeperThread(
                    condition(), cancelOrder()
            );

            final ExecutorService pool = Executors.newFixedThreadPool(1);

            Future<Boolean> future = pool.submit(thread);


            getOrderSuccessCallback().register(
                    orderId, secondOrder(), thread
            );

        }
    }


    abstract Supplier<String> firstOrder();

    abstract Supplier<Boolean> condition();

    abstract Runnable cancelOrder();

    abstract Runnable secondOrder();

    abstract OrderSuccessCallback getOrderSuccessCallback();
}
