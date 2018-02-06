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

    public String getBalances() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        return new BitfinexHttpHandler("/v1/balances", Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);
    }

    public String getOpenOrders(String symbol) {

        try {
            return new BitfinexHttpHandler("/v1/book/" + symbol, Collections.EMPTY_MAP).invokePublic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getMyActiveOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        return new BitfinexHttpHandler("/v1/orders", Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);
    }


    public String addOrder(String symbol, double amount, double price, Action action) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        Map<String, String> additionals = new HashMap<>();
        additionals.put("symbol" , symbol );
        additionals.put("amount", Double.toString(amount));
        additionals.put("price", Double.toString(price));
        additionals.put("exchange", "bitfinex");
        additionals.put("side", action.name());
        additionals.put("type", "exchange limit");
        return new BitfinexHttpHandler("/v1/order/new", additionals)
                .invokePrivate(apiKey, apiKeySecret);
    }

    public String cancelOrder(String orderId) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        return new BitfinexHttpHandler("/v1/order/cancel",
                ImmutableMap.of("order_id", orderId)
                ).invokePrivate(apiKey, apiKeySecret);
    }

    public String cancellAllOrders() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
            return new BitfinexHttpHandler("/v1/order/cancel/all",
                    Collections.EMPTY_MAP).invokePrivate(apiKey, apiKeySecret);


    }

    public String withdrawal(String amount) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        return new BitfinexHttpHandler("/v1/withdraw",
                ImmutableMap.of("withdraw_type", "neo",
                        "walletselected", "exchange",
                        "amount", amount,
                        "address", "AX3akz59X88sQ3sWgjyqYWK9RUKUdg9cYk"
                        )
        ).invokePrivate(apiKey, apiKeySecret);
    }
}
