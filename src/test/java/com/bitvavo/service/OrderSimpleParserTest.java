package com.bitvavo.service;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import com.bitvavo.serdes.OrderSimpleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class OrderSimpleParserTest {

    OrderSimpleParser TOT;

    @Mock
    InputStream inputStream;

    @BeforeEach
    void setUp() {
        TOT = new OrderSimpleParser(inputStream);
    }

    @Test
    public void shouldParseBuyOrder() {
        String sOrder = "10000,B,98,25500";
        Order order = TOT.parseOrder(sOrder);
        Assertions.assertEquals("10000", order.getOrderId());
        Assertions.assertEquals(Side.BUY, order.getSide());
        Assertions.assertEquals(98, order.getPrice());
        Assertions.assertEquals(25_500, order.getQuantity());
    }

    @Test
    public void shouldParseSellOrder() {
        String sOrder = "10005,S,105,20000";
        Order order = TOT.parseOrder(sOrder);
        Assertions.assertEquals("10005", order.getOrderId());
        Assertions.assertEquals(Side.SELL, order.getSide());
        Assertions.assertEquals(105, order.getPrice());
        Assertions.assertEquals(20_000, order.getQuantity());
    }
}