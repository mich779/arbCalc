package com.romanobori;

public interface AmountChangedObserver {

    void updateInfo(String type, double newAmount);
}
