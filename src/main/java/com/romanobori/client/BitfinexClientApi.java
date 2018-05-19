package com.romanobori.client;

import com.bitfinex.client.Action;
import com.bitfinex.client.BitfinexClient;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.romanobori.datastructures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BitfinexClientApi implements ApiClient {
    BitfinexClient bitfinexClient;
    public BitfinexClientApi(BitfinexClient bitfinexClient) {
        this.bitfinexClient = bitfinexClient;
    }
    Map<String, String> currencyToAdress = ImmutableMap.of("BTC", "", "NEO", "");

    @Override
    public ArbOrders getOrderBook(String symbol) {

        JsonObject jsonObject = new Gson().fromJson(bitfinexClient.getOrderBook(symbol),
                JsonObject.class);

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
    public List<MyArbOrder> getMyOrders() {
        String myActiveOrders = bitfinexClient.getMyActiveOrders();

        JsonArray ordersArray = new Gson().fromJson(myActiveOrders, JsonArray.class);

        return extractMyArbOrdersFromArray(ordersArray);
    }

    private List<MyArbOrder> extractMyArbOrdersFromArray(JsonArray ordersArray) {
        List<MyArbOrder> myArbOrders = new ArrayList<>();
        ordersArray.forEach(
                order -> {
                    JsonObject orderObj = order.getAsJsonObject();

                    myArbOrders.add(new MyArbOrder(
                            orderObj.get("symbol").getAsString(),
                            orderObj.get("id").getAsString(),
                            orderObj.get("price").getAsDouble(),
                            orderObj.get("original_amount").getAsDouble(),
                            orderObj.get("executed_amount").getAsDouble(),
                            orderObj.get("side").getAsString().equals("sell") ? ARBTradeAction.SELL : ARBTradeAction.BUY,
                            (long)(orderObj.get("timestamp").getAsDouble())
                    ));

                }
        );
        return myArbOrders;
    }

    @Override
    public String addArbOrder(NewArbOrderMarket order) {
                    String answer = bitfinexClient.addOrder(order.getSymbol(), order.getQuantity(),
                    order.getAction()== ARBTradeAction.BUY? Action.buy : Action.sell);
            return new Gson().fromJson(answer, JsonObject.class).get("id").getAsString();
    }

    @Override
    public String addArbOrder(NewArbOrderLimit order) {
        String answer = bitfinexClient.addOrder(order.getSymbol(), order.getQuantity(),
                order.getPrice(),
                order.getAction()== ARBTradeAction.BUY? Action.buy : Action.sell);
        return new Gson().fromJson(answer, JsonObject.class).get("id").getAsString();
    }

    @Override
    public void cancelOrder(String symbol, String orderId) {
        bitfinexClient.cancelOrder(orderId);
    }


    @Override
    public void cancelAllOrders() {
        bitfinexClient.cancellAllOrders();
    }

    @Override
    public void withdrawal(ArbWalletEntry withdrawalDetails) {

        bitfinexClient.withdrawal(withdrawalDetails.getCurrency(), currencyToAdress.get(withdrawalDetails.getCurrency()),
                Double.toString(withdrawalDetails.getAmount()));

    }

    @Override
    public ArbWallet getWallet() {
        String balanceJson = bitfinexClient.getBalances();

        JsonArray balances = new Gson().fromJson(balanceJson, JsonArray.class);

        return new ArbWallet(extractWalletEntriesFromJson(balances));

    }

    private List<ArbWalletEntry> extractWalletEntriesFromJson(JsonArray balances) {
        List<ArbWalletEntry> entries = new ArrayList<>();
        balances.forEach(
                balance -> {
                    JsonObject asJsonObject = balance.getAsJsonObject();
                    entries.add(new ArbWalletEntry(
                            asJsonObject.get("currency").getAsString(),
                            asJsonObject.get("amount").getAsDouble(),
                            asJsonObject.get("available").getAsDouble()
                ));
                }
        );
        return entries;
    }
}
