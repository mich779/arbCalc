package com.romanobori;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ConditionKeeperThread implements Callable<Boolean> {
    Supplier<Boolean> condition;
    AtomicBoolean shouldRun = new AtomicBoolean(true);
    Runnable actionIfNotMet;

    public ConditionKeeperThread(Supplier<Boolean> condition,  Runnable actionIfNotMet) {
        this.condition = condition;
        this.actionIfNotMet = actionIfNotMet;
    }

    @Override
    public Boolean call() throws Exception {
        while (shouldRun.get()) {


            if (!condition.get()) {
                actionIfNotMet.run();
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

