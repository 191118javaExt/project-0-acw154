package com.revature.services;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.revature.models.User;
import com.revature.util.ConnectionUtil;

public class UserServiceTest {
	UserService us = new UserService();
	
	@Before
	public void setUp() {
		us.insert(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0));
		us.insert(new User("MockEmployee", DigestUtils.sha256Hex("emppw"), "Employee", 0));
	}
	
	@After
	public void tearDown() {
		us.delete(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0));
		us.delete(new User("MockEmployee", DigestUtils.sha256Hex("emppw"), "Employee", 0));
	}

	@Test
	public void testFindUser() {
		User user = null;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockUser");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//int id = rs.getInt("user_id");
				String username = rs.getString("username");
				String password = rs.getString("sha256_password");
				String role = rs.getString("role");
				int approvalStatus = rs.getInt("approval_status");
				int account_id = rs.getInt("account_id");
				user = new User(username, password, role, approvalStatus, account_id);
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(user, us.findUser("MockUser"));
		//fail("Not yet implemented");
	}

	@Test
	public void testApproveEmployee() {
		us.approveEmployee(new User("MockEmployee", DigestUtils.sha256Hex("emppw"), "Employee", 0));
		int actualAS = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT approval_status FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockEmployee");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				actualAS = rs.getInt("approval_status");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(1, actualAS);
	}

	@Test
	public void testUpdatePassword() {
		us.updatePassword(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0), "changed");
		String actualPW = "";
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT sha256_password FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockUser");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				actualPW = rs.getString("sha256_password");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(DigestUtils.sha256Hex("changed"), actualPW);
	}

	@Test
	public void testUpdateRole() {
		us.updateRole(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0), 'A');
		String actualRole = "";
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT role FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockUser");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				actualRole = rs.getString("role");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals("Admin", actualRole);
	}

	@Test
	public void testDenyUser() {
		us.denyUser(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0));
		int deniedAS = 0;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT approval_status FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockUser");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				deniedAS = rs.getInt("approval_status");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(-1, deniedAS);
	}

	@Test
	public void testSetPending() {
		us.setPending(new User("MockUser", DigestUtils.sha256Hex("mockpass"), "Client", 0));
		int pendingAS = -1;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT approval_status FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "MockUser");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				pendingAS = rs.getInt("approval_status");
			}
			rs.close();
		} catch (SQLException e) {
		}
		assertEquals(0, pendingAS);
	}



}
