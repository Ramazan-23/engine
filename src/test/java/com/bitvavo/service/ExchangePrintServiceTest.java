package com.bitvavo.service;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import com.bitvavo.serdes.ExchangePrintService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ExchangePrintServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ExchangePrintServiceTest.class);
    long orderId = 0;
    ExchangePrintService TOT = new ExchangePrintService();

    @Mock
    ExchangeMarket exchangeMarket;

    @Test
    void whenSellOrdersCountIsBigger() throws Exception {
        TreeSet<Order> buyOrders = Stream.of(
                new Order("10000", Side.BUY, 98, 25_500).setUniqueId(orderId++),
                new Order("10003", Side.BUY, 99, 50_000).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<Order> sellOrders = Stream.of(
                new Order("10005", Side.SELL, 105, 20_000).setUniqueId(orderId++),
                new Order("10001", Side.SELL, 100, 500).setUniqueId(orderId++),
                new Order("10002", Side.SELL, 100, 10_000).setUniqueId(orderId++),
                new Order("10004", Side.SELL, 103, 100).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        Mockito.when(exchangeMarket.getBuyOrders()).thenReturn(buyOrders);
        Mockito.when(exchangeMarket.getSellOrders()).thenReturn(sellOrders);

        String expected =
                "     50,000     99 |    100         500\n" +
                "     25,500     98 |    100      10,000\n" +
                "                   |    103         100\n" +
                "                   |    105      20,000\n";

        // taken from Example 1
        String checksum = "8ff13aad3e61429bfb5ce0857e846567";
        String actual = TOT.printMarket(exchangeMarket);
        log.info(actual);
        Assertions.assertEquals(expected, actual);

        String myHash = calcChecksum(actual);
        Assertions.assertEquals(checksum, myHash);
    }

    @Test
    void whenBuyOrdersCountIsBigger() {
        TreeSet<Order> buyOrders = Stream.of(
                        new Order("10000", Side.BUY, 98, 25_500).setUniqueId(orderId++),
                        new Order("10003", Side.BUY, 99, 50_000).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<Order> sellOrders = Stream.of(
                        new Order("10005", Side.SELL, 105, 14_600).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        Mockito.when(exchangeMarket.getBuyOrders()).thenReturn(buyOrders);
        Mockito.when(exchangeMarket.getSellOrders()).thenReturn(sellOrders);

        String expected =
                "     50,000     99 |    105      14,600\n" +
                "     25,500     98 |                   \n";
        String actual = TOT.printMarket(exchangeMarket);
        log.info(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void whenNoSellOrders() {
        TreeSet<Order> buyOrders = Stream.of(
                        new Order("10000", Side.BUY, 98, 25_500).setUniqueId(orderId++),
                        new Order("10003", Side.BUY, 99, 50_000).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<Order> sellOrders = new TreeSet<>();
        Mockito.when(exchangeMarket.getBuyOrders()).thenReturn(buyOrders);
        Mockito.when(exchangeMarket.getSellOrders()).thenReturn(sellOrders);

        String expected =
                "     50,000     99 |                   \n" +
                "     25,500     98 |                   \n";
        String actual = TOT.printMarket(exchangeMarket);
        log.info(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void whenNoBuyOrder() {
        TreeSet<Order> buyOrders = new TreeSet<>();
        TreeSet<Order> sellOrders = Stream.of(
                        new Order("10005", Side.SELL, 105, 14_600).setUniqueId(orderId++))
                .collect(Collectors.toCollection(TreeSet::new));
        Mockito.when(exchangeMarket.getBuyOrders()).thenReturn(buyOrders);
        Mockito.when(exchangeMarket.getSellOrders()).thenReturn(sellOrders);

        String expected = "                   |    105      14,600\n";
        String actual = TOT.printMarket(exchangeMarket);
        log.info(actual);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name="[{0}] <=> {2} {4}@{3}")
    @CsvSource({
            "'          5     99', orderId, BUY, 99, 5, 0",
            "'         50     99', orderId, BUY, 99, 50, 0",
            "'        500     99', orderId, BUY, 99, 500, 0",
            "'      5,000     99', orderId, BUY, 99, 5000, 0",
            "'     50,000     99', orderId, BUY, 99, 50000, 0",
            "'    500,000     99', orderId, BUY, 99, 500000, 0",
            "'  5,000,000     99', orderId, BUY, 99, 5000000, 0",
            "' 50,000,000     99', orderId, BUY, 99, 50000000, 0",
            "'500,000,000     99', orderId, BUY, 99, 500000000, 0",

            "'    500,000    999', orderId, BUY, 999, 500000, 0",
            "'    500,000   9999', orderId, BUY, 9999, 500000, 0",
            "'    500,000  99999', orderId, BUY, 99999, 500000, 0",
            "'    500,000 999999', orderId, BUY, 999999, 500000, 0",
    })
    void formatBuyOrder(String expectedOutput, String orderId, Side side, int price, int volume) {
        Order order = new Order(orderId, side, price, volume);
        String actual = TOT.formatOrder(order);
        Assertions.assertEquals(expectedOutput, actual);
    }


    @ParameterizedTest(name="[{0}] <=> {2} {4}@{3}")
    @CsvSource({
            "'    99           5', orderId, SELL, 99, 5, 0",
            "'    99          50', orderId, SELL, 99, 50, 0",
            "'    99         500', orderId, SELL, 99, 500, 0",
            "'    99       5,000', orderId, SELL, 99, 5000, 0",
            "'    99      50,000', orderId, SELL, 99, 50000, 0",
            "'    99     500,000', orderId, SELL, 99, 500000, 0",
            "'    99   5,000,000', orderId, SELL, 99, 5000000, 0",
            "'    99  50,000,000', orderId, SELL, 99, 50000000, 0",
            "'    99 500,000,000', orderId, SELL, 99, 500000000, 0",

            "'   999     500,000', orderId, SELL, 999, 500000, 0",
            "'  9999     500,000', orderId, SELL, 9999, 500000, 0",
            "' 99999     500,000', orderId, SELL, 99999, 500000, 0",
            "'999999     500,000', orderId, SELL, 999999, 500000, 0",
    })
    void formatSellOrder(String expectedOutput, String orderId, Side side, int price, int volume, long uniqueId) {
        Order order = new Order(orderId, side, price, volume);
        String actual = TOT.formatOrder(order);
        Assertions.assertEquals(expectedOutput, actual);
    }

    private static String calcChecksum(String expected) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(expected.getBytes());
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        StringBuilder hashText = new StringBuilder(bigInt.toString(16));
        // zero pad to full 32 chars.
        while(hashText.length() < 32) {
            hashText.insert(0, "0");
        }
        return hashText.toString();
    }
}