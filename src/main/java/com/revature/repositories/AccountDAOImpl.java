package com.revature.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.models.Account;
import com.revature.models.User;
import com.revature.util.ConnectionUtil;

public class AccountDAOImpl implements AccountDAO{
	private static Logger logger = Logger.getLogger(UserDAOImpl.class);
	
	/**
	 * Returns an Account object associated with the given account id
	 */
	@Override
	public Account getAccount(int id) {
		// TODO Auto-generated method stub
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int acc_id = rs.getInt("account_id");
				double balance = rs.getDouble("balance");
				int count = rs.getInt("transCounter");
				Account a = new Account(acc_id, balance, count);
				return a;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Operation 'Retreive Account' Failed; " + e.getMessage());
		}
		return null;
	}

	/**
	 * Increments the balance of the given account by a given amount.
	 */
	@Override
	public boolean deposit(Account a, double amt) {
		
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.account SET balance = (?), transCounter = (?) WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setDouble(1, a.getBalance() + amt);
			stmt.setInt(2, a.getTransCounter() + 1);
			stmt.setInt(3, a.getAccount_id());
			if(!stmt.execute()) {
				a.setBalance(a.getBalance() + amt);
				logger.info("Account " + a.getAccount_id() + ": Deposit of " + amt + " successful");
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Operation 'Deposit' Failed; " + e.getMessage());
			return false;
		}		
		return true;
	}

	/**
	 * Decrements the balance field of the given account by a given amount. s
	 */
	@Override
	public boolean withdraw(Account a, double amt) {
		if (amt > a.getBalance()) {
			return false;
		}
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.account SET balance = (?), transCounter = (?) WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setDouble(1, a.getBalance() - amt);
			stmt.setInt(2, a.getTransCounter() + 1);
			stmt.setInt(3, a.getAccount_id());
			if(!stmt.execute()) {
				a.setBalance(a.getBalance() - amt);
				a.setTransCounter(a.getTransCounter() + 1);
				logger.info("Account " + a.getAccount_id() + ": Withdrawal of " + amt + " successful");
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Operation 'Withdraw' Failed; " + e.getMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * Transfers money from one account to another via the deposit and withdrawal methods
	 */
	@Override
	public boolean transfer(Account from, Account to, double amt) {
		// TODO: Ensure that transaction can be rolled back
		if(amt > from.getBalance()) {
			logger.warn("Transfer failed: Transfer amount exceeds Account " + from.getAccount_id() + " balance");
			return false;
		} else {
			if(withdraw(from, amt) && deposit(to, amt)) {
				logger.info("Transfer Successful: " + from.getAccount_id() + ": " + from.getBalance() + ", "
						+ to.getAccount_id() + ": " + to.getBalance());

				return true;
			}
		}
		logger.warn("Operation 'Transfer' Failed");
		return false;
	}

	/**
	 * Returns a list of all the accounts in the account table
	 */
	@Override
	public List<Account> getAllAccounts() {
		List<Account> list = new ArrayList<>();
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.account;";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int acc_id = rs.getInt("account_id");
				double bal = rs.getDouble("balance");
				int count = rs.getInt("transCounter");
				Account a = new Account(acc_id, bal, count);
				list.add(a);
			}
			rs.close();
		} catch (SQLException e) {
			logger.warn("Operation 'Get All Accounts' Failed; " + e.getMessage());
		}
		return list;
	}

	/**
	 * Inserts the account object into the account table
	 */
	@Override
	public boolean insert(Account a) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "INSERT INTO project0.account (balance, transCounter)"
					+ "VALUES (?, ?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setDouble(1, a.getBalance());
			stmt.setInt(2, a.getTransCounter());
			if(!stmt.execute()) {
				return true;
			}

		} catch (SQLException exc) {
			logger.warn("Operation 'Insert Account' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes the record containing the specified account from the account table
	 */
	@Override
	public boolean delete(Account a) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "DELETE FROM project0.account WHERE account_id = (?) ;";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, a.getAccount_id());
			if(!stmt.execute()) {
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Delete Account' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the largest account_id in database and increments it by 2;
	 */
	@Override
	public int getNextIDInSequence() {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT MAX(account_id) as last_id FROM project0.account;";		
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			int last_id = -1;
			while(rs.next()) {
				last_id = rs.getInt("last_id");
			}
			rs.close();
			return last_id + 2;
		} catch (SQLException exc) {
			logger.warn("Operation 'Retrieve Next ID' Failed; " + exc.getMessage());
		}
		return -1;
	}

	/**
	 * Returns the account balance of a given account 
	 */
	@Override
	public double getBalance(Account a) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT balance FROM project0.account WHERE account_id = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, a.getAccount_id());
			ResultSet rs = stmt.executeQuery();
			double bal = -1;
			while(rs.next()) {
				bal = rs.getDouble("balance");
			}
			rs.close();
			return bal;
		} catch (SQLException exc) {
			logger.warn("Operation 'Retrieve Balance' Failed; " + exc.getMessage());
		}
		return -1;
	}
	
}
