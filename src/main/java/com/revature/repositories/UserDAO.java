package com.revature.repositories;

import java.util.List;

import com.revature.models.Account;
import com.revature.models.User;

public interface UserDAO {
	public List<User> findAllUsers();
	public List<User> findAllActive();
	public List<User> findAllPending();
	public User findUser(String name);
	public boolean insert(User u);
	public boolean delete(User u);
	public boolean approveClient(User u, Account a);
	public boolean approveEmployee(User e);
	public boolean updateUsername(User u, String newVal);
	public boolean updatePassword(User u, String newVal);
	public boolean updateRole(User u, char c);
	public boolean setPending(User u);
	public boolean updateAccount(User u, Account a);
	public boolean denyUser(User u);
	public boolean detachAccount(User u);
	// Either adapt pending based on role or create additional method that filters 
}
