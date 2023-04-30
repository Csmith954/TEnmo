package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    //add additional account to user (user name or user id)

    //add a list all accounts by user (user name or user id)

    double getAccountBalanceByUser(String name);

    Account getAccountById(int accountId);

    Account getAccountByUser(String name);

    Account addAccountToUserId(int userId);

    List<Account> listAccountsByUserId(int userId);


}
