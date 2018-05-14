package com.romanobori.commands;

public class UpdateConditionDetails {
    String updateType;
    double amount;

    public UpdateConditionDetails(String updateType, double amount) {
        this.updateType = updateType;
        this.amount = amount;
    }

    public String getUpdateType() {
        return updateType;
    }

    public double getAmount() {
        return amount;
    }
}
