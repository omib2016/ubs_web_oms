package com.ubs.matcher;

import com.ubs.entity.Order;
import com.ubs.entity.Trade;
import com.ubs.entity.OrdersAtPrice;
import com.ubs.entity.Side;
import com.ubs.orderbook.OrderBook;
import com.ubs.manager.OrderBookManager;
import com.ubs.manager.OrderBookManagerImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by omib on 20/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderBookManagerTest
{
    @Test
    public void testForEmptyBookAndThenAddBidOrOfferToBook()
    {
        Order mockOrder = Mockito.mock(Order.class);
        Mockito.when(mockOrder.getSide()).thenReturn(Side.BUY);
        Mockito.when(mockOrder.getItemId()).thenReturn(123L);
        Mockito.when(mockOrder.getPrice()).thenReturn(BigDecimal.ONE);

        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        OrderBook orderBook = orderBookManager.getOrderBookFor(123L);
        Assert.assertNull(orderBook);

        orderBookManager.addOrderToOrderBook(mockOrder);
        OrderBook orderBookWithOrder = orderBookManager.getOrderBookFor(123L);
        Assert.assertNotNull(orderBookWithOrder);
        Assert.assertEquals(1, orderBookWithOrder.getBook(Side.BUY).size());

        SortedMap<BigDecimal, OrdersAtPrice> book = orderBookWithOrder.getBook(Side.BUY);
        for (Map.Entry<BigDecimal, OrdersAtPrice> entry : book.entrySet())
        {
            Assert.assertEquals(BigDecimal.ONE,entry.getKey());
            OrdersAtPrice value = entry.getValue();
            Order order = value.getOrdersAtPrice().get(0);
            Assert.assertEquals(mockOrder, order);
        }

    }

    @Test
    public void testMatcherForMatchAtSameQtyAndSamePriceAndNoQtyLeftOnBook() throws CloneNotSupportedException
    {
        //Arrange
        Long itemId=234L;
        Long buyerId=226677L;
        Long sellerId=226678L;

        Order incomingOrder = new Order(123L,itemId,buyerId,10,BigDecimal.TEN,Side.BUY);
        Order order2 = new Order(124L,itemId,sellerId,10,BigDecimal.TEN,Side.SELL);

        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order2);

        //Act
        OrderBookMatcher orderBookMatcher = new OrderBookMatcherImpl();
        List<Trade> match = orderBookMatcher.match(incomingOrder, orderBookManager);
        Assert.assertEquals(1, match.size());

        //Assert
        OrderBook orderBookForItem = orderBookManager.getOrderBookFor(itemId);
        Assert.assertNotNull(orderBookForItem);

        //1. Assert no bids remaining on buy book post match
        SortedMap<BigDecimal, OrdersAtPrice> buyBook = orderBookForItem.getBook(Side.BUY);
        Assert.assertNotNull(buyBook);
        Assert.assertEquals(0, buyBook.size());

        //2. Assert no offers remaining on sell book post match
        SortedMap<BigDecimal, OrdersAtPrice> sellBook = orderBookForItem.getBook(Side.SELL);
        Assert.assertNotNull(sellBook);
        Assert.assertEquals(0, sellBook.size());

        //3. Assert orders post match for buyer
        List<Trade> buySideLedgerForBuyer = orderBookManager.getBuySideLedgerFor(buyerId);
        List<Trade> sellSideLedgerForBuyer = orderBookManager.getSellSideLedgerFor(buyerId);
        Assert.assertNotNull(buySideLedgerForBuyer);
        Assert.assertEquals(0, sellSideLedgerForBuyer.size());
        Assert.assertEquals(1, buySideLedgerForBuyer.size());

        // Assert order price/qty
        Trade buyTrade = buySideLedgerForBuyer.get(0);
        Assert.assertEquals(buyerId, buyTrade.getBuyerId());
        Assert.assertEquals(sellerId, buyTrade.getSellerId());
        Assert.assertEquals(itemId, buyTrade.getItemId());
        Assert.assertEquals(BigDecimal.TEN.doubleValue(), buyTrade.getPrice().doubleValue());
        Assert.assertEquals(10, buyTrade.getQuantity());

        //4. Assert orders post match for seller
        List<Trade> buySideLedgerForSeller = orderBookManager.getBuySideLedgerFor(sellerId);
        List<Trade> sellSideLedgerForSeller = orderBookManager.getSellSideLedgerFor(sellerId);
        Assert.assertNotNull(sellSideLedgerForSeller);
        Assert.assertEquals(0,buySideLedgerForSeller.size());
        Assert.assertEquals(1, sellSideLedgerForSeller.size());

        // Assert order price/qty
        Trade sellTrade = sellSideLedgerForSeller.get(0);
        Assert.assertEquals(buyerId, sellTrade.getBuyerId());
        Assert.assertEquals(sellerId, sellTrade.getSellerId());
        Assert.assertEquals(itemId, sellTrade.getItemId());
        Assert.assertEquals(BigDecimal.TEN.doubleValue(), sellTrade.getPrice().doubleValue());
        Assert.assertEquals(10, sellTrade.getQuantity());

    }


    @Test
    public void testMatcherForMatchAtBetterPriceAndRemainingQtyGoesBackToBook() throws CloneNotSupportedException
    {
        //Arrange
        Long itemId=234L;
        Long buyerId=226677L;
        Long sellerId=226678L;

        Order order2 = new Order(123L,itemId,buyerId,10,new BigDecimal("25"),Side.BUY);
        Order incomingOrder = new Order(125L,itemId,sellerId,22,new BigDecimal("24"),Side.SELL);

        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order2);

        //Act
        OrderBookMatcher orderBookMatcher = new OrderBookMatcherImpl();
        List<Trade> match = orderBookMatcher.match(incomingOrder, orderBookManager);
        Assert.assertEquals(1, match.size());

        //Assert
        OrderBook orderBookForItem = orderBookManager.getOrderBookFor(itemId);
        Assert.assertNotNull(orderBookForItem);

        //1. Assert no bids remaining on buy book post match
        SortedMap<BigDecimal, OrdersAtPrice> buyBook = orderBookForItem.getBook(Side.BUY);
        Assert.assertNotNull(buyBook);
        Assert.assertEquals(0, buyBook.size());

        //2. Assert 1 offers remaining on sell book post match with remaining qty of 12
        SortedMap<BigDecimal, OrdersAtPrice> sellBook = orderBookForItem.getBook(Side.SELL);
        Assert.assertNotNull(sellBook);
        Assert.assertEquals(1, sellBook.size());
        Assert.assertEquals(new BigDecimal("24"),sellBook.firstKey());
        for (OrdersAtPrice ordersAtPrice : sellBook.values())
        {
            List<Order> orderList = ordersAtPrice.getOrdersAtPrice();
            Assert.assertEquals(12, orderList.get(0).getQuantity());
        }

        //3. Assert orders post match for buyer
        List<Trade> buySideLedgerForBuyer = orderBookManager.getBuySideLedgerFor(buyerId);
        List<Trade> sellSideLedgerForBuyer = orderBookManager.getSellSideLedgerFor(buyerId);
        Assert.assertNotNull(buySideLedgerForBuyer);
        Assert.assertEquals(0, sellSideLedgerForBuyer.size());
        Assert.assertEquals(1, buySideLedgerForBuyer.size());

        // Assert order price/qty
        Trade buyTrade = buySideLedgerForBuyer.get(0);
        Assert.assertEquals(buyerId, buyTrade.getBuyerId());
        Assert.assertEquals(sellerId, buyTrade.getSellerId());
        Assert.assertEquals(itemId, buyTrade.getItemId());
        Assert.assertEquals(new BigDecimal("24").doubleValue(), buyTrade.getPrice().doubleValue());
        Assert.assertEquals(10, buyTrade.getQuantity());

        //4. Assert orders post match for seller
        List<Trade> buySideLedgerForSeller = orderBookManager.getBuySideLedgerFor(sellerId);
        List<Trade> sellSideLedgerForSeller = orderBookManager.getSellSideLedgerFor(sellerId);
        Assert.assertNotNull(sellSideLedgerForSeller);
        Assert.assertEquals(0,buySideLedgerForSeller.size());
        Assert.assertEquals(1, sellSideLedgerForSeller.size());

        // Assert order price/qty
        Trade sellTrade = sellSideLedgerForSeller.get(0);
        Assert.assertEquals(buyerId, sellTrade.getBuyerId());
        Assert.assertEquals(sellerId, sellTrade.getSellerId());
        Assert.assertEquals(itemId, sellTrade.getItemId());
        Assert.assertEquals(new BigDecimal("24").doubleValue(), sellTrade.getPrice().doubleValue());
        Assert.assertEquals(10, sellTrade.getQuantity());

    }

    @Test
    public void testMatcherForNoMatchWhenBidLessThanOfferPrice() throws CloneNotSupportedException
    {
        //Arrange
        Long itemId=234L;
        Long buyerId=226677L;
        Long sellerId=226678L;

        Order order2 = new Order(123L,itemId,buyerId,10,new BigDecimal("24"),Side.BUY);
        Order incomingOrder = new Order(124L,itemId,sellerId,10,new BigDecimal("25"),Side.SELL);

        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order2);

        //Act
        OrderBookMatcher orderBookMatcher = new OrderBookMatcherImpl();
        List<Trade> match = orderBookMatcher.match(incomingOrder, orderBookManager);
        Assert.assertEquals(0, match.size());

        //Assert
        OrderBook orderBookForItem = orderBookManager.getOrderBookFor(itemId);
        Assert.assertNotNull(orderBookForItem);

        //1. Assert 1 bid remaining on buy book post match
        SortedMap<BigDecimal, OrdersAtPrice> buyBook = orderBookForItem.getBook(Side.BUY);
        Assert.assertNotNull(buyBook);
        Assert.assertEquals(1, buyBook.size());
        Assert.assertEquals(new BigDecimal("24"),buyBook.firstKey());
        for (OrdersAtPrice ordersAtPrice : buyBook.values())
        {
            List<Order> orderList = ordersAtPrice.getOrdersAtPrice();
            Assert.assertEquals(10, orderList.get(0).getQuantity());
        }


        //2. Assert 1 offer remaining on sell book post match
        SortedMap<BigDecimal, OrdersAtPrice> sellBook = orderBookForItem.getBook(Side.SELL);
        Assert.assertNotNull(sellBook);
        Assert.assertEquals(1, sellBook.size());
        Assert.assertEquals(new BigDecimal("25"),sellBook.firstKey());
        for (OrdersAtPrice ordersAtPrice : sellBook.values())
        {
            List<Order> orderList = ordersAtPrice.getOrdersAtPrice();
            Assert.assertEquals(10, orderList.get(0).getQuantity());
        }

    }

    @Test
    public void testMatcherForNoMatchWhenOfferQtyLessThanBidQty() throws CloneNotSupportedException
    {
        //Arrange
        Long itemId=234L;
        Long buyerId=226677L;
        Long sellerId=226678L;

        Order order2 = new Order(123L,itemId,buyerId,10,new BigDecimal("25"),Side.BUY);
        Order incomingOrder = new Order(124L,itemId,sellerId,8,new BigDecimal("25"),Side.SELL);

        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order2);

        //Act
        OrderBookMatcher orderBookMatcher = new OrderBookMatcherImpl();
        List<Trade> match = orderBookMatcher.match(incomingOrder, orderBookManager);
        Assert.assertEquals(0, match.size());

        //Assert
        OrderBook orderBookForItem = orderBookManager.getOrderBookFor(itemId);
        Assert.assertNotNull(orderBookForItem);

        //1. Assert 1 bid remaining on buy book post match
        SortedMap<BigDecimal, OrdersAtPrice> buyBook = orderBookForItem.getBook(Side.BUY);
        Assert.assertNotNull(buyBook);
        Assert.assertEquals(1, buyBook.size());
        Assert.assertEquals(new BigDecimal("25"),buyBook.firstKey());
        for (OrdersAtPrice ordersAtPrice : buyBook.values())
        {
            List<Order> orderList = ordersAtPrice.getOrdersAtPrice();
            Assert.assertEquals(10, orderList.get(0).getQuantity());
        }


        //2. Assert 1 offer remaining on sell book post match
        SortedMap<BigDecimal, OrdersAtPrice> sellBook = orderBookForItem.getBook(Side.SELL);
        Assert.assertNotNull(sellBook);
        Assert.assertEquals(1, sellBook.size());
        Assert.assertEquals(new BigDecimal("25"),sellBook.firstKey());
        for (OrdersAtPrice ordersAtPrice : sellBook.values())
        {
            List<Order> orderList = ordersAtPrice.getOrdersAtPrice();
            Assert.assertEquals(8, orderList.get(0).getQuantity());
        }
    }

    @Test
    public void testGetCurrentBidAndOfferPriceForAnItem() throws CloneNotSupportedException {
        //Arrange
        Long itemId = 234L;
        Long buyerId = 226677L;
        Long sellerId = 226678L;

        Order order1 = new Order(123L, itemId, buyerId, 10, new BigDecimal("21"), Side.BUY);
        Order order2 = new Order(123L, itemId, buyerId, 10, new BigDecimal("23"), Side.BUY);
        Order order3 = new Order(123L, itemId, buyerId, 10, new BigDecimal("22"), Side.BUY);

        Order order4 = new Order(123L, itemId, sellerId, 10, new BigDecimal("31"), Side.SELL);
        Order order5 = new Order(123L, itemId, sellerId, 10, new BigDecimal("30"), Side.SELL);
        Order order6 = new Order(123L, itemId, sellerId, 10, new BigDecimal("32"), Side.SELL);


        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order1);
        orderBookManager.addOrderToOrderBook(order2);
        orderBookManager.addOrderToOrderBook(order3);
        orderBookManager.addOrderToOrderBook(order4);
        orderBookManager.addOrderToOrderBook(order5);
        orderBookManager.addOrderToOrderBook(order6);

        //Act
        BigDecimal offerPrice = orderBookManager.getOfferPriceFor(itemId);
        BigDecimal bidPrice = orderBookManager.getBidPriceFor(itemId);


        //Assert
        Assert.assertEquals(new BigDecimal("30"), offerPrice);
        Assert.assertEquals(new BigDecimal("23"), bidPrice);
    }

    @Test
    public void testGetCurrentBidAndOfferPriceForAUser() throws CloneNotSupportedException
    {
        //Arrange
        Long itemId = 234L;
        Long buyerId = 226677L;
        Long sellerId = 226678L;

        Order order1 = new Order(123L, itemId, buyerId, 10, new BigDecimal("21"), Side.BUY);
        Order order2 = new Order(123L, itemId, buyerId, 10, new BigDecimal("23"), Side.BUY);
        Order order3 = new Order(123L, itemId, buyerId, 10, new BigDecimal("22"), Side.BUY);

        Order order4 = new Order(123L, itemId, sellerId, 10, new BigDecimal("31"), Side.SELL);
        Order order5 = new Order(123L, itemId, sellerId, 10, new BigDecimal("30"), Side.SELL);
        Order order6 = new Order(123L, itemId, sellerId, 10, new BigDecimal("32"), Side.SELL);


        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrderToOrderBook(order1);
        orderBookManager.addOrderToOrderBook(order2);
        orderBookManager.addOrderToOrderBook(order3);
        orderBookManager.addOrderToOrderBook(order4);
        orderBookManager.addOrderToOrderBook(order5);
        orderBookManager.addOrderToOrderBook(order6);

        //Act
        List<Order> buySideBook = orderBookManager.getBuySideBookFor(buyerId);
        Assert.assertEquals(3, buySideBook.size());

        List<Order> sellSideBook = orderBookManager.getSellSideBookFor(sellerId);
        Assert.assertEquals(3,sellSideBook.size());
    }

}
