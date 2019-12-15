package com.revature.services;

import java.util.List;

import org.omg.CORBA.RepositoryIdHelper;

import com.revature.models.Account;
import com.revature.models.User;
import com.revature.repositories.*;

public class UserService {
	UserDAO repository = new UserDAOImpl();
	
	public List<User> findAllActive(){
		return repository.findAllActive();
	}
	
	public List<User> findAllPending(){
		return repository.findAllPending();
	}
	
	public User findUser(String name) {
		return repository.findUser(name);
	}
	
	public boolean insert(User u) {
		return repository.insert(u);	
	}
	
	public boolean delete(User u) {
		return repository.delete(u);
	}
	
	public boolean approveClient(User u, Account a) {
		return repository.approveClient(u, a);
	}
	
	public boolean approveEmployee(User e) {
		return repository.approveEmployee(e);
	}
	
	public List<User> findAllUsers() {
		return repository.findAllUsers();
	}
	
	public boolean updatePassword(User u, String newVal) {
		return repository.updatePassword(u, newVal);
	}
	public boolean updateRole(User u, char c) {
		return repository.updateRole(u, c);
	}
	public boolean denyUser(User u) {
		return repository.denyUser(u);
	}
	public boolean setPending(User u) {
		return repository.setPending(u);
	}
	public boolean updateAccount(User u, Account a) {
		return repository.updateAccount(u, a);
	}
	public boolean updateUsername(User u, String newVal) {
		return repository.updateUsername(u, newVal);
	}
	public boolean detachAccount(User u) {
		return repository.detachAccount(u);
	}
}
