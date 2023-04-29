package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

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



    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));

        return account;
    }
}



