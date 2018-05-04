package com.romanobori;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommonFunctions {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }



}
