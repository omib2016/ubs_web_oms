package com.ubs.orderbook;

import com.ubs.annotations.NotThreadSafe;
import com.ubs.entity.Order;
import com.ubs.entity.PriceComparator;
import com.ubs.entity.OrdersAtPrice;
import com.ubs.entity.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * No operations are allowed directly on OrderBook without following appropriate thread safety protocols as it will
 * lead to incorrect state being maintained.
 *
 * Please use OrderBookManager for ANY operations on OrderBook as it ensures thread safety and will ensure
 * correct OrderBook state being maintained.
 */
@NotThreadSafe
public class OrderBookImpl implements OrderBook
{

    private static final Logger log = LoggerFactory.getLogger(OrderBookImpl.class);
    private final TreeMap<BigDecimal,OrdersAtPrice> buySide = new TreeMap<>(PriceComparator.DESC);
    private final TreeMap<BigDecimal,OrdersAtPrice> sellSide = new TreeMap<>(PriceComparator.ASC);


    @Override
    public void addOrderToBook(Order order)
    {
        BigDecimal price = order.getPrice();
        if (price == null)
        {
            throw new IllegalArgumentException("Invalid or null price for order "+ order.getOrderId());
        }

        OrdersAtPrice ordersAtPrice = new OrdersAtPrice();
        ordersAtPrice.addOrder(order);

        SortedMap<BigDecimal,OrdersAtPrice> ordersOnBook = getBook(order.getSide());
        OrdersAtPrice currentOrder = ordersOnBook.putIfAbsent(price, ordersAtPrice);

        if (currentOrder != null)
        {
            currentOrder.addOrder(order);
            ordersOnBook.replace(price, currentOrder);
        }

    }

    @Override
    public TreeMap<BigDecimal, OrdersAtPrice> getPotentialMatchesFor(Order order)
    {
        TreeMap<BigDecimal, OrdersAtPrice> contraOrdersForSameOrBetterPrice = getContraOrdersForSameOrBetterPrice(order);
        return contraOrdersForSameOrBetterPrice;
    }

    private TreeMap<BigDecimal, OrdersAtPrice> getContraOrdersForSameOrBetterPrice(Order order)
    {
        TreeMap<BigDecimal, OrdersAtPrice> contraBook = getContraBook(order.getSide());

        if (Side.BUY.equals(order.getSide()))
        {
            TreeMap<BigDecimal,OrdersAtPrice> resultMap = new TreeMap<>(PriceComparator.ASC);
            SortedMap<BigDecimal, OrdersAtPrice> ordersWithBetterPrice = contraBook.headMap(order.getPrice(),true);

            //TODO: remove orders resulting in potential wash trades.

            //now remove any offer quotes where offer qty < bid qty
            for (Map.Entry<BigDecimal, OrdersAtPrice> entry : ordersWithBetterPrice.entrySet())
            {
                resultMap.put(entry.getKey(),entry.getValue());
            }
            log.info("Potential match size {},Potential matches {}",resultMap.size(),resultMap.values());
            return resultMap;
        }
        else
        {
            TreeMap<BigDecimal,OrdersAtPrice> resultMap = new TreeMap<>(PriceComparator.DESC);
            SortedMap<BigDecimal, OrdersAtPrice> orderWithBetterOrSamePrice = contraBook.headMap(order.getPrice(),true);

            //TODO: remove orders resulting in potential wash trades.

            //now remove any bid quotes where bid qty > bid qty
            for (Map.Entry<BigDecimal, OrdersAtPrice> entry : orderWithBetterOrSamePrice.entrySet())
            {
                resultMap.put(entry.getKey(),entry.getValue());
            }
            log.info("Potential match size {},Potential matches {}",resultMap.size(),resultMap.values());
            return resultMap;
        }

    }

    @Override
    public void removeOrderFromBook(Order order)
    {
        boolean removeKey=false;
        SortedMap<BigDecimal, OrdersAtPrice> ordersOnBook = getBook(order.getSide());
        BigDecimal price = order.getPrice();

        OrdersAtPrice ordersAtPrice = ordersOnBook.get(price);
        if(ordersAtPrice != null)
        {
            ordersAtPrice.removeOrder(order);
            if (ordersAtPrice.getOrdersAtPrice().size() == 0)
            {
                removeKey = true;
            }
        }
        if (removeKey)
        {
            ordersOnBook.remove(price);
        }
    }

    @Override
    public void removeAllOrdersFromBook(List<Order> orders)
    {
        for (Order order : orders)
        {
            removeOrderFromBook(order);
        }

    }

    @Override
    public TreeMap<BigDecimal, OrdersAtPrice> getBook(Side side)
    {
        return Side.BUY.equals(side) ? buySide : sellSide;
    }

    @Override
    public TreeMap<BigDecimal, OrdersAtPrice> getContraBook(Side side)
    {
        return Side.BUY.equals(side) ? sellSide : buySide;
    }

    @Override
    public void addAllOrdersToBook(List<Order> orders)
    {
        for (Order order : orders)
        {
            addOrderToBook(order);
        }

    }
}
