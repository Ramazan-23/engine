package com.bitvavo.serdes;

import com.bitvavo.entity.Order;
import com.bitvavo.entity.Side;
import com.bitvavo.service.ExchangeMarket;

import java.text.DecimalFormat;
import java.util.Iterator;

public class ExchangePrintService {
    private static final DecimalFormat volumeFormat = new DecimalFormat("###,###,###");
    private static final DecimalFormat priceFormat = new DecimalFormat("######");
    private static final String EMPTY_ORDER_PLACEHOLDER = new String(new char[18]).replace('\0', ' ');


    public String printMarket(ExchangeMarket exchangeMarket) {
        StringBuilder sb = new StringBuilder();
        Iterator<Order> buyOrdersIterator = exchangeMarket.getBuyOrders().iterator();
        Iterator<Order> sellOrderIterator = exchangeMarket.getSellOrders().iterator();
        while (buyOrdersIterator.hasNext() || sellOrderIterator.hasNext()) {
            if (buyOrdersIterator.hasNext()) {
                sb.append(formatOrder(buyOrdersIterator.next()));
            } else {
                sb.append(EMPTY_ORDER_PLACEHOLDER);
            }

            sb.append(" | ");

            if (sellOrderIterator.hasNext()) {
                sb.append(formatOrder(sellOrderIterator.next()));
            } else {
                sb.append(EMPTY_ORDER_PLACEHOLDER);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String formatOrder(Order order) {
        return order.getSide() == Side.BUY
            ? String.format("%11s %6s", volumeFormat.format(order.getQuantity()), priceFormat.format(order.getPrice()))
            : String.format("%6s %11s", priceFormat.format(order.getPrice()), volumeFormat.format(order.getQuantity()));
    }
}
