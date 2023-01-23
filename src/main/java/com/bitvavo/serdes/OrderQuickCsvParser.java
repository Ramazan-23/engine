package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import org.jetbrains.annotations.NotNull;
import uk.elementarysoftware.quickcsv.api.CSVParser;
import uk.elementarysoftware.quickcsv.api.CSVParserBuilder;

import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderQuickCsvParser implements OrderParser {
    private final InputStream inputStream;

    public OrderQuickCsvParser(@NotNull final InputStream inputStream) {
        this.inputStream = Objects.requireNonNull(inputStream);
    }

    @Override
    public Stream<Order> readOrders() {
        CSVParser<Order> parser = CSVParserBuilder.aParser(Order::new).build();
        return parser.parse(inputStream).sequential();
    }
}
