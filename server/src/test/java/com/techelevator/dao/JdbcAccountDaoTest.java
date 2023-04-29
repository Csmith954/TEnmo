package com.techelevator.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private static Account user1 = new Account(2001, 1001, 1000);
    private static Account user2 = new Account(2002, 1002, 1000);

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccountBalanceByUserReturnsBalance() {
        double expected = user1.getBalance();
        double user1Balance = sut.getAccountBalanceByUser("user");
        Assert.assertEquals(expected, user1Balance, 0.01);

    }
    @Test
    public void getAccountBalanceByUserReturnsWrongBalance() {
        double expected = user1.getBalance()-200;
        double user1Balance = sut.getAccountBalanceByUser("user");
        Assert.assertNotEquals(expected, user1Balance, 0.01);

    }

}
