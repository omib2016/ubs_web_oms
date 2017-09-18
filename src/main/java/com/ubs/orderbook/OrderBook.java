package com.ubs.orderbook;

import com.ubs.entity.Order;
import com.ubs.entity.OrdersAtPrice;
import com.ubs.entity.Side;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by omib on 16/09/2017.
 */
public interface OrderBook
{
    void addOrderToBook(Order order);
    TreeMap<BigDecimal,OrdersAtPrice> getPotentialMatchesFor(Order order);
    void removeOrderFromBook(Order order);
    void removeAllOrdersFromBook(List<Order> orders);
    TreeMap<BigDecimal,OrdersAtPrice> getBook(Side side);
    TreeMap<BigDecimal, OrdersAtPrice> getContraBook(Side side);
    void addAllOrdersToBook(List<Order> orders);

}
