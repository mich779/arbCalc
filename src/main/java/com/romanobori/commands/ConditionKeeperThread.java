package com.romanobori.commands;

import com.romanobori.datastructures.ConditionStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;

public class ConditionKeeperThread implements Callable<Boolean> {
    Function<Double,ConditionStatus> condition;
    AtomicBoolean orderComplete;
    Consumer<String> actionIfNotMet;
    String orderId;
    Double price;
    public ConditionKeeperThread(Function<Double,ConditionStatus> condition,
                                 Consumer<String> actionIfNotMet, String orderId,
                                 AtomicBoolean orderComplete, Double price) {
        this.condition = condition;
        this.actionIfNotMet = actionIfNotMet;
        this.orderId = orderId;
        this.orderComplete = orderComplete;
        this.price = price;
    }

    @Override
    public Boolean call() throws Exception {
        long timeStarted = System.currentTimeMillis();
        while (orderNotCompleted()) {
            if (actionBreaked(condition) || System.currentTimeMillis() - timeStarted > 30 * 1000) {
                actionIfNotMet.accept(orderId);
                return Boolean.FALSE;
            }
            Thread.sleep(500);
        }
        return Boolean.TRUE;
    }

    private boolean actionBreaked(Function<Double,ConditionStatus> condition) {

        ConditionStatus conditionStatus = condition.apply(price);
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

