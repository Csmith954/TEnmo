package com.techelevator.tenmo.controller;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;
import com.techelevator.tenmo.dao.AccountDao;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@PreAuthorize("isAuthenticated()")
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;
    private AccountDao accountDao;
    private TransactionDao transactionDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, AccountDao accountDao, TransactionDao transactionDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginDTO loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        User user = userDao.findByUsername(loginDto.getUsername());

        return new LoginResponse(jwt, user);
    }
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        if (!userDao.create(newUser.getUsername(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
        }

    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> listOfUsers(Principal principal){
        return userDao.findAll(userDao.findIdByUsername(principal.getName()));
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public double getBalanceByName( Principal principal) {

        return accountDao.getAccountBalanceByUser(principal.getName());
    }

    @RequestMapping(path="/transactions/{transactionId}", method = RequestMethod.GET)
    public Transaction getTransactionById(@PathVariable int transactionId) {
        Transaction transaction = null;
        transaction = transactionDao.getTransactionById(transactionId);
        if (transaction == null){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        else{
            return transaction;
        }
    }

    @RequestMapping(path = "/transactions/list", method = RequestMethod.GET)
    public List<Transaction> listOfTransactionsByUser(Principal principal) {
        return transactionDao.getAllTransactionsByUser(userDao.findIdByUsername(principal.getName()));
    }


    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public Transaction sendMoneyByUser(@RequestBody Transaction transaction){
        String userOne = userDao.findUsernameById(transaction.getUser1Id());
        String userTwo = userDao.findUsernameById(transaction.getUser2Id());
        double transferAmount = transaction.getTransferAmmount();
       transaction = transactionDao.sendMoney(userOne, userTwo, transferAmount);
       return transaction;
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private User user;

        LoginResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
    }
}

