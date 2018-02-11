package com.romanobori;

import java.util.Objects;

public class NewArbOrderLimit extends NewArbOrder {
    double price;


    public NewArbOrderLimit(String symbol, ARBTradeAction action, double quantity, double price) {

        super(symbol, action, quantity);
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewArbOrderLimit)) return false;
        if (! super.equals(o)) return false;
        NewArbOrderLimit that = (NewArbOrderLimit) o;
        return Double.compare(that.price, price) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(price);
    }


}
