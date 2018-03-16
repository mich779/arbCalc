package com.romanobori;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OrderSuccessCallback {

    public abstract void register(String orderId, Runnable action, AtomicBoolean orderCompletionMarker);
}
