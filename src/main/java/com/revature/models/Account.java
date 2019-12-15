package com.revature.models;

public class Account {
	private int account_id;
	private double balance;
	private int transCounter;
	
	
	
	public Account(int account_id, double balance, int transCounter) {
		super();
		this.account_id = account_id;
		this.balance = balance;
		this.transCounter = transCounter;
	}
	
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public int getTransCounter() {
		return transCounter;
	}

	public void setTransCounter(int transCounter) {
		this.transCounter = transCounter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + account_id;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + transCounter;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (account_id != other.account_id)
			return false;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (transCounter != other.transCounter)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [account_id=" + account_id + ", balance=" + balance + ", transCounter=" + transCounter + "]";
	}
	
}