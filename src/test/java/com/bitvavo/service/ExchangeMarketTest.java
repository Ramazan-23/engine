package com.bitvavo.service;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Iterator;

class ExchangeMarketTest {
    TradeListener tradeListener = Mockito.mock(TradeListener.class);
    ExchangeMarket TOT;
    long orderId;

    @BeforeEach
    void setUp() {
        TOT = new ExchangeMarket(tradeListener);
        orderId = 0L;
    }

    @Test
    @DisplayName("Adding orders without trades")
    void simpleAddingOrdersWithoutTrades() {
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 100, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 101, 100).setUniqueId(orderId++));
        
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 102, 100).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 103, 200).setUniqueId(orderId++));

        Mockito.verifyNoInteractions(tradeListener);
        Assertions.assertEquals(101, TOT.getBestBid());
        Assertions.assertEquals(102, TOT.getBestAsk());

        Assertions.assertEquals(2, TOT.getBuyOrders().size());
        Iterator<Order> buyOrdersIt = TOT.getBuyOrders().iterator();
        Order buyOrder1 = buyOrdersIt.next();
        Assertions.assertEquals(101, buyOrder1.getPrice());
        Assertions.assertEquals(100, buyOrder1.getQuantity());
        Assertions.assertEquals(Side.BUY, buyOrder1.getSide());
        
        Order buyOrder2 = buyOrdersIt.next();
        Assertions.assertEquals(100, buyOrder2.getPrice());
        Assertions.assertEquals(200, buyOrder2.getQuantity());
        Assertions.assertEquals(Side.BUY, buyOrder2.getSide());

        Assertions.assertEquals(2, TOT.getSellOrders().size());
        Iterator<Order> sellOrdersIt = TOT.getSellOrders().iterator();
        Order sellOrder1 = sellOrdersIt.next();
        Assertions.assertEquals(102, sellOrder1.getPrice());
        Assertions.assertEquals(100, sellOrder1.getQuantity());
        Assertions.assertEquals(Side.SELL, sellOrder1.getSide());

        Order sellOrder2 = sellOrdersIt.next();
        Assertions.assertEquals(103, sellOrder2.getPrice());
        Assertions.assertEquals(200, sellOrder2.getQuantity());
        Assertions.assertEquals(Side.SELL, sellOrder2.getSide());
    }

    @Test
    @DisplayName("Simple full matching trade")
    void simpleFullTradeTest() {
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 100, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 100, 200).setUniqueId(orderId++));

        Mockito.verify(tradeListener).onTrade(Mockito.argThat(trade ->
                trade.getAggressorOrderId().equals("1") &&
                trade.getInitiatorOrderId().equals("0") &&
                trade.getPrice() == 100 &&
                trade.getVolume() == 200));
        Mockito.verifyNoMoreInteractions(tradeListener);

        Assertions.assertEquals(0, TOT.getBestBid());
        Assertions.assertEquals(Integer.MAX_VALUE, TOT.getBestAsk());
        Assertions.assertTrue(TOT.getBuyOrders().isEmpty());
        Assertions.assertTrue(TOT.getSellOrders().isEmpty());
    }

    @Test
    @DisplayName("Partial execution on initiator's side")
    void partialExecution1() {
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 100, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 101, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 101, 50).setUniqueId(orderId++));

        Mockito.verify(tradeListener).onTrade(Mockito.argThat(trade ->
                trade.getAggressorOrderId().equals("2") &&
                trade.getInitiatorOrderId().equals("1") &&
                trade.getPrice() == 101 &&
                trade.getVolume() == 50));
        Mockito.verifyNoMoreInteractions(tradeListener);

        Assertions.assertEquals(100, TOT.getBestBid());
        Assertions.assertEquals(101, TOT.getBestAsk());

        Assertions.assertEquals(1, TOT.getBuyOrders().size());
        Order buyOrder1 = TOT.getBuyOrders().iterator().next();
        Assertions.assertEquals(100, buyOrder1.getPrice());
        Assertions.assertEquals(200, buyOrder1.getQuantity());

        Assertions.assertEquals(1, TOT.getSellOrders().size());
        Order sellOrder1 = TOT.getSellOrders().iterator().next();
        Assertions.assertEquals(101, sellOrder1.getPrice());
        Assertions.assertEquals(150, sellOrder1.getQuantity());
    }


    @Test
    @DisplayName("Partial execution on aggressor's side")
    void partialExecution2() {
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 100, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 99, 400).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 101, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 102, 400).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 100, 300).setUniqueId(orderId++));

        Mockito.verify(tradeListener).onTrade(Mockito.argThat(trade ->
                trade.getAggressorOrderId().equals("4") &&
                        trade.getInitiatorOrderId().equals("0") &&
                        trade.getPrice() == 100 &&
                        trade.getVolume() == 200));
        Mockito.verifyNoMoreInteractions(tradeListener);

        Assertions.assertEquals(99, TOT.getBestBid());
        Assertions.assertEquals(100, TOT.getBestAsk());

        Assertions.assertEquals(1, TOT.getBuyOrders().size());
        Iterator<Order> buyOrdersIt = TOT.getBuyOrders().iterator();
        Order buyOrder1 = buyOrdersIt.next();
        Assertions.assertEquals(99, buyOrder1.getPrice());
        Assertions.assertEquals(400, buyOrder1.getQuantity());


        Assertions.assertEquals(3, TOT.getSellOrders().size());
        Iterator<Order> sellOrdersIt = TOT.getSellOrders().iterator();
        Order sellOrder1 = sellOrdersIt.next();
        Assertions.assertEquals(100, sellOrder1.getPrice());
        Assertions.assertEquals(100, sellOrder1.getQuantity());

        Order sellOrder2 = sellOrdersIt.next();
        Assertions.assertEquals(101, sellOrder2.getPrice());
        Assertions.assertEquals(200, sellOrder2.getQuantity());

        Order sellOrder3 = sellOrdersIt.next();
        Assertions.assertEquals(102, sellOrder3.getPrice());
        Assertions.assertEquals(400, sellOrder3.getQuantity());
    }

    @Test
    @DisplayName("Execution of multiple orders")
    void executionOfMultipleOrders() {
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 95, 400).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 100, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.BUY, 101, 100).setUniqueId(orderId++));

        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 102, 200).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 103, 300).setUniqueId(orderId++));
        TOT.onOrder(new Order(String.valueOf(orderId), Side.SELL, 98, 350).setUniqueId(orderId++));

        Mockito.verify(tradeListener).onTrade(Mockito.argThat(trade ->
                trade.getAggressorOrderId().equals("5") &&
                        trade.getInitiatorOrderId().equals("2") &&
                        trade.getPrice() == 101 &&
                        trade.getVolume() == 100));

        Mockito.verify(tradeListener).onTrade(Mockito.argThat(trade ->
                trade.getAggressorOrderId().equals("5") &&
                        trade.getInitiatorOrderId().equals("1") &&
                        trade.getPrice() == 100 &&
                        trade.getVolume() == 200));
        Mockito.verifyNoMoreInteractions(tradeListener);

        Assertions.assertEquals(95, TOT.getBestBid());
        Assertions.assertEquals(98, TOT.getBestAsk());

        Assertions.assertEquals(1, TOT.getBuyOrders().size());
        Iterator<Order> buyOrdersIt = TOT.getBuyOrders().iterator();
        Order buyOrder1 = buyOrdersIt.next();
        Assertions.assertEquals(95, buyOrder1.getPrice());
        Assertions.assertEquals(400, buyOrder1.getQuantity());

        Assertions.assertEquals(3, TOT.getSellOrders().size());
        Iterator<Order> sellOrdersIt = TOT.getSellOrders().iterator();
        Order sellOrder1 = sellOrdersIt.next();
        Assertions.assertEquals(98, sellOrder1.getPrice());
        Assertions.assertEquals(50, sellOrder1.getQuantity());

        Order sellOrder2 = sellOrdersIt.next();
        Assertions.assertEquals(102, sellOrder2.getPrice());
        Assertions.assertEquals(200, sellOrder2.getQuantity());

        Order sellOrder3 = sellOrdersIt.next();
        Assertions.assertEquals(103, sellOrder3.getPrice());
        Assertions.assertEquals(300, sellOrder3.getQuantity());
    }
}