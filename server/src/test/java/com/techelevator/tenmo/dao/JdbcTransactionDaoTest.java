package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcTransactionDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);
    }

    @Test
    public void getTransactionByIdReturnsCorrectTransaction() {
        int expected = 1001;
        Transaction actual = sut.getTransactionById(3001);
         Assert.assertEquals(expected, actual.getUser1Id());
    }
    @Test
    public void getTransactionByIdReturnsWrongTransaction() {
        int expected = 3001;
        Transaction actual = sut.getTransactionById(3002);
        Assert.assertNotEquals(expected, actual.getTransactionId());
    }

    @Test
    public void getAllTransactionsByUserReturnsCorrectTransactions() {
        int expected =2;
        List <Transaction> actual = sut.getAllTransactionsByUser(1001);
        Assert.assertEquals(expected,actual.size());
    }
    @Test
    public void getAllTransactionsByUserReturnsWrongTransactions() {
        int expected =3;
        List <Transaction> actual = sut.getAllTransactionsByUser(1001);
        Assert.assertNotEquals(expected,actual.size());
    }

    @Test
    public void sendMoneyReturnsCorrectTrnsaction() {
        int expected = 3003;
        Transaction actual = sut.sendMoney("user", "bob", 200);
        Assert.assertEquals(expected, actual.getTransactionId());
    }
    @Test(expected = ResponseStatusException.class) //https://www.baeldung.com/junit-assert-exception
    public void sendMoneyReturnsWrongTrnsaction() {
        Transaction actual = sut.sendMoney("user", "bob", -100);
        Assert.assertNull(actual);
    }

    @Test
    public void getSenderAccountReturnsCorrectAccount() {
        int expected = 1001;
        Account actual = sut.getSenderAccount("bob");
        Assert.assertEquals(expected, actual.getUserId());
    }
    @Test
    public void getSenderAccountReturnsWrongAccount() {
        int expected = 1002;
        Account actual = sut.getSenderAccount("bob");
        Assert.assertNotEquals(expected, actual.getUserId());
    }
}