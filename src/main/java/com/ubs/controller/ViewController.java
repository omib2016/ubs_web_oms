package com.ubs.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.ubs.entity.*;
import com.ubs.service.OrderBookService;
import com.ubs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by omib on 17/09/2017.
 */
@Controller
public class ViewController
{
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OrderBookService orderBookService;

    public ViewController(UserService userService, OrderBookService orderBookService)
    {
        this.userService = userService;
        this.orderBookService = orderBookService;
    }

    @RequestMapping("/")
    public String home(Model model)
    {
        model.addAttribute("userentry",new UserEntry());
        model.addAttribute("logonentry", new LogonEntry());
        return "home";
    }

    @RequestMapping(value = "/viewAllOrders", method = RequestMethod.GET)
    public String viewAllOrders(@RequestParam(value = "username") String username)
    {
        return "viewallorders";
    }

    @RequestMapping(value = "/viewAllTrades", method = RequestMethod.GET)
    public String viewAllTrades(@RequestParam(value = "username") String username)
    {
        return "viewalltrades";
    }

    @RequestMapping(value = "/viewPlaceOrder", method = RequestMethod.GET)
    public String viewPlaceOrder(@RequestParam(value = "username") String username,@ModelAttribute OrderEntry orderEntry,Model model)
    {
        model.addAttribute("orderentry",orderEntry);
        return "viewPlaceOrder";
    }

    @RequestMapping("/home")
    public String returnHome(Model model)
    {
        model.addAttribute("userentry",new UserEntry());
        model.addAttribute("logonentry", new LogonEntry());
        return "success";
    }

    @RequestMapping("/loginUser")
    public String loginUser(@ModelAttribute LogonEntry logonEntry)
    {
        String username = logonEntry.getUsername();
        String password = logonEntry.getPassword();

        if (userService.loginUser(username,password))
        {
            return "success";
        }
        else
        {
            return "failure";
        }
    }

    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public String saveUser(@ModelAttribute UserEntry userEntry,Model model)
    {
        model.addAttribute("logonentry",new LogonEntry());
        userService.saveUser(userEntry.getUsername(),userEntry.getPassword());
        return "register";
    }

    @RequestMapping(value = "/placeOrder", method = RequestMethod.POST)
    public String placeorder(@ModelAttribute OrderEntry orderEntry, Model model)
    {
        log.info("Request received to place order {} for user", orderEntry);
        model.addAttribute("orderentry",orderEntry);
        String userName = orderEntry.getUsername();
        Long userId = userService.getUserId(userName);
        long itemId = orderEntry.getItem().getItemId();
        int quantity = orderEntry.getQuantity();
        BigDecimal price = orderEntry.getPrice();
        Side side = orderEntry.getSide();
        orderBookService.placeOrder(new Order(itemId, userId, quantity, price, side));
        return "ordersuccess";
    }



}
