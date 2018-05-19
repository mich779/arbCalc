package com.romanobori.state;

public interface AmountChangedObserver {

    void updateInfo(String type, double newAmount);
}
