package com.ubs.matcher;

import com.ubs.entity.Order;
import com.ubs.entity.Trade;
import com.ubs.entity.OrdersAtPrice;
import com.ubs.entity.Side;
import com.ubs.manager.OrderBookManager;
import com.ubs.orderbook.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 *
 * Stateless matcher
 *
 * Created by omib on 16/09/2017.
 */

@Service
public class OrderBookMatcherImpl implements OrderBookMatcher
{
    private static final Logger log = LoggerFactory.getLogger(OrderBookMatcherImpl.class);

    @Override
    public List<Trade> match(Order incomingOrder, OrderBookManager orderbookManager) throws CloneNotSupportedException
    {
        log.info("Attempting match for incoming order {}", incomingOrder);
        OrderBook orderBookForItem = orderbookManager.getOrderBookFor(incomingOrder.getItemId());
        SortedMap<BigDecimal, OrdersAtPrice> potentialMatches = orderBookForItem.getPotentialMatchesFor(incomingOrder);

        //1. Iterate through matches to fill up qty - first based in timestamp , then on qty.
        int originalMatchQty = incomingOrder.getQuantity();
        int requiredMatchqty = incomingOrder.getQuantity();
        int availableQty=0;

        List<Trade> results = new ArrayList<Trade>();
        List<Order> matchedOrders = new ArrayList<>();
        List<Order> partiallyMatchedOrders = new ArrayList<>();
        for (OrdersAtPrice ordersAtPrice : potentialMatches.values())
        {
            for (Order contraOrder : ordersAtPrice.getOrdersAtPrice())
            {
               availableQty = contraOrder.getQuantity();
                if(availableQty <= requiredMatchqty)
                {
                    requiredMatchqty -=availableQty;
                    matchedOrders.add(contraOrder);
                    matchedOrders.add(incomingOrder);
                    createTrade(incomingOrder, availableQty, results, contraOrder, orderbookManager);
                }
                else
                {
                    if (requiredMatchqty == 0)
                    {
                        break;
                    }
                    else
                    {
                        //2. partial match & update remaining qty on contra books.
                        int remainingQty = availableQty - requiredMatchqty;
                        if (remainingQty > 0)
                        {
                            Order newOrder = contraOrder.clone();
                            newOrder.setQuantity(remainingQty);
                            partiallyMatchedOrders.add(newOrder);
                        }
                        matchedOrders.add(contraOrder);
                        matchedOrders.add(incomingOrder);
                        createTrade(incomingOrder, requiredMatchqty, results, contraOrder, orderbookManager);
                        requiredMatchqty=0;
                    }

                }
            }

            //3. Remove all matched quotes from contra book.
            orderBookForItem.removeAllOrdersFromBook(matchedOrders);
            //4. Update remaining qty on contra books.
            orderBookForItem.addAllOrdersToBook(partiallyMatchedOrders);
        }

        //5. Add any remaining unmatched qty back to book.
        if (requiredMatchqty > 0 && (requiredMatchqty != originalMatchQty))
        {
            Order newOrder = incomingOrder.clone();
            newOrder.setQuantity(requiredMatchqty);
            orderbookManager.addOrderToOrderBook(newOrder);
        }

        log.info("Match attempt completed");
        return results;
    }

    private void createTrade(Order incomingOrder, int requiredMatchqty, List<Trade> results, Order order, OrderBookManager orderBookManager)
    {
        long buyerId = Side.BUY.equals(incomingOrder.getSide()) ? incomingOrder.getUserId() : order.getUserId();
        long sellerId = Side.SELL.equals(incomingOrder.getSide()) ? incomingOrder.getUserId() : order.getUserId();
        BigDecimal matchedPrice = BigDecimal.valueOf(Math.min(incomingOrder.getPrice().doubleValue(), order.getPrice().doubleValue()));
        Trade trade = new Trade(buyerId,sellerId, order.getItemId(),requiredMatchqty,matchedPrice,System.currentTimeMillis());
        orderBookManager.saveTrade(buyerId, trade);
        orderBookManager.saveTrade(sellerId, trade);
        results.add(trade);
    }
}
