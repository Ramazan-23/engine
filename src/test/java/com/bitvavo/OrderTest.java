package com.bitvavo;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import com.bitvavo.service.id.IdGenerator;
import com.bitvavo.service.id.TimeBasedIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    IdGenerator idGenerator = new TimeBasedIdGenerator();
    @Test
    void buyOrderWithBiggerPriceHasHigherPriority() {
        Order order1 = new Order("1", Side.BUY, 99, 300).setUniqueId(idGenerator.getNext());
        Order order2 = new Order("2", Side.BUY, 100, 300).setUniqueId(idGenerator.getNext());
        Assertions.assertEquals(-1, order2.compareTo(order1));
    }

    @Test
    void sellOrderWithLowerPriceHasHigherPriority() {
        Order order1 = new Order("1", Side.SELL, 100, 300).setUniqueId(idGenerator.getNext());
        Order order2 = new Order("2", Side.SELL, 101, 300).setUniqueId(idGenerator.getNext());
        Assertions.assertEquals(1, order2.compareTo(order1));
    }

    @Test
    @DisplayName("when orders has same price - earliest should have higher priority")
    void samePrice() {
        Order order1 = new Order("1", Side.BUY, 100, 300).setUniqueId(idGenerator.getNext());
        Order order2 = new Order("2", Side.BUY, 100, 300).setUniqueId(idGenerator.getNext());
        Assertions.assertEquals(-1, order1.compareTo(order2));

        Order order3 = new Order("3", Side.SELL, 100, 300).setUniqueId(idGenerator.getNext());
        Order order4 = new Order("4", Side.SELL, 100, 400).setUniqueId(idGenerator.getNext());
        Assertions.assertEquals(-1, order3.compareTo(order4));
    }

}