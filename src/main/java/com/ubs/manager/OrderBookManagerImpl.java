package com.ubs.manager;

import com.ubs.annotations.ThreadSafe;
import com.ubs.entity.Order;
import com.ubs.entity.Side;
import com.ubs.entity.Trade;
import com.ubs.orderbook.OrderBook;
import com.ubs.orderbook.OrderBookImpl;
import com.ubs.entity.OrdersAtPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by omib on 16/09/2017.
 */
@ThreadSafe
@Service
public class OrderBookManagerImpl implements OrderBookManager
{
    private static final Logger log = LoggerFactory.getLogger(OrderBookManagerImpl.class);
    //order book partitioned by itemId. Every order goes into an OrderBook instance post initial matching attempt.
    private final ConcurrentMap<Long,OrderBook> allOrderBooks = new ConcurrentHashMap<>();
    //completed orders partitioned by userID. All matched Orders are maintained here.
    private final ConcurrentMap<Long,List<Trade>> allTrades = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    @Override
    public OrderBook getOrderBookFor(Long itemId)
    {
        return allOrderBooks.get(itemId);
    }

    @Override
    public void addOrderToOrderBook(Order order)
    {
        if(order == null)
        {
            throw new IllegalArgumentException("Attempt to add null order to book.Invalid operation!");
        }

        if (order.getItemId() == null)
        {
            throw new IllegalArgumentException("Invalid order.Item id is not set for quoteId:"+ order.getOrderId());
        }

        OrderBook orderBook = new OrderBookImpl();
        orderBook.addOrderToBook(order);
        try
        {
            writeLock.tryLock();
            log.info("Adding order {} to book", order);
            orderBook = allOrderBooks.putIfAbsent(order.getItemId(), orderBook);
            if (orderBook != null)
            {
                orderBook.addOrderToBook(order);
                allOrderBooks.replace(order.getItemId(),orderBook);
            }
        }catch (Exception e)
        {
            log.error(e.getMessage());

        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Trade> getLedgerFor(Long buyerId)
    {
        List<Trade> results=new ArrayList<>();
        try
        {
            readLock.lock();
            results.addAll(allTrades.get(buyerId));

        } catch (Exception e)
        {
            log.error(e.getMessage());

        }finally
        {
            readLock.unlock();
        }
        return results;
    }

    @Override
    public List<Trade> getBuySideLedgerFor(Long userId)
    {
        List<Trade> trades = allTrades.get(userId);
        return trades.parallelStream().filter(o -> o.getBuyerId().equals(userId)).collect(toList());
    }

    @Override
    public List<Trade> getSellSideLedgerFor(Long userId)
    {
        List<Trade> trades = allTrades.get(userId);
        return trades.parallelStream().filter(o -> o.getSellerId().equals(userId)).collect(toList());
    }

    @Override
    public List<Order> getBuySideBookFor(Long userId)
    {
        Collection<OrderBook> allBooks = allOrderBooks.values();
        Stream<OrdersAtPrice> allOrdersAtPrice = allBooks.stream().map(o -> o.getBook(Side.BUY)).flatMap(c -> c.values().stream());
        Stream<Order> allOrders = allOrdersAtPrice.flatMap(q -> q.getOrdersAtPrice().stream());
        return allOrders.filter(q->q.getUserId().equals(userId)).collect(toList());
    }

    @Override
    public List<Order> getSellSideBookFor(Long userId)
    {
        Collection<OrderBook> allBooks = allOrderBooks.values();
        Stream<OrdersAtPrice> allQuotesAtPrice = allBooks.stream().map(o -> o.getBook(Side.SELL)).flatMap(c -> c.values().stream());
        Stream<Order> allOrders = allQuotesAtPrice.flatMap(q -> q.getOrdersAtPrice().stream());
        return allOrders.filter(q->q.getUserId().equals(userId)).collect(toList());
    }

    @Override
    public BigDecimal getBidPriceFor(Long itemId)
    {
        OrderBook orderBook = allOrderBooks.get(itemId);
        SortedMap<BigDecimal, OrdersAtPrice> book = orderBook.getBook(Side.BUY);
        return book.firstKey();
    }

    @Override
    public BigDecimal getOfferPriceFor(Long itemId)
    {
        OrderBook orderBook = allOrderBooks.get(itemId);
        SortedMap<BigDecimal, OrdersAtPrice> book = orderBook.getBook(Side.SELL);
        return book.firstKey();
    }


    @Override
    public void saveTrade(Long userId, Trade trade)
    {
        if (userId == null || trade == null)
        {
            throw new IllegalArgumentException("Invalid or null userId/Trade while trying to save trade!");
        }

        List<Trade> trades = new ArrayList<Trade>();
        trades.add(trade);

        try
        {
            writeLock.tryLock();
            List<Trade> currentTrades = allTrades.putIfAbsent(userId, trades);
            if (currentTrades != null)
            {
                currentTrades.add(trade);
                allTrades.replace(userId, currentTrades);
            }

        }catch (Exception e)
        {
            System.out.print(e);

        }finally
        {
            writeLock.unlock();
        }

    }


}
