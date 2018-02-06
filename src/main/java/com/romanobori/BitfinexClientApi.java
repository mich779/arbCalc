package com.romanobori;

import com.bitfinex.client.BitfinexClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BitfinexClientApi implements ApiClient {
    BitfinexClient bitfinexClient;
    public BitfinexClientApi(BitfinexClient bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }

    @Override
    public ArbOrders getOpenOrders(String symbol) {

        String openOrdersJson = bitfinexClient.getOpenOrders(symbol);

        JsonObject jsonObject = new Gson().fromJson(openOrdersJson, JsonObject.class);

        return new ArbOrders(extractOrdersFromJson(jsonObject.getAsJsonArray("bids")),
                extractOrdersFromJson(jsonObject.getAsJsonArray("asks")));
    }

    private List<ArbOrderEntry> extractOrdersFromJson(JsonArray asks) {
        List<ArbOrderEntry> ordersEntries = new ArrayList<>();

        asks.forEach(entry ->{
                    JsonObject askObject = entry.getAsJsonObject();
                    ordersEntries.add(new ArbOrderEntry(askObject.get("price").getAsDouble(),
                            askObject.get("amount").getAsDouble()));

                }
        );
        return ordersEntries;
    }


    @Override
    public MyArbOrders getMyOrders() {
        return null;
    }

    @Override
    public void addArbOrder(NewArbOrder order) {

    }

    @Override
    public void cancelOrder(long orderId) {

    }

    @Override
    public void cancelAllOrders() {

    }

    @Override
    public void withdrawal(long withrawalId) {

    }
}
