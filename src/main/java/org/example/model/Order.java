package org.example.model;

public class Order {
    private int id;
    private String name;
    private String date;
    private double priceInUSD;
    private double priceInPLN;

    public Order(String name, String date, double priceInUSD, double priceInPLN) {
        this.name = name;
        this.date = date;
        this.priceInUSD = priceInUSD;
        this.priceInPLN = priceInPLN;
    }

    public double getPriceInUSD() {
        return priceInUSD;
    }

    public double getPriceInPLN() {
        return priceInPLN;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}