package com.bitvavo.service;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import com.bitvavo.entity.Trade;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

public class ExchangeMarket implements OrderListener {
    private static final Logger log = LoggerFactory.getLogger(ExchangeMarket.class);
    private volatile boolean isStopped = false;

    private final TradeListener tradeListener;

    private final TreeSet<Order> buyOrders = new TreeSet<>();
    private final TreeSet<Order> sellOrders = new TreeSet<>();

    private int bestBid = 0;
    private int bestAsk = Integer.MAX_VALUE;

    public ExchangeMarket(@NotNull final TradeListener tradeListener) {
        this.tradeListener = Objects.requireNonNull(tradeListener);
    }

    @Override
    public void onOrder(Order order) {
        if (isStopped) {
            log.info("Exchange is stopped. Unprocessed order {}", order);
            return;
        }

        try {
            order.validate();

            boolean isBuy = order.getSide() == Side.BUY;
            TreeSet<Order> orders = isBuy ? buyOrders : sellOrders;

            if (isNotRemovingLiquidity(order)) {
                orders.add(order);
                updateBestBidAsk();
                return;
            }

            TreeSet<Order> counterOrders = isBuy ? sellOrders : buyOrders;

            Iterator<Order> orderIterator = counterOrders.iterator();
            while (order.getQuantity() > 0 && orderIterator.hasNext()) {
                Order counterOrder = orderIterator.next();
                if (doNotMatchByPrice(order, counterOrder)) {
                    break;
                }

                int quantity = order.getQuantity();
                int counterQuantity = counterOrder.getQuantity();

                Trade trade = new Trade(); // TODO make reusable object
                trade.setAggressorOrderId(order.getOrderId());
                trade.setInitiatorOrderId(counterOrder.getOrderId());
                trade.setPrice(counterOrder.getPrice());

                if (order.getQuantity() >= counterOrder.getQuantity()) {
                    order.decreaseQuantity(counterQuantity);
                    counterOrder.decreaseQuantity(counterQuantity);
                    trade.setVolume(counterQuantity);
                    orderIterator.remove();
                } else {
                    order.decreaseQuantity(quantity);
                    counterOrder.decreaseQuantity(quantity);
                    trade.setVolume(quantity);
                }
                tradeListener.onTrade(trade);
            }

            if (order.getQuantity() != 0) {
                orders.add(order);
            }
            updateBestBidAsk();
        } catch (Exception e) {
            log.error("event=order_processing_execution_failed order={}", order, e);
            log.debug("event=order_processing_execution_failed buy_orders={} sell_orders={}", buyOrders, sellOrders);
        }
    }

    @Override
    public void stop() {
        isStopped = true;
        try {
            tradeListener.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.debug("event=exchange_stopped buy_orders={} sell_orders={}", buyOrders, sellOrders);
    }

    private static boolean doNotMatchByPrice(Order order, Order counterOrder) {
        return order.getSide() == Side.BUY && counterOrder.getPrice() > order.getPrice() ||
                order.getSide() == Side.SELL && counterOrder.getPrice() < order.getPrice();
    }

    private void updateBestBidAsk() {
        bestBid = !buyOrders.isEmpty() ? buyOrders.first().getPrice() : 0;
        bestAsk = !sellOrders.isEmpty() ? sellOrders.first().getPrice() : Integer.MAX_VALUE;
    }

    private boolean isNotRemovingLiquidity(Order order) {
        return order.getSide() == Side.BUY
                ? order.getPrice() < bestAsk
                : order.getPrice() > bestBid;
    }

    public TreeSet<Order> getBuyOrders() {
        return buyOrders;
    }

    public TreeSet<Order> getSellOrders() {
        return sellOrders;
    }

    public int getBestBid() {
        return bestBid;
    }

    public int getBestAsk() {
        return bestAsk;
    }
}
