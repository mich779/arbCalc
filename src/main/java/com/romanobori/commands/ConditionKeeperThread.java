package com.romanobori.commands;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConditionKeeperThread implements Callable<Boolean> {
    Supplier<Boolean> condition;
    AtomicBoolean orderComplete;
    Consumer<String> actionIfNotMet;
    String orderId;
    public ConditionKeeperThread(Supplier<Boolean> condition, Consumer<String> actionIfNotMet, String orderId, AtomicBoolean orderComplete) {
        this.condition = condition;
        this.actionIfNotMet = actionIfNotMet;
        this.orderId = orderId;
        this.orderComplete = orderComplete;
    }

    @Override
    public Boolean call() throws Exception {
        while (orderNotCompleted()) {
            if (actionBreaked(condition)) {
                actionIfNotMet.accept(orderId);
                return Boolean.FALSE;
            }
            Thread.sleep(500);
        }
        return Boolean.TRUE;
    }

    private boolean actionBreaked(Supplier<Boolean> condition) {
        return !condition.get();
    }

    private boolean orderNotCompleted() {
        return !orderComplete.get();
    }

}

