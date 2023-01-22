package com.bitvavo.service;

import com.bitvavo.entity.Order;

public interface OrderListener {
    void onOrder(Order order);
    void stop();
}
