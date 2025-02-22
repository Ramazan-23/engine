package com.bitvavo;

import com.bitvavo.serdes.ExchangeFormatterService;
import com.bitvavo.serdes.OrderParser;
import com.bitvavo.serdes.OrderQuickCsvParser;
import com.bitvavo.serdes.TradePrintServicePrintWriter;
import com.bitvavo.service.ExchangeMarket;
import com.bitvavo.service.OrderProvidingService;
import com.bitvavo.service.TradeListener;
import com.bitvavo.service.id.IdGenerator;
import com.bitvavo.service.id.TimeBasedIdGenerator;

public class MatchingEngineApp {

    public static void main(String[] args) {

        TradeListener tradeListener = new TradePrintServicePrintWriter();
        ExchangeMarket exchangeMarket = new ExchangeMarket(tradeListener);
        ExchangeFormatterService exchangeFormatterService = new ExchangeFormatterService();

        IdGenerator idGenerator = new TimeBasedIdGenerator();
        OrderParser orderParser = new OrderQuickCsvParser(System.in);
        OrderProvidingService orderProvidingService =
                new OrderProvidingService(orderParser, exchangeMarket, idGenerator);
        orderProvidingService.run();
        orderProvidingService.stop();

        System.out.print(exchangeFormatterService.formatMarket(exchangeMarket));
    }
}
