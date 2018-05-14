package com.romanobori;

import com.romanobori.commands.LimitOrderDetails;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

public abstract class AmountFillerDetector extends Observable {



    public abstract void register(LimitOrderDetails orderId, Consumer<Double> secondOrder, Observer observer);
}
