package com.bitvavo.service;

import com.bitvavo.entity.Trade;

import java.io.Closeable;

public interface TradeListener extends Closeable {
    void onTrade(Trade trade);
}
