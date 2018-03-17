package com.romanobori.datastructures;

import java.util.Objects;

public class ArbOrderEntry {

    double price;
    double amount;


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbOrderEntry that = (ArbOrderEntry) o;
        return Double.compare(that.price, price) == 0 &&
                Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(price, amount);
    }

    @Override
    public String toString() {
        return "ArbOrderEntry{" +
                "price=" + price +
                ", amount=" + amount +
                '}';
    }

    public ArbOrderEntry(double price, double amount) {
        this.price = price;
        this.amount = amount;
    }
}
