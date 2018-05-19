package com.romanobori.state;

import com.romanobori.datastructures.LimitOrderDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AmountFillerDetectorObservable{

    private List<AmountChangedObserver> observers = new ArrayList<>();
    abstract void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder);


    public void register(LimitOrderDetails orderDetails, Consumer<Double> secondOrder, AmountChangedObserver observer){
        observers.add(observer);
        register(orderDetails, secondOrder);
    }

    void notifyObservers(String status, double newAmount) {
        observers.forEach(observer -> observer.updateInfo(status, newAmount));
    }
}
