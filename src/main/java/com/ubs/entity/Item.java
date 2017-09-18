package com.ubs.entity;

/**
 * Created by omib on 16/09/2017.
 */
public enum  Item
{
    A(1L),
    B(2L),
    C(3L);

    private final long itemId;

    Item(long itemId)
    {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }
}
