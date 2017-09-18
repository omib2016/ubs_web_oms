package com.ubs.entity;

import java.math.BigDecimal;

/**
 * Created by omib on 16/09/2017.
 */
public class Trade
{
    private final Long tradeId;
    private final Long buyerId;
    private final Long sellerId;
    private final Long itemId;
    private final int quantity;
    private final BigDecimal price;
    private final long timeStampInMillis;

    public Trade(Long buyerId, Long sellerId, Long itemId, int quantity, BigDecimal price, long timeStampInMillis)
    {
        this.tradeId = IdGenerator.getNextId();
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.timeStampInMillis = timeStampInMillis;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public Long getTradeId() {
        return tradeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (quantity != trade.quantity) return false;
        if (timeStampInMillis != trade.timeStampInMillis) return false;
        if (!tradeId.equals(trade.tradeId)) return false;
        if (!buyerId.equals(trade.buyerId)) return false;
        if (!sellerId.equals(trade.sellerId)) return false;
        if (!itemId.equals(trade.itemId)) return false;
        return price.equals(trade.price);

    }

    @Override
    public int hashCode() {
        int result = tradeId.hashCode();
        result = 31 * result + buyerId.hashCode();
        result = 31 * result + sellerId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + quantity;
        result = 31 * result + price.hashCode();
        result = 31 * result + (int) (timeStampInMillis ^ (timeStampInMillis >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", timeStampInMillis=" + timeStampInMillis +
                '}';
    }
}
