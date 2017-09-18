package com.ubs.entity;

import com.ubs.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by omib on 17/09/2017.
 */
@ThreadSafe
public class IdGenerator
{
    private static final AtomicLong id = new AtomicLong(10000001);
    private IdGenerator()
    {

    }

    public static final long getNextId()
    {
        return id.incrementAndGet();
    }
}
