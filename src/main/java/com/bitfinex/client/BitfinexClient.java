package com.bitfinex.client;

import com.google.common.collect.ImmutableMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BitfinexClient {



    private String apiKey = "";
    private String apiKeySecret = "";

    /**
     * public access only
     */
    public BitfinexClient() {
        apiKey = null;
        apiKeySecret = null;
    }

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

    public String getOpenOrders(String symbol) throws IOException {

        return new BitfinexHttpHandler("/v1/book/" + symbol, Collections.EMPTY_MAP).invokePublic();
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
}
