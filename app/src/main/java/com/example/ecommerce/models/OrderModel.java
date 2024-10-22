package com.example.ecommerce.models;

public class OrderModel {

    public double amount;

    public OrderModel(double amount) {
        this.amount = amount;
    }

    public OrderModel() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
