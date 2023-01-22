package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderSimpleParser implements OrderParser {

    private final Path path;

    public OrderSimpleParser(@NotNull final Path path) {
        this.path = Objects.requireNonNull(path);
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
        try {
            return Files.lines(path).map(this::parseOrder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
