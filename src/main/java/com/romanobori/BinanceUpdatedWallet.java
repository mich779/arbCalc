package com.romanobori;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.romanobori.datastructures.ArbWallet;
import com.romanobori.datastructures.ArbWalletEntry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BinanceUpdatedWallet {
    private BinanceApiWebSocketClient socketClient;
    private String binanceListenKey;
    private BinanceApiClient binanceApiClient;
    private Map<String, Double> currency2FreeAmount = new ConcurrentHashMap<>();

    public BinanceUpdatedWallet(BinanceApiWebSocketClient socketClient,
                                BinanceApiClient binanceApiClient,
                                String binanceListenKey) {
        this.binanceApiClient = binanceApiClient;
        this.socketClient = socketClient;
        this.binanceListenKey = binanceListenKey;
        init();
        activateListener();
    }

    private void init(){
        ArbWallet wallet = binanceApiClient.getWallet();
        for( ArbWalletEntry entry : wallet.getEntries()){
            currency2FreeAmount.put(entry.getCurrency(), entry.getAvailable());
        }
    }

    private void activateListener(){
        socketClient.onUserDataUpdateEvent(binanceListenKey,
                userDataUpdateEvent -> {
                    if(userDataUpdateEvent.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_UPDATE){
                        AccountUpdateEvent accountUpdateEvent = userDataUpdateEvent.getAccountUpdateEvent();
                        List<AssetBalance> balances = accountUpdateEvent.getBalances();
                        for(AssetBalance assetBalance : balances){
                            currency2FreeAmount.put(assetBalance.getAsset(),
                                    Double.parseDouble(assetBalance.getFree()));
                        }
                    }
                });
    }

    public double getFreeAmount(String symbol){
        return currency2FreeAmount.get(symbol);
    }

    public Map<String, Double> getCurrency2FreeAmount() {
        return currency2FreeAmount;
    }
}
