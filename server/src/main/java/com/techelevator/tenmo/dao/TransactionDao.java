package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface TransactionDao {
    public Transaction getTransactionById(int transactionId);
    public List<Transaction> getAllTransactionsByUser(int userId);
    public Transaction sendMoney( String senderName, String receiverName, double transferAmount);
//    List<User> findAllMinusCurrentUser(List<User> userList);
    public Account getSenderAccount(String senderName);



}
