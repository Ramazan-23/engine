package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

class OrderQuickCsvParserTest {

    OrderQuickCsvParser TOT;

    @Test
    void readOrdersScenario1() {
        TOT = new OrderQuickCsvParser(Paths.get("src/test/resources/scenarios/test1.txt"));
        List<Order> actual = TOT.readOrders().collect(Collectors.toList());
        List<Order> expected = List.of(
                new Order(String.valueOf(10000), Side.BUY, 98, 25_500),
                new Order(String.valueOf(10005), Side.SELL, 105, 20_000),
                new Order(String.valueOf(10001), Side.SELL, 100, 500),
                new Order(String.valueOf(10002), Side.SELL, 100, 10_000),
                new Order(String.valueOf(10003), Side.BUY, 99, 50_000),
                new Order(String.valueOf(10004), Side.SELL, 103, 100)

        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void readOrdersScenario2() {
        TOT = new OrderQuickCsvParser(Paths.get("src/test/resources/scenarios/test2.txt"));
        List<Order> actual = TOT.readOrders().collect(Collectors.toList());
        List<Order> expected = List.of(
                new Order(String.valueOf(10000), Side.BUY, 98, 25_500),
                new Order(String.valueOf(10005), Side.SELL, 105, 20_000),
                new Order(String.valueOf(10001), Side.SELL, 100, 500),
                new Order(String.valueOf(10002), Side.SELL, 100, 10_000),
                new Order(String.valueOf(10003), Side.BUY, 99, 50_000),
                new Order(String.valueOf(10004), Side.SELL, 103, 100),
                new Order(String.valueOf(10006), Side.BUY, 105, 16_000)

        );
        Assertions.assertEquals(expected, actual);
    }
}