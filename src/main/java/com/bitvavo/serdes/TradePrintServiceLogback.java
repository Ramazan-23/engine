package com.bitvavo.serdes;

import com.bitvavo.entity.Trade;
import com.bitvavo.service.TradeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradePrintServiceLogback implements TradeListener {
    private static final Logger log = LoggerFactory.getLogger(TradePrintServiceLogback.class);

    @Override
    public void onTrade(Trade trade) {
        log.info(trade.toString());
    }

    @Override
    public void close() {}
}
