package com.romanobori;

import com.romanobori.commands.ConditionKeeperThread;

public abstract class OrderSuccessCallback {

    public abstract void register(String orderId, Runnable action, ConditionKeeperThread t);
}
