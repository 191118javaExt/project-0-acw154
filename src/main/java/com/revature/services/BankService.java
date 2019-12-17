package com.revature.services;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.digest.DigestUtils;

import com.revature.models.Account;
import com.revature.models.User;

public class BankService {
	AccountService as = new AccountService();
	Scanner sc = new Scanner(System.in);
	UserService us = new UserService();
	
	public String printAdminMenu() {	
		System.out.println("Admin Menu");
		System.out.println("1 - View All Users");
		System.out.println("2 - View Pending Users");
		System.out.println("3 - View User");
		System.out.println("4 - Approve User");
		System.out.println("5 - Modify User");
		System.out.println("6 - Delete User");
		System.out.println("7 - View All Accounts");
		System.out.println("8 - View Account");
		System.out.println("9 - Account Operations");
		System.out.println("Q - Quit");
		return sc.nextLine();
	}
	
	public void printUserList(List<User> list) {
		if(list.isEmpty()) {
			System.out.println("No Users fit the requirements");
		} else {
			System.out.println(String.format("%-20s%-20s%-12s%-12s%-6s", "Username", "Password", "Role", "Status", "Account ID"));
			for (User u : list) {
				String status;
				String accountNum;
				if(u.getApprovalStatus() > 0) {
					status = "Approved";
					accountNum = Integer.toString(u.getAccount_id());
				} else if(u.getApprovalStatus() == 0){
					status = "Pending";
					accountNum = "N/A";
				} else {
					status = "Denied";
					accountNum = "N/A";
				}
			
			System.out.println(String.format("%-20s%-20s%-12s%-12s%-6s", u.getUsername(),
					u.getPassword().substring(0, 10) + "...", u.getRole(), status, accountNum));
			}
		}
	}
	
	public void printUser(User u) {
		System.out.println(String.format("%-20s%-20s%-12s%-12s%-6s", "Username", "Password", "Role", "Status", "Account ID"));
		String status;
		String accountNum;
		if(u.getApprovalStatus() > 0) {
			status = "Approved";
			accountNum = Integer.toString(u.getAccount_id());
		} else if(u.getApprovalStatus() == 0){
			status = "Pending";
			accountNum = "N/A";
		} else {
			status = "Denied";
			accountNum = "N/A";
		}
		System.out.println(String.format("%-20s%-20s%-12s%-12s%-6s", u.getUsername(),
				u.getPassword().substring(0, 10) + "...", u.getRole(), status, accountNum));
	}
	
	public void printAllAccounts(List<Account> list) {
		if(list.isEmpty()) {
			System.out.println("No accounts fit the requirements");
		} else {
			System.out.println(String.format("%-15s%-35s%-20s", "Account ID", "Balance", "# of Transactions"));
			for (Account a : list) {
				String id = Integer.toString(a.getAccount_id());
				String bal = Double.toString(a.getBalance());
				String tCounter = Integer.toString(a.getTransCounter());
				System.out.println(String.format("%-15s%-35s%-20s", id, bal, tCounter));
			}
		}
	}
	
	public void printAccount(Account a) {
		System.out.println(String.format("%-15s%-35s%-20s", "Account ID", "Balance", "# of Transactions"));
		String id = Integer.toString(a.getAccount_id());
		String bal = Double.toString(a.getBalance());
		String tCounter = Integer.toString(a.getTransCounter());
		System.out.println(String.format("%-15s%-35s%-20s", id, bal, tCounter));
	}
	
	public int userApproval(User u) {
		if(u.getApprovalStatus() > 0) {
			System.out.println("User has already been approved");
			return -2;
		} else {
			if(u.getRole().equals("Client")) {
				int curr_id;
				if(as.getAllAccounts().isEmpty()) {
					curr_id = 1000;
				} else {
					curr_id = as.getNextIDInSequence();
				}
				if(curr_id != -1 && u.getAccount_id() <= 0) {
					Account a = new Account(curr_id, 0.0, 0);
					as.insert(a);
					if(us.approveClient(u, a)) {	
						System.out.println("Successfully approved Client " + u.getUsername() + " with Account ID " + curr_id );
						return 1;
					} else {
						as.delete(a);
						System.out.println("Error: Client approval failed");
						return -1;
					}
				} else {
					System.out.println("Account Approval failed");
					return -1;
				}
			} else {
				us.approveEmployee(u);
				System.out.println("Employee Approved");
			}
		}
		return 0;
	}
	
	public int userDenial(User u) {
		if(us.denyUser(u)) {
			System.out.println("User has been denied");
			return 1;
		} else {
			System.out.println("Unable to deny user");
			return -1;
		}
	}
}
