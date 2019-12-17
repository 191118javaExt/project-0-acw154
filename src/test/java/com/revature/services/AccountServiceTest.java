package com.revature.services;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.models.Account;
import com.revature.services.AccountService;
import com.revature.util.ConnectionUtil;

public class AccountServiceTest {
	AccountService as = new AccountService();


	@Before
	public void setUp() throws Exception {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "INSERT INTO project0.account (account_id, balance, transCounter)"
					+ "VALUES (?, ?, ?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			stmt.setDouble(2, 100);
			stmt.setInt(3, 0);
			stmt.execute();
			String query2 = "INSERT INTO project0.account (account_id, balance, transCounter)"
					+ "VALUES (?, ?, ?);";
			PreparedStatement stmt2 = conn.prepareStatement(query2);
			stmt2.setInt(1, 200);
			stmt2.setDouble(2, 400.59);
			stmt2.setInt(3, 0);
			stmt2.execute();
		} catch (SQLException e) {
		}
	}

	@After
	public void tearDown() throws Exception {
		// tearDown will only work as intended if no accounts are created in between setUp and tearDown
		// else setUp must be changed to have a specific account ID that is lower than the start with value
		// of the serial account_id
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "DELETE FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			stmt.execute();
			String query2 = "DELETE FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt2 = conn.prepareStatement(query2);
			stmt2.setInt(1, 200);
			stmt2.execute();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGetAccount() {
		Account a = null;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				a = new Account(rs.getInt("account_id"), rs.getDouble("balance"), rs.getInt("transcounter"));
			}
			rs.close();
		} catch (SQLException e) {
		}
		Account b = as.getAccount(100);
		assertEquals(b, a);
	}

	@Test
	public void testDeposit() {
		//fail("Not yet implemented");
		Account a = as.getAccount(100);
		as.deposit(a, 50.05);
		double actualBal = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT balance FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				actualBal = rs.getDouble("balance");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(new Double(150.05), new Double(actualBal));
	}

	@Test
	public void testWithdraw() {
		Account a = as.getAccount(100);
		as.withdraw(a, 25.28);
		double actualBal = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT balance FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				actualBal = rs.getDouble("balance");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(new Double(74.72), new Double(actualBal));
	}

	@Test
	public void testTransfer() {
		int count = 0;
		double[] bals = new double[2];
		double[] expected = new double[] {100+88.88, 400.59-88.88};
		as.transfer(as.getAccount(200), as.getAccount(100), 88.88);
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT balance FROM project0.account WHERE account_id IN (?, ?) ORDER BY account_id ASC;";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 100);
			stmt.setInt(2, 200);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				bals[count] = rs.getDouble("balance");
				count++;
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertArrayEquals(expected, bals, 0.00);
	}

	@Test
	public void testGetNextIDInSequence() {
		int nextId = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT MAX(account_id) as last_id FROM project0.account;";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				nextId = rs.getInt("last_id");
			}
			rs.close();
		} catch (SQLException e) {
			
		}
		assertEquals((nextId + 2), as.getNextIDInSequence());
	}
	
	
	@Test
	public void testGetBalance() {
		double bal = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT balance FROM project0.account WHERE account_id = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 200);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				bal = rs.getDouble("balance");
			}
		} catch (SQLException e) {
		}
		assertEquals(new Double(400.59), new Double(bal));
	}

	
}
