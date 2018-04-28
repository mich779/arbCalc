package com.romanobori.datastructures;

import java.util.Objects;

public class ArbWalletEntry {

    private String currency;
    private double amount;
    private double available;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    @Override
    public int hashCode() {

        return Objects.hash(currency, amount, available);
    }
}
