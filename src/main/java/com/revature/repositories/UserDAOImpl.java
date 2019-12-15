package com.revature.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.revature.models.Account;
import com.revature.models.User;
import com.revature.util.ConnectionUtil;

public class UserDAOImpl implements UserDAO {
	private static Logger logger = Logger.getLogger(UserDAOImpl.class);

	@Override
	public List<User> findAllActive() {
		List<User> list = new ArrayList<>();
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.users WHERE (approval_status = 1)";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//int id = rs.getInt("user_id");
				String username = rs.getString("username");
				String password = rs.getString("sha256_password");
				String role = rs.getString("role");
				int approvalStatus = rs.getInt("approval_status");
				int account_id = rs.getInt("account_id");
				User u = new User(username, password, role, approvalStatus, account_id);
				list.add(u);
			}
			rs.close();
		} catch (SQLException e) {
			logger.warn("Operation 'Find All Active Users' Failed; "  + e.getMessage());
		}
		return list;
	}

	@Override
	public List<User> findAllPending() {
		List<User> list = new ArrayList<>();
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.users WHERE (approval_status = 0);";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//int id = rs.getInt("u_id");
				String username = rs.getString("username");
				String password = rs.getString("sha256_password");
				String role = rs.getString("role");
				int approvalStatus = rs.getInt("approval_status");
				int account_id = rs.getInt("account_id");
				User u = new User(username, password, role, approvalStatus, account_id);
				list.add(u);
			}
			rs.close();
		} catch (SQLException e) {
			logger.warn("Operation 'Find All Pending Users' Failed; "  + e.getMessage());
		}
		return list;
	}

	@Override
	public User findUser(String name) {
		User user;
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.users WHERE username = (?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//int id = rs.getInt("user_id");
				String username = rs.getString("username");
				String password = rs.getString("sha256_password");
				String role = rs.getString("role");
				int approvalStatus = rs.getInt("approval_status");
				int account_id = rs.getInt("account_id");
				return new User(username, password, role, approvalStatus, account_id);
			}
			rs.close();
		} catch (SQLException e) {
			logger.warn("Operation 'Find User' Failed; "  + e.getMessage());
		}
		return null;
	}

	@Override
	public boolean insert(User u) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "INSERT INTO project0.users (username, sha256_password, role)"
					+ "VALUES (?, ?, ?);";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, u.getUsername());
			stmt.setString(2, u.getPassword());
			stmt.setString(3, u.getRole());
			// THESE TWO VALUES BELOW MUST BE 0 & -1 upon initialization
			// stmt.setInt(4, u.getApprovalStatus());
			// stmt.setInt(5, -1);
			if(!stmt.execute()) {
				return true;
			}

		} catch (SQLException exc) {
			logger.warn("Operation 'Insert User' Failed; "  + exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(User u) {
		// Need to check whether user trying to be deleted is current user
		// If User is associated with an account then delete user and delete account
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "DELETE FROM project0.users WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, u.getUsername());
			if(!stmt.execute()) {
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Delete User' Failed; "  + exc.getMessage());
			return false;
		}
		return true;
	}


	@Override
	public boolean approveClient(User u, Account a) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET account_id = (?), approval_status = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, a.getAccount_id());
			stmt.setInt(2, 1);
			stmt.setString(3, u.getUsername());
			if(!stmt.execute()) {
				u.setApprovalStatus(1);
				u.setAccount_id(a.getAccount_id());
				return true;
			}
		} catch (SQLException e) {
			logger.warn("Operation 'Approve Client' Failed; "  + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean approveEmployee(User e) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET approval_status = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 1);
			stmt.setString(2, e.getUsername());
			if(!stmt.execute()) {
				e.setApprovalStatus(1);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Approve Employee' Failed; "  + exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public List<User> findAllUsers() {
		List<User> list = new ArrayList<>();
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "SELECT * FROM project0.users;";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//int id = rs.getInt("user_id");
				String username = rs.getString("username");
				String password = rs.getString("sha256_password");
				String role = rs.getString("role");
				int approvalStatus = rs.getInt("approval_status");
				int account_id = rs.getInt("account_id");
				User u = new User(username, password, role, approvalStatus, account_id);
				list.add(u);
			}
			rs.close();
		} catch (SQLException e) {
			logger.warn("Operation 'Find All Users' Failed; " +  e.getMessage());
		}
		return list;
	}

	@Override
	public boolean updatePassword(User u, String newVal) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String newPass = DigestUtils.sha256Hex(newVal);
			String query = "UPDATE project0.users SET sha256_password = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, newPass);
			stmt.setString(2, u.getUsername());
			if(!stmt.execute()) {
				u.setPassword(newPass);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Update Password' Failed; " +  exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean updateRole(User u, char c) {
		try (Connection conn = ConnectionUtil.getConnection()){
			switch(Character.toUpperCase(c)) {
			case 'E': {
				if(u.getAccount_id() == -1) {
					String query = "UPDATE project0.users SET role = (?) WHERE username = (?);";		
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1, "Employee");
					stmt.setString(2, u.getUsername());
					if(!stmt.execute()) {
						u.setRole("Employee");
						return true;
					}
				} else {
					String query = "UPDATE project0.users SET role = (?), account_id = (?) WHERE username = (?);";		
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1, "Employee");
					stmt.setNull(2, java.sql.Types.NULL);
					stmt.setString(3, u.getUsername());
					if(!stmt.execute()) {
						u.setRole("Employee");
						u.setAccount_id(-1);
						return true;
					}
				}
				break;
			}
			case 'A':{
				if(u.getAccount_id() == -1) {
					String query = "UPDATE project0.users SET role = (?) WHERE username = (?);";		
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1, "Admin");
					stmt.setString(2, u.getUsername());
					if(!stmt.execute()) {
						u.setRole("Admin");
						return true;
					}
					
				} else {
					String query = "UPDATE project0.users SET role = (?), account_id = (?) WHERE username = (?);";		
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1, "Admin");
					stmt.setInt(2, java.sql.Types.NULL);
					stmt.setString(3, u.getUsername());
					if(!stmt.execute()) {
						u.setRole("Admin");
						u.setAccount_id(-1);
						return true;
					}		
				}
				break;
			}
			case 'C':{
				String query = "UPDATE project0.users SET role = (?) WHERE username = (?);";		
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, "Client");
				stmt.setString(2, u.getUsername());
				if(!stmt.execute()) {
					u.setRole("Admin");
					return true;
				}
				break;
			}
			default:{
				logger.warn("Operation 'Update Role' Failed - Invalid Input ");
				return false;
			}
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Update Role' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean denyUser(User u) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET approval_status = (?), account_id = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, -1);
			stmt.setInt(2, java.sql.Types.NULL);
			stmt.setString(3, u.getUsername());
			if(!stmt.execute()) {
				u.setApprovalStatus(-1);
				u.setAccount_id(-1);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Deny User' Failed; " +  exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUsername(User u, String newVal) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET username = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, newVal);
			stmt.setString(2, u.getUsername());
			if(!stmt.execute()) {
				u.setUsername(newVal);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Update Username' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean setPending(User u) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET approval_status = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, 0);
			stmt.setString(2, u.getUsername());
			if(!stmt.execute()) {
				u.setApprovalStatus(0);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Set Approval Status Pending' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean updateAccount(User u, Account a) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET account_id = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, a.getAccount_id());
			stmt.setString(2, u.getUsername());
			if(!stmt.execute()) {
				u.setAccount_id(a.getAccount_id());
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Update Account ID' Failed; " + exc.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean detachAccount(User u) {
		try (Connection conn = ConnectionUtil.getConnection()){
			String query = "UPDATE project0.users SET account_id = (?) WHERE username = (?);";		
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, java.sql.Types.NULL);
			stmt.setString(2, u.getUsername());
			if(!stmt.execute()) {
				u.setAccount_id(-1);
				return true;
			}
		} catch (SQLException exc) {
			logger.warn("Operation 'Detach Account' Failed; "  + exc.getMessage());
			return false;
		}
		return true;
	}

	
}
