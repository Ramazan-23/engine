package com.bitvavo.entity;

public class Trade {
    private String aggressorOrderId;
    private String initiatorOrderId;
    private int price;
    private int volume;

    public String getAggressorOrderId() {
        return aggressorOrderId;
    }

    public void setAggressorOrderId(String aggressorOrderId) {
        this.aggressorOrderId = aggressorOrderId;
    }

    public String getInitiatorOrderId() {
        return initiatorOrderId;
    }

    public void setInitiatorOrderId(String initiatorOrderId) {
        this.initiatorOrderId = initiatorOrderId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "trade " + aggressorOrderId + "," + initiatorOrderId + "," + price + "," + volume;
    }
}
