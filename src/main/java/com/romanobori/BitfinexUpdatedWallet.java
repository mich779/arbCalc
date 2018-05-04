package com.romanobori;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.Wallet;

import java.util.Collection;

import static com.github.jnidzwetzki.bitfinex.v2.entity.Wallet.WALLET_TYPE_EXCHANGE;

public class BitfinexUpdatedWallet {

    private BitfinexApiBroker bitfinexApiBroker;

    public BitfinexUpdatedWallet(BitfinexApiBroker bitfinexApiBroker) {
        this.bitfinexApiBroker = bitfinexApiBroker;
    }


    public double getFreeAmount(String symbol){
        try {
            Collection<Wallet> wallets = bitfinexApiBroker.getWallets();
            for(Wallet wallet : wallets){
                if(wallet.getWalletType().equals(WALLET_TYPE_EXCHANGE)){
                   if(wallet.getCurreny().equals(symbol)){
                       return wallet.getBalance();
                   }
                }
            }

        } catch (APIException e) {
            throw new RuntimeException(e);
        }

        return 0.0;
    }
}
