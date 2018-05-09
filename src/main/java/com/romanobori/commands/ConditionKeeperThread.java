package com.romanobori.commands;

import com.romanobori.datastructures.ConditionStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.lang.String.format;

public class ConditionKeeperThread implements Callable<Boolean> {
    Function<LimitOrderDetails,ConditionStatus> condition;
    private AtomicBoolean orderComplete;
    private Function<String, Boolean> cancellation;
    private String orderId;
    private LimitOrderDetails orderDetails;
    ConditionKeeperThread(Function<LimitOrderDetails, ConditionStatus> condition,
                          Function<String, Boolean> actionIfNotMet, String orderId,
                          AtomicBoolean orderComplete, LimitOrderDetails orderDetails) {
        this.condition = condition;
        this.cancellation = actionIfNotMet;
        this.orderId = orderId;
        this.orderComplete = orderComplete;
        this.orderDetails = orderDetails;
    }

    @Override
    public Boolean call() throws Exception {
        while (orderNotCompleted()) {
            if (actionBreaked(condition)) {
                Boolean success = cancellation.apply(orderId);
                return !success;
            }
            Thread.sleep(500);
        }
        return Boolean.TRUE;
    }

    private boolean actionBreaked(Function<LimitOrderDetails,ConditionStatus> condition) {

        ConditionStatus conditionStatus = condition.apply(orderDetails);
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

