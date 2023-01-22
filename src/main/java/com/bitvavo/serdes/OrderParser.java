package com.bitvavo.serdes;

import com.bitvavo.entity.Order;

import java.util.stream.Stream;

public interface OrderParser {

    Stream<Order> readOrders();
}
