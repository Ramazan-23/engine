package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import org.jetbrains.annotations.NotNull;
import uk.elementarysoftware.quickcsv.api.CSVParser;
import uk.elementarysoftware.quickcsv.api.CSVParserBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderQuickCsvParser implements OrderParser {
    private final Path path;

    public OrderQuickCsvParser(@NotNull final Path path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public Stream<Order> readOrders() {
        try {
            InputStream source = new FileInputStream(path.toFile());
            CSVParser<Order> parser = CSVParserBuilder.aParser(Order::new).build();
            return parser.parse(source).sequential();
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
