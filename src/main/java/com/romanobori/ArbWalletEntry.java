package com.romanobori;

import java.util.Objects;

public class ArbWalletEntry {

    String currency;
    double amount;
    double available;

    public ArbWalletEntry(String currency, double amount, double available) {
        this.currency = currency;
        this.amount = amount;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbWalletEntry that = (ArbWalletEntry) o;
        return Double.compare(that.amount, amount) == 0 &&
                Double.compare(that.available, available) == 0 &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {

        return Objects.hash(currency, amount, available);
    }
}
