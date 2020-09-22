package com.mihaijianu.dmlbank.entities;

public class Account{

    private static Account reference = null;
    private String username = "Bill Gates";
    private int balance = 120;

    public static Account getReference(){
        if(reference == null){
            reference = new Account();
        }
        return reference;
    }

    private Account(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}


