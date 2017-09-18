package com.ubs.entity;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * Created by omib on 16/09/2017.
 */
public class OrdersAtPrice
{
    private final List <Order> ordersAtPrice;

    public OrdersAtPrice()
    {
        this.ordersAtPrice = new ArrayList<>();
        Collections.sort(ordersAtPrice, comparing(Order::getTimeStampInMillis));
    }

    public void addOrder(Order order)
    {
        ordersAtPrice.add(order);
    }

    public void addAllOrders(List<Order> orders)
    {
        ordersAtPrice.addAll(orders);
    }

    public void removeOrder(Order order)
    {
        ordersAtPrice.remove(order);
    }

    public List<Order> getOrdersAtPrice() {
        return ordersAtPrice;
    }

    @Override
    public String toString()
    {
        return "OrdersAtPrice{" +
                "ordersAtPrice=" + ordersAtPrice +
                '}';
    }
}
