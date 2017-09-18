package com.ubs.service;

import com.ubs.entity.User;
import com.ubs.entity.UserEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by omib on 16/09/2017.
 */
@Service
public class UserServiceImpl implements UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    ConcurrentMap<String,User> userCache = new ConcurrentHashMap<>();

    @Override
    public boolean loginUser(String username, String password)
    {
        log.info("Logging in user {} to OMS", username);
        if (userCache.containsKey(username))
        {
            User user = userCache.get(username);
            return user.getPassword().equals(password)?true:false;
        }
        return false;
    }

    @Override
    public void saveUser(String username, String password)
    {
        log.info("Adding user {} to cache.", username);
        User user = new User(username,password);
        userCache.put(username,user);
    }

    @Override
    public Long getUserId(String username)
    {
        log.info("Getting userID for user {}", username);
        if(userCache.containsKey(username))
        {
            return userCache.get(username).getUserId();
        }

        return null;
    }
}
