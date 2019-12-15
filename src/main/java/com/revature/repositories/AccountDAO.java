package com.revature.repositories;

import java.util.List;

import com.revature.models.Account;
import com.revature.models.User;

public interface AccountDAO {
	public List<Account> getAllAccounts();
	public Account getAccount(int id);
	public boolean insert(Account a);
	public boolean delete(Account a);
	public boolean deposit(Account a, double amt);
	public boolean withdraw(Account a, double amt);
	public boolean transfer(Account from, Account to, double amt);
	public boolean updateBalance(Account a, double newVal);
	public double getBalance(Account a);
	public boolean updateTransCounter(Account a, int newVal);
	public int getNextIDInSequence();
	
}
