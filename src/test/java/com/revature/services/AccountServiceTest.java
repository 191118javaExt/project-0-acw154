package com.revature.services;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.models.Account;
import com.revature.services.AccountService;
import com.revature.util.ConnectionUtil;

public class AccountServiceTest {
	AccountService as;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AccountService as = new AccountService();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

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
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGetAllAccounts() {
		fail("Not yet implemented");
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
		} catch (SQLException e) {
		}
		assertEquals(a, as.getAccount(100));
	}

	@Test
	public void testInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testWithdraw() {
		fail("Not yet implemented");
	}

	@Test
	public void testTransfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextIDInSequence() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateBalance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBalance() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateTransCounter() {
		fail("Not yet implemented");
	}

}
