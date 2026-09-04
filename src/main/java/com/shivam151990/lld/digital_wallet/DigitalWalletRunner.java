package com.shivam151990.lld.digital_wallet;

import com.shivam151990.lld.digital_wallet.model.User;
import com.shivam151990.lld.digital_wallet.model.Wallet;
import com.shivam151990.lld.digital_wallet.repo.InMemoryTxRepo;
import com.shivam151990.lld.digital_wallet.repo.InMemoryWalletRepo;
import com.shivam151990.lld.digital_wallet.repo.TxRepo;
import com.shivam151990.lld.digital_wallet.service.BankAccount;
import com.shivam151990.lld.digital_wallet.service.CreditCard;
import com.shivam151990.lld.digital_wallet.service.PaymentMethod;
import com.shivam151990.lld.digital_wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.Currency;

public class DigitalWalletRunner {
    public static void main(String[] args) {
        TxRepo txRepo = new InMemoryTxRepo();
        WalletService walletService = new WalletService(new InMemoryWalletRepo(), txRepo);

        // Create Users and payment methods
        User shivam = new User("Shivam", "shivam@gmail.com");
        PaymentMethod shivamBank = new BankAccount("shivam_bank", "1234");
        PaymentMethod shivamCredit = new CreditCard("shivam_credit", "123456789");

        User ashish = new User("Ashish", "ashish@gmail.com");
        PaymentMethod ashishBank = new BankAccount("ashish_bank", "1234");
        PaymentMethod ashishCredit = new CreditCard("ashish_credit", "123456789");

        // Create Wallet for shivam and topup via a bank account
        Wallet shivamWallet = walletService.createWallet(shivam, Currency.getInstance("USD"));
        shivamWallet.addPaymentMethod(shivamBank);
        shivamWallet.addPaymentMethod(shivamCredit);
        walletService.topUp(shivamWallet.getId(), shivamBank.getId(), new BigDecimal(100));
        System.out.println("Shivam start balance: " + shivamWallet.getBalance());

        // Create Wallet for ashish
        Wallet ashishWallet = walletService.createWallet(ashish, Currency.getInstance("USD"));
        ashishWallet.addPaymentMethod(ashishBank);
        ashishWallet.addPaymentMethod(ashishCredit);

        // Transfer from one wallet to another
        int amount = 100;
        System.out.println("Shivam transfers " + amount + " to Ashish");
        walletService.transfer(shivamWallet.getId(), ashishWallet.getId(), new BigDecimal(amount));

        //Checking wallet balance for Each
        System.out.println("Shivam Balance: " + shivamWallet.getBalance());
        System.out.println("Ashish Balance: " + ashishWallet.getBalance());

        // Check Transaction logs
        System.out.println("tx Logs Shivam: " + txRepo.findTransactions(shivamWallet.getId()));
        System.out.println("tx Logs Ashish: " + txRepo.findTransactions(ashishWallet.getId()));
    }
}
