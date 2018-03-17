package com.romanobori.datastructures;

import java.util.List;

public class ArbWallet {
    List<ArbWalletEntry> entries;


    public List<ArbWalletEntry> getEntries() {
        return entries;
    }

    public ArbWallet(List<ArbWalletEntry> entries) {
        this.entries = entries;
    }
}
