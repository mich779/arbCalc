package com.romanobori.commands;

import com.romanobori.datastructures.ConditionStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;

public class ConditionKeeperThread implements Callable<Boolean> {
    Supplier<ConditionStatus> condition;
    AtomicBoolean orderComplete;
    Consumer<String> actionIfNotMet;
    String orderId;
    public ConditionKeeperThread(Supplier<ConditionStatus> condition, Consumer<String> actionIfNotMet, String orderId, AtomicBoolean orderComplete) {
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

    private boolean actionBreaked(Supplier<ConditionStatus> condition) {

        ConditionStatus conditionStatus = condition.get();
        if(conditionStatus.isPassed()){
            System.out.println(format("condition is passed, binanacePrice is %f " +
                    "bitfinex price is %f", conditionStatus.getBinancePrice()
            , conditionStatus.getBitfinexPrice()));
            return false;
        }else{
            return true;
        }
    }

    private boolean orderNotCompleted() {
        return !orderComplete.get();
    }

}

