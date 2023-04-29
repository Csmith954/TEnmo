package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;

@Component
public class JdbcTransactionDao implements TransactionDao {
    private JdbcTemplate jdbctemplate;
    private JdbcUserDao userDao;

    public JdbcTransactionDao(JdbcTemplate jdbctemplate) {
        this.jdbctemplate = jdbctemplate;
    }




    public Transaction getTransactionById(int transactionId) {
        Transaction transaction = null;
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?;";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, transactionId);
        if (rowSet.next()) {
            transaction = mapRowToTransaction(rowSet);
        }
        return transaction;
    }


    public List<Transaction> getAllTransactionsByUser(int userId) {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_1_id = ? OR user_2_id = ?;";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, userId, userId);
        while (rowSet.next()) {
            transactionList.add(mapRowToTransaction(rowSet));
        }
        return transactionList;
    }

    public Transaction sendMoney(String senderName, String receiverName, double transferAmount) {
        Transaction transaction = null; //set up a transaction to be returned to the request body
        Account account1 = getSenderAccount(senderName); // need the sender account info to check if balance being sent is valid
        double senderBalance = account1.getBalance(); // setting the sender balance onto variable

        if (senderBalance > transferAmount && transferAmount > 0 && (!senderName.equals(receiverName))){
            String sql = "INSERT INTO transactions (transfer_ammount, user_1_id, user_2_id, transfer_status) VALUES (?, (SELECT user_id FROM tenmo_user WHERE username = ?), " +
                    "(SELECT user_id FROM tenmo_user WHERE username = ?), ?) RETURNING transaction_id;";
            int transactionId = jdbctemplate.queryForObject(sql, Integer.class, transferAmount, senderName, receiverName, "approved"); // getting a transaction id after creating a transaction with the parameter info
            transaction = getTransactionById(transactionId); // setting the transaction

            // update the account balances to reflect the transfer
            String balanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?); UPDATE account SET balance = balance + ? WHERE user_id =(SELECT user_id FROM tenmo_user WHERE username = ?);";
            int result = jdbctemplate.update(balanceSql, transferAmount, senderName, transferAmount, receiverName);
            if (result == 0){
                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return transaction;
    }

    @Override
    public Transaction requestMoney(String senderName, String receiverName, double transferAmount) {
        //create a transaction object
        Transaction request = null;
        //check if the request is valid to be sent
        if(transferAmount > 0 && (!senderName.equals(receiverName))){
            //create a transaction and add to table, set to pending status
            String sql = "INSERT INTO transactions (transfer_ammount, user_1_id, user_2_id, transfer_status) VALUES (?, (SELECT user_id FROM tenmo_user WHERE username = ?), " +
                    "(SELECT user_id FROM tenmo_user WHERE username = ?), ?) RETURNING transaction_id;";
            int transactionId = jdbctemplate.queryForObject(sql, Integer.class,transferAmount, senderName, receiverName, "pending");
            //set the var with the new info
            request = getTransactionById(transactionId);

//            String balanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?); UPDATE account SET balance = balance + ? WHERE user_id =(SELECT user_id FROM tenmo_user WHERE username = ?);";
//            int result = jdbctemplate.update(balanceSql, transferAmount, senderName, transferAmount, receiverName);
//            if (result == 0){
//                throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
//            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return request;
    }

    @Override
    public Transaction respondToRequest(boolean yesOrNo, Transaction pendingTransaction) {
        //if the user approves the transaction, check if the senders balance can cover the transaction and run sendMoney()
        Transaction finalTransaction = pendingTransaction;
//        Account senderAccount = getSenderAccountById(finalTransaction.getUser1Id());
        double transferAmmount = finalTransaction.getTransferAmmount();
        int user1Id = finalTransaction.getUser1Id();
        String user1Name = findUsernameByIdTransaction(user1Id);
        int user2Id = finalTransaction.getUser2Id();
        String user2Name = findUsernameByIdTransaction(user2Id);

        if(yesOrNo){
            Account account1 = getSenderAccount(user1Name); // need the sender account info to check if balance being sent is valid
            double senderBalance = account1.getBalance(); // setting the sender balance onto variable

            if (senderBalance > transferAmmount && transferAmmount > 0 && (!user1Name.equals(user2Name))){
                String sql = "INSERT INTO transactions (transfer_ammount, user_1_id, user_2_id, transfer_status) VALUES (?, (SELECT user_id FROM tenmo_user WHERE username = ?), " +
                        "(SELECT user_id FROM tenmo_user WHERE username = ?), ?) RETURNING transaction_id;";
                int transactionId = jdbctemplate.queryForObject(sql, Integer.class, transferAmmount, user1Name, user2Name, "approved"); // getting a transaction id after creating a transaction with the parameter info
                finalTransaction = getTransactionById(transactionId); // setting the transaction

                // update the account balances to reflect the transfer
                String balanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?); UPDATE account SET balance = balance + ? WHERE user_id =(SELECT user_id FROM tenmo_user WHERE username = ?);";
                int result = jdbctemplate.update(balanceSql, transferAmmount, user1Name, transferAmmount, user2Name);
                if (result == 0){
                    throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
                }
            }
        } else {
            finalTransaction.setTransferStatus("rejected");
        }
        return finalTransaction;
    }

    @Override
    public List<Transaction> getTransactionsByStatusForUser(String status, int userId) {
        //create a list var and populate it with the users transactions filitered by status
        List<Transaction> allTransactionsByUser = getAllTransactionsByUser(userId);
        List<Transaction> filteredTransactions = new ArrayList<>();

        //use a for loop to check the status and write to new list
        for (Transaction transaction : allTransactionsByUser){
            if(transaction.getTransferStatus().equals(status)){
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    @Override
    public Account getSenderAccount(String senderName) {
        Account sender = null;
        String sql = "SELECT * FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, senderName);
        if (rowSet.next()){
            sender = mapRowToAccount(rowSet);
        }
        return sender;
    }

    @Override
    public Account getSenderAccountById(int userId) {
        Account sender = null;
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            sender = mapRowToAccount(rowSet);
        }
        return sender;
    }

    @Override
    public String findUsernameByIdTransaction(int userId) {
        String sql = "SELECT * FROM tenmo_user WHERE user_id = ?;";
        String name = "";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, userId);
        if(rowSet.next()){
            User user = mapRowToUser(rowSet);
            name = user.getUsername();
        }
        return name;
    }


    private Transaction mapRowToTransaction(SqlRowSet rs) {
        Transaction result = new Transaction();
        result.setTransactionId(rs.getInt("transaction_id"));
        result.setUser1Id(rs.getInt("user_1_id"));
        result.setUser2Id(rs.getInt("user_2_id"));
        result.setTransferAmmount(rs.getDouble("transfer_ammount"));
        result.setTransferStatus(rs.getString("transfer_status"));

        return result;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));

        return account;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }

}