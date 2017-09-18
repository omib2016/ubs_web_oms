package com.ubs.entity;

import java.math.BigDecimal;

/**
 * Created by omib on 17/09/2017.
 */
public class OrderEntry
{
    private String username;
    private Item item;
    private int quantity;
    private BigDecimal price;
    private Side side;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Item getItem() {
        return item;
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

    @Override
    public String toString() {
        return "OrderEntry{" +
                "username='" + username + '\'' +
                ", item=" + item +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                '}';
    }
}
