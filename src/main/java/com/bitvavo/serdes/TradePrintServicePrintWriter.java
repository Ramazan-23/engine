package com.bitvavo.serdes;

import com.bitvavo.entity.Trade;
import com.bitvavo.service.TradeListener;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TradePrintServicePrintWriter implements TradeListener, Closeable {
    // value is taken empirically
    private static final int OUTPUT_BUFFER_SIZE = 8192 * 8;
    private final PrintWriter writer = new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(System.out), OUTPUT_BUFFER_SIZE));

    @Override
    public void onTrade(Trade trade) {
        writer.println(trade.toString());
    }

    @Override
    public void close() {
        writer.flush();
    }
}
