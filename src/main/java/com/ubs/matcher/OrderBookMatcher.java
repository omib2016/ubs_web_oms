package com.ubs.matcher;

import com.ubs.entity.Trade;
import com.ubs.entity.Order;
import com.ubs.manager.OrderBookManager;

import java.util.List;

public interface OrderBookMatcher
{
    public List<Trade> match(Order incomingOrder, OrderBookManager orderbookManager) throws CloneNotSupportedException;
}
