package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.security.Principal;
import java.util.List;

public interface UserDao {

    //add a list all accounts by user

    List<User> findAll(int userId);

    User findByUsername(String username);

    String findUsernameById(int userId);

    int findIdByUsername(String username);

    boolean create(String username, String password);
}
