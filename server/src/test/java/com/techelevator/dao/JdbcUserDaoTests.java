package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;



public class JdbcUserDaoTests extends BaseDaoTests{

    private static User user1 = new User(1001, "bob", "password", "\"$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2\"");
    private static User user2 = new User(1002, "user", "password", "$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy");


    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }
    @Test
    public void findIdByUserNameReturnsCorrectName(){
        int expected = 1001;
        int actual = sut.findIdByUsername("bob");
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void findIdByUserNameReturnsWrongValue(){
        int expected = 1003;
        int actual = sut.findIdByUsername("bob");
        Assert.assertNotEquals(expected, actual);
    }
    @Test
    public void findAllByUserIdReturnsWrongValue(){
        List<User> expected = new ArrayList<>();
        expected.add(sut.findByUsername("bob"));
        List<User> actual = new ArrayList<>();
        actual = sut.findAll(1001);
        Assert.assertNotEquals(expected.toString(), actual.toString());

    }
    @Test
    public void findAllByUserIdReturnsCorrectValue() {
        List<User> expected = new ArrayList<>();
        expected.add(sut.findByUsername("bob"));
        List<User> actual = new ArrayList<>();
        actual = sut.findAll(1002);
        Assert.assertEquals(expected.toString(), actual.toString());


        }
        @Test
    public void findByUserNameReturnsCorrectUser() {
        String expected = "bob";
        User actual = sut.findByUsername("bob");
        Assert.assertEquals(expected, actual.getUsername());


        }
    @Test
    public void findByUserNameReturnsWrongUser() {
        String expected = "bob";
        User actual = sut.findByUsername("user");
        Assert.assertNotEquals(expected, actual.getUsername());


    }
    @Test
    public void findUserNameByIdReturnsCorrectUser(){
        String expected = "user";
        String actual = sut.findUsernameById(1002);
        Assert.assertEquals(expected, actual);

    }
    @Test
    public void findUserNameByIdReturnsWrongUser(){
        String expected = "user";
        String actual = sut.findUsernameById(1001);
        Assert.assertNotEquals(expected, actual);

    }

    }




