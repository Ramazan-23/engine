package com.bitvavo.entity;

public enum Side {
    BUY("B"),
    SELL("S");

    private final String value;

    Side(String value) {
        this.value = value;
    }

    public static Side getByValue(String value) {
        if ("B".equals(value)) {
            return Side.BUY;
        }
        if ("S".equals(value)) {
            return Side.SELL;
        }
        throw new IllegalArgumentException("Unknown side for value " + value);
    }

}
