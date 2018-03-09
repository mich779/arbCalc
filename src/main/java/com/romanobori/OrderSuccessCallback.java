package com.romanobori;

public abstract class OrderSuccessCallback {

    public abstract void register(String orderId, Runnable action, ConditionKeeperThread t);
}
