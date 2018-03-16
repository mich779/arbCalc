package com.romanobori.commands;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConditionKeeperThread implements Callable<Boolean> {
    Supplier<Boolean> condition;
    AtomicBoolean shouldRun = new AtomicBoolean(true);
    Consumer<String> actionIfNotMet;
    String orderId;
    public ConditionKeeperThread(Supplier<Boolean> condition,  Consumer<String> actionIfNotMet, String orderId) {
        this.condition = condition;
        this.actionIfNotMet = actionIfNotMet;
        this.orderId = orderId;
    }

    @Override
    public Boolean call() throws Exception {
        while (shouldRun.get()) {


            if (!condition.get()) {
                actionIfNotMet.accept(orderId);
                return Boolean.FALSE;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        return Boolean.TRUE;
    }

    public void stop(){
        shouldRun.set(false);
    }
}

