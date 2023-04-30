package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate  jdbctemplate;

    public JdbcAccountDao(JdbcTemplate jdbctemplate) {
        this.jdbctemplate = jdbctemplate;
    }



   @Override
   public double getAccountBalanceByUser(String name) {
       String sql = "select * from account join tenmo_user on account.user_id = tenmo_user.user_id where tenmo_user.username= ?";
       SqlRowSet result = jdbctemplate.queryForRowSet(sql, name);
       Account account = null;
       if(result.next()){
           account = mapRowToAccount(result);
       }
       double results = account.getBalance();
       return results;
   }

    @Override
    public Account getAccountById(int accountId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, accountId);
        if (rowSet.next()){
            account = mapRowToAccount(rowSet);
        }
        return account;
    }

    @Override
    public Account getAccountByUser(String name) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, name);
        if (rowSet.next()){
            account = mapRowToAccount(rowSet);
        }
        return account;
    }


    @Override
    public Account addAccountToUserId(int userId) {
        Account newAccount = null;
        String sql = "INSERT INTO account (user_id, balance) VALUES (?,1000) RETURNING account_id;";
        int accountId = jdbctemplate.queryForObject(sql, Integer.class, userId);
        if(accountId > 0){
            newAccount = getAccountById(accountId);
        }
        return newAccount;
    }

    @Override
    public List<Account> listAccountsByUserId(int userId) {
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, userId);
        while (rowSet.next()){
            accountList.add(mapRowToAccount(rowSet));
        }
        return accountList;
    }


    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));

        return account;
    }
}



