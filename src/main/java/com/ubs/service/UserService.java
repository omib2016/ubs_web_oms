package com.ubs.service;

import com.ubs.entity.UserEntry;

/**
 * Created by omib on 16/09/2017.
 */
public interface UserService
{

    boolean loginUser(String username,String password);
    void saveUser(String username,String password);
    Long getUserId(String username);

}
