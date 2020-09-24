package com.mihaijianu.dmlbank.banktransfer;

import com.mihaijianu.dmlbank.entities.Account;

public class BankTransferController {

    // Singleton class
    private static BankTransferController ref;
    public static BankTransferController getReference(){
        if (ref == null){
            ref = new BankTransferController();
        }
        return ref;
    }

    public void pay(Account payerAccount, int toPay) throws InsufficentBalanceException {

        // checks if payerAccount has enough money to pay transaction
        if (payerAccount.getBalance() < toPay){
            throw new InsufficentBalanceException();
        }

        // Does the transfer
        this.doTransfer(payerAccount, 0 - toPay);
    }

    private void doTransfer(Account account, int amount){

        // Does the transfer
        account.setBalance(account.getBalance() + amount);
    }
}
