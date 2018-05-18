package com.romanobori;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

public abstract class AmountFillerDetectorObservable extends Observable {

    private List<Observer> observers = new ArrayList<>();
    abstract void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder);


    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder, Observer observer){
        observers.add(observer);
        register(orderDetails, secondOrder);
    }

    void notifyObservers(String status) {
        observers.forEach(observer -> observer.update(this, status));
    }
}
