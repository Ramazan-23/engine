package com.bitvavo.service;

import com.bitvavo.serdes.OrderParser;
import com.bitvavo.service.id.IdGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OrderProvidingService {
    private final OrderParser orderParser;
    private final OrderListener orderListener;
    private final IdGenerator idGenerator;

    public OrderProvidingService(@NotNull final OrderParser orderParser,
                                 @NotNull final OrderListener orderListener,
                                 @NotNull final IdGenerator idGenerator) {
        this.orderParser = Objects.requireNonNull(orderParser);
        this.orderListener = Objects.requireNonNull(orderListener);
        this.idGenerator = Objects.requireNonNull(idGenerator);
    }

    public void run() {
        orderParser.readOrders().forEach(order -> {
            order.setUniqueId(idGenerator.getNext());
            orderListener.onOrder(order);
        });
    }

    public void stop() {
        orderListener.stop();
    }
}
