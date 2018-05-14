package com.romanobori.commands;

import com.google.common.util.concurrent.AtomicDouble;
import com.romanobori.datastructures.ConditionStatus;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.lang.String.format;

public class ConditionKeeperThread implements Runnable, Observer {
    Function<LimitOrderDetails, ConditionStatus> condition;
    private CountDownLatch countDownLatch;
    private Function<String, Boolean> cancellation;
    private String orderId;
    private AtomicDouble amount;
    private AtomicDouble price;
    private AtomicBoolean orderFilled = new AtomicBoolean(false);

    ConditionKeeperThread(Function<LimitOrderDetails, ConditionStatus> condition,
                          Function<String, Boolean> actionIfNotMet, String orderId,
                          CountDownLatch countDownLatch, LimitOrderDetails orderDetails) {
        this.condition = condition;
        this.cancellation = actionIfNotMet;
        this.orderId = orderId;
        this.countDownLatch = countDownLatch;
        this.amount = new AtomicDouble(orderDetails.getAmount());
        this.price = new AtomicDouble(orderDetails.getPrice());
    }

    @Override
    public void run() {
        while (! orderFilled.get()) {
            if (actionBreaked(condition)) {
                cancellation.apply(orderId);
                System.out.println("the command cancelled");
                countDownLatch.countDown();
                break;
            }
        }
    }

    private boolean actionBreaked(Function<LimitOrderDetails, ConditionStatus> condition) {
        ConditionStatus conditionStatus = condition.apply(
                new LimitOrderDetails(orderId, price.get(), amount.get())
        );
        if (conditionStatus.isPassed()) {
            System.out.println(format("condition is passed, binanacePrice is %f " +
                            "bitfinex price is %f", conditionStatus.getBinancePrice()
                    , conditionStatus.getBitfinexPrice()));
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        UpdateConditionDetails details = (UpdateConditionDetails) arg;
        if(details.getUpdateType().equals("PARTIAL")){
            amount.set(details.getAmount());
        }else if(details.getUpdateType().equals("FULL")){
            orderFilled.set(true);
        }
    }
}

