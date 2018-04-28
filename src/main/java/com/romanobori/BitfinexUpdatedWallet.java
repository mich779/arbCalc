package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.Wallet;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.jnidzwetzki.bitfinex.v2.entity.Wallet.WALLET_TYPE_EXCHANGE;

public class BitfinexUpdatedWallet {

    private BitfinexApiBroker bitfinexApiBroker;
    private Map<String, Double> currency2FreeAmount = new ConcurrentHashMap<>();

    public BitfinexUpdatedWallet(BitfinexApiBroker bitfinexApiBroker) {
        this.bitfinexApiBroker = bitfinexApiBroker;
    }


    public double getFreeAmount(String symbol){
        return currency2FreeAmount.get(symbol);
    }
}
