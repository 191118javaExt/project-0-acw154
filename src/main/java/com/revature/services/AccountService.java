package com.revature.services;

import java.util.List;

import com.revature.models.Account;
import com.revature.repositories.*;

public class AccountService {
	AccountDAO repository = new AccountDAOImpl();
	
	public List<Account> getAllAccounts(){
		return repository.getAllAccounts();
	}
	
	public Account getAccount(int id) {
		return repository.getAccount(id);
	}
	
	public boolean insert(Account a) {
		return repository.insert(a);
	}
	
	public boolean delete(Account a) {
		return repository.delete(a);
	}
	
	public boolean deposit(Account a, double amt) {
		return repository.deposit(a, amt);
	}
	
	public boolean withdraw(Account a, double amt) {
		return repository.withdraw(a, amt);
	}
	
	public boolean transfer(Account from, Account to, double amt) {
		return repository.transfer(from, to, amt);
	}
	
	public int getNextIDInSequence() {
		 return repository.getNextIDInSequence();
	}
	
	public double getBalance(Account a) {
		return repository.getBalance(a);
	}

}
