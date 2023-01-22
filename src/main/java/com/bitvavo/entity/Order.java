package com.bitvavo.entity;

import uk.elementarysoftware.quickcsv.api.CSVRecord;

public class Order implements Comparable<Order> {
    // TODO use Chronicle Bytes
    private String orderId;
    /**
     * Timestamp based unique ID
     */
    private long uniqueId;
    private Side side;
    private int price;
    private int quantity;

    public Order(String orderId,
                 Side side,
                 final int price,
                 final int quantity) {
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public Order(CSVRecord record) {
        this.orderId = record.getNextField().asString();
        this.side = Side.getByValue(record.getNextField().asString());
        this.price = record.getNextField().asInt();
        this.quantity = record.getNextField().asInt();
    }

    public void validate() {
        if (price < 0 || price > 999_999) {
            throw new IllegalStateException("Invalid price " + price);
        }
        if (quantity < 0 ||  quantity > 999_999_999) {
            throw new IllegalStateException("Invalid quantity " + quantity);
        }
        if (orderId.length() == 0) {
            throw new IllegalStateException("orderId not set");
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void decreaseQuantity(int delta) {
        quantity -= delta;
    }

    public Side getSide() {
        return side;
    }

    public Order setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    @Override
    public int compareTo(Order that) {
        int priceComparison = Integer.compare(this.price, that.price);

        // sell orders with lower price has higher priority in execution
        // buy order with higher price has higher priority in execution
        if (priceComparison != 0) {
            return side == Side.SELL ? priceComparison: -priceComparison;
        }
        return Long.compare(this.uniqueId, that.uniqueId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Order order = (Order) o;

        return uniqueId == order.uniqueId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Long.hashCode(uniqueId);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", uniqueId=" + uniqueId +
                ", side=" + side +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
