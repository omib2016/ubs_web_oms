package com.ubs.controller;

import com.ubs.entity.*;
import com.ubs.service.OrderBookService;
import com.ubs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omib on 16/09/2017.
 */
@RestController
public class UserController
{
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OrderBookService orderBookService;

    @Autowired
    public UserController(UserService userService, OrderBookService orderBookService)
    {
        this.userService = userService;
        this.orderBookService = orderBookService;
    }


    @RequestMapping(value = "/getAllOrders", method = RequestMethod.GET)
    public List<Order> getAllOrders(@RequestParam(value = "username") String username)
    {
        log.info("Request received to view all orders for user {}", username);
        Long userId = userService.getUserId(username);
//        ArrayList<Order> result = new ArrayList();
//        result.add(new Order(Item.A.getItemId(),123L,100,BigDecimal.TEN,Side.BUY));
//        result.add(new Order(Item.A.getItemId(),123L,100,BigDecimal.TEN,Side.SELL));
//        return result;
        return orderBookService.getAllOrders(userId);
    }

    @RequestMapping(value = "/getAllTrades",  method = RequestMethod.GET)
    public List<Trade> getAllTrades(@RequestParam(value = "username") String username)
    {
        log.info("Request received to view all trades for user {}", username);
//        ArrayList<Trade> trades = new ArrayList<>();
//        trades.add(new Trade(123L,245L,1L,10,BigDecimal.TEN,System.currentTimeMillis()));
//        trades.add(new Trade(123L,245L,1L,10,BigDecimal.ONE,System.currentTimeMillis()));
//        return trades;

        Long userId = userService.getUserId(username);
        return orderBookService.getAllTrades(userId);
    }









}
