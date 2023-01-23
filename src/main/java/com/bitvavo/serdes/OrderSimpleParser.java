package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderSimpleParser implements OrderParser {

    private final InputStream inputStream;

    public OrderSimpleParser(@NotNull final InputStream inputStream) {
        this.inputStream = Objects.requireNonNull(inputStream);
    }


    public Order parseOrder(String source) {
        String[] parts = source.split(",");
        return new Order(parts[0],
                Side.getByValue(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]));
    }

    @Override
    public Stream<Order> readOrders() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines().map(this::parseOrder);
    }
}
