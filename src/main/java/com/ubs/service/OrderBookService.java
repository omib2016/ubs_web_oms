package com.ubs.service;

import com.ubs.entity.Order;
import com.ubs.entity.Trade;

import java.util.List;

/**
 * Created by omib on 16/09/2017.
 */
public interface OrderBookService
{
    void placeOrder(Order order);
    List<Order> getAllOrders(Long userId);
    List<Trade> getAllTrades(Long userId);
}
