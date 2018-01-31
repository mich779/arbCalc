package com.bitfinex.client;

public enum Action {
    buy("buy"), sell("sell");

    private final String action;
    Action(String action){
        this.action = action;
    }
}
