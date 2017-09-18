package com.ubs.service;

import com.ubs.entity.Order;
import com.ubs.entity.Trade;
import com.ubs.manager.OrderBookManager;
import com.ubs.matcher.OrderBookMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by omib on 16/09/2017.
 */
@Service
public class OrderBookServiceImpl implements OrderBookService
{
    private static final Logger log = LoggerFactory.getLogger(OrderBookServiceImpl.class);
    private final OrderBookManager orderBookManager;
    private final OrderBookMatcher orderBookMatcher;

    @Autowired
    public OrderBookServiceImpl(OrderBookManager orderBookManager, OrderBookMatcher orderBookMatcher)
    {
        this.orderBookManager = orderBookManager;
        this.orderBookMatcher = orderBookMatcher;
    }

    @Override
    public void placeOrder(Order order)
    {
        orderBookManager.addOrderToOrderBook(order);
        try
        {
            orderBookMatcher.match(order,orderBookManager);
        } catch (Exception e)
        {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<Order> getAllOrders(Long userId)
    {
        List<Order> buySideBook = orderBookManager.getBuySideBookFor(userId);
        log.info("Buy side book {}", buySideBook);
        List<Order> sellSideBook = orderBookManager.getSellSideBookFor(userId);
        log.info("Sell side book {}", sellSideBook);
        buySideBook.addAll(sellSideBook);
        return buySideBook;
    }

    @Override
    public List<Trade> getAllTrades(Long userId)
    {
        return orderBookManager.getLedgerFor(userId);
    }
}
