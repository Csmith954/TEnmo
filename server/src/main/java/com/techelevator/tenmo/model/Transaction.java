package com.techelevator.tenmo.model;

public class Transaction {
    private int transactionId;
    private int user1Id;
    private int user2Id;
    private double transferAmmount;
    private String transferStatus;

    public Transaction() {
    }

    public Transaction(int transactionId, int user1Id, int user2Id, double transferAmmount, String transferStatus) {
        this.transactionId = transactionId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.transferAmmount = transferAmmount;
        this.transferStatus = transferStatus;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public double getTransferAmmount() {
        return transferAmmount;
    }

    public void setTransferAmmount(double transferAmmount) {
        this.transferAmmount = transferAmmount;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                ", transferAmmount=" + transferAmmount +
                ", transferStatus='" + transferStatus + '\'' +
                '}';
    }
}
