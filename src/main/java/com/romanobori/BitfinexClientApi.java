package com.romanobori;

import com.bitfinex.client.Action;
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
                            orderObj.get("executed_amount").getAsDouble(), ARBTradeAction.SELL,
                            (long)(orderObj.get("timestamp").getAsDouble())
                    ));

                }
        );
        return myArbOrders;
    }

    @Override
    public void addArbOrder(NewArbOrder order) {
            bitfinexClient.addOrder(order.symbol, order.quantity, order.price,
                    order.action == ARBTradeAction.BUY? Action.buy : Action.sell);
    }

    @Override
    public void cancelOrder(long orderId) {
        bitfinexClient.cancelOrder(Long.toString(orderId));
    }

    @Override
    public void cancelAllOrders() {
        bitfinexClient.cancellAllOrders();
    }

    @Override
    public void withdrawal(long withrawalId) {

        bitfinexClient.withdrawal(Long.toString(withrawalId));

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
