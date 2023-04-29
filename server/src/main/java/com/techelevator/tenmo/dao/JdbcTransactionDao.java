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
            String balanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?); UPDATE account SET balance = balance + ? WHERE user_id =(SELECT user_id FROM tenmo_user WHERE username = ?)";
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
    public Account getSenderAccount(String senderName) {
        Account sender = null;
        String sql = "SELECT * FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
        SqlRowSet rowSet = jdbctemplate.queryForRowSet(sql, senderName);
        if (rowSet.next()){
            sender = mapRowToAccount(rowSet);
        }
        return sender;
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

}