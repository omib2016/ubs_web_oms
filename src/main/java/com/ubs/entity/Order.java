package com.ubs.entity;

import java.math.BigDecimal;

/**
 * Created by omib on 16/09/2017.
 */
public class Order implements Cloneable
{
    private Long orderId;
    private Long itemId;
    private Long userId;
    private BigDecimal price;
    private Side side;
    private long timeStampInMillis;

    private int quantity;

    public Order(Long orderId,Long itemId, Long userId, int quantity, BigDecimal price, Side side)
    {
        this.orderId = orderId;
        this.itemId = itemId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.timeStampInMillis = System.currentTimeMillis();
    }

    public Order(Long itemId, Long userId, int quantity, BigDecimal price, Side side)
    {
        this.orderId = IdGenerator.getNextId();
        this.itemId = itemId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.timeStampInMillis = System.currentTimeMillis();
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public void setTimeStampInMillis(long timeStampInMillis) {
        this.timeStampInMillis = timeStampInMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (userId != order.userId) return false;
        if (quantity != order.quantity) return false;
        if (timeStampInMillis != order.timeStampInMillis) return false;
        if (!orderId.equals(order.orderId)) return false;
        if (!itemId.equals(order.itemId)) return false;
        if (!price.equals(order.price)) return false;
        return side == order.side;

    }

    @Override
    public int hashCode() {
        int result = orderId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + price.hashCode();
        result = 31 * result + side.hashCode();
        result = 31 * result + (int) (timeStampInMillis ^ (timeStampInMillis >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                ", userId=" + userId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                ", timeStampInMillis=" + timeStampInMillis +
                '}';
    }

    @Override
    public Order clone() throws CloneNotSupportedException
    {
        return (Order) super.clone();
    }

    public Long getOrderId() {
        return orderId;
    }
}
