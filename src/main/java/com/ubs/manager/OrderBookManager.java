package com.ubs.manager;

import com.ubs.entity.Order;
import com.ubs.entity.Trade;
import com.ubs.orderbook.OrderBook;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by omib on 16/09/2017.
 */
public interface OrderBookManager
{
    /*
     * Get all bids and offer for an item.
     */
    OrderBook getOrderBookFor(Long itemId);
    /*
     * Add a bid/or offer to book.
     */
    void addOrderToOrderBook(Order order);
    /*
     * Get all orders for a user by userId.
     */
     List<Trade> getLedgerFor(Long userId);
    /*
     * Get all orders where user was a buyer(by userId)
     */
     List<Trade> getBuySideLedgerFor(Long userId);
    /*
     * Get all orders where user was a seller(by userId)
     */
     List<Trade> getSellSideLedgerFor(Long userId);
    /*
     * Get all bids for a user(by userId)
     */
     List<Order> getBuySideBookFor(Long userId);
    /*
     * Get all offers for a user(by userId)
     */
     List<Order> getSellSideBookFor(Long userId);
    /*
     * Get the current bid price for an item(by ItemId)
     */
     BigDecimal getBidPriceFor(Long itemId);
    /*
     * Get the current offer price for an item(by ItemId)
     */
     BigDecimal getOfferPriceFor(Long itemId);
    /*
     * Save an trade(post match for a user.
     */
     void saveTrade(Long userId, Trade trade);
}
