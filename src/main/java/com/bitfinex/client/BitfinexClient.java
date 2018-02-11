package com.bitfinex.client;

import com.google.common.collect.ImmutableMap;


import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BitfinexClient {

    private String apiKey = "";
    private String apiKeySecret = "";

    /**
     * public and authenticated access
     *
     * @param apiKey
     * @param apiKeySecret
     */
    public BitfinexClient(String apiKey, String apiKeySecret) {
        this.apiKey = apiKey;
        this.apiKeySecret = apiKeySecret;
    }

    public String getBalances() {
        try {
            return new BitfinexHttpHandler("/v1/balances", Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getOrderBook(String symbol) {

        try {
            return new BitfinexHttpHandler("/v1/book/" + symbol, Collections.EMPTY_MAP).invokePublic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getMyActiveOrders() {

        try {
            return new BitfinexHttpHandler("/v1/orders", Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }


    public String addOrder(String symbol, double amount, double price, Action action) {
        Map<String, String> additionals = new HashMap<>();
        additionals.put("symbol" , symbol );
        additionals.put("amount", Double.toString(amount));
        additionals.put("price", Double.toString(price));
        additionals.put("exchange", "bitfinex");
        additionals.put("side", action.name());
        additionals.put("type", "exchange limit");

        try {
            return new BitfinexHttpHandler("/v1/order/new", additionals)
                    .invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public String addOrder(String symbol, double amount, Action action) {
        Map<String, String> additionals = new HashMap<>();
        additionals.put("symbol" , symbol );
        additionals.put("amount", Double.toString(amount));
        additionals.put("exchange", "bitfinex");
        additionals.put("side", action.name());
        additionals.put("type", "exchange market");

        try {
            return new BitfinexHttpHandler("/v1/order/new", additionals)
                    .invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public String cancelOrder(String orderId) {

        try {
            return new BitfinexHttpHandler("/v1/order/cancel",
                    ImmutableMap.of("order_id", orderId)
                    ).invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String cancellAllOrders()  {
        try {
            return new BitfinexHttpHandler("/v1/order/cancel/all",
                    Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String withdrawal(String currency ,String adress, String amount) {

        try {
            return new BitfinexHttpHandler("/v1/withdraw",
                    ImmutableMap.of("withdraw_type", currency,
                            "walletselected", "exchange",
                            "amount", amount,
                            "address", adress
                            )
            ).invokePrivate(apiKey, apiKeySecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
