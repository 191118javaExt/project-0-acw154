package com.revature;

import java.util.List;
import java.sql.Connection;
import java.text.DecimalFormat;

import com.revature.repositories.*;
import com.revature.services.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import com.revature.models.*;
public class Bank {
	private User current;
	private Account currAccount;
	private boolean quit = false;
	private boolean loggedIn = false;
	UserService us = new UserService();
	AccountService as = new AccountService();
	
	public Bank() {
		super();
	}

	public void runInstance() {
		while(!quit) {
			if(!loggedIn) {
				loggedIn = displayLogIn();
			}else if(loggedIn && current.getApprovalStatus() <= 0){
				current = null;
				System.out.println("User is not approved.");
				loggedIn = displayLogIn();

			} else {
				switch(current.getRole()) {
				case "Client": {
					if(current.getApprovalStatus() > 0) {
						currAccount = as.getAccount(current.getAccount_id());
						mainMenu();
					}
					break;
				}
				case "Employee": {
					employeeMenu();
					break;
				}
				case "Admin": {
					adminMenu();
					break;
				}
				default : {
					System.out.println("Role does not Exist.");
					break;
				}
				}
				quit = true;
			}
		
		}
		System.out.println("Exiting Banking Application");
	}
	
	public boolean verifyLogIn(String user, String pass) {
		User temp = us.findUser(user);
		String hex_password = DigestUtils.sha256Hex(pass);
		if (temp != null && temp.getPassword().equals(hex_password)) {
			return true;
		}
		return false;
	}
	
	public boolean checkUser(String user) {
		List<User> users = us.findAllUsers();
		for (User u: users) {
			if(u.getUsername().equalsIgnoreCase(user)) {
				return true;
			}
		}
		return false;
	}
	
	public void delay() {
		try
		{
		    Thread.sleep(2000);
		}
		catch(InterruptedException ex)
		{
		    Thread.currentThread().interrupt();
		}
	}
	public void mainMenu() {
		boolean terminated = false;
		Scanner sc = new Scanner(System.in);
		String input;
		while(terminated != true) {
			
			System.out.println("Welcome " + current.getUsername());
			System.out.println("Main Menu");
			System.out.println("1 - View Balance");
			System.out.println("2 - Withdraw funds");
			System.out.println("3 - Deposit funds");
			System.out.println("4 - Transfer funds");
			System.out.println("Q - Log Out");
			input = sc.nextLine();
			switch(input.toUpperCase()) {
			case "1": {
				System.out.println("----------------");
				System.out.println("Your balance is " + as.getBalance(currAccount));
				System.out.println("-------------------");
				delay();
				break;
			}
			case "2": {
				System.out.println("----------------");
				System.out.println("How much money would you like to withdraw?");
				try {
					double val = moneyFormatter(sc.nextDouble());
					if(val < 0) {
						System.out.println("Input must be a non-negative number");
					} else if(Double.toString(val).length() > 15) {
						System.out.println("Suspicious withdrawal amount. Alerting the authorities");
					} else if(as.withdraw(currAccount, val)) {
						System.out.println("Transaction Successful. Your account balance is now: $" + as.getBalance(currAccount));
					} else {
						System.out.println("Withdrawal amount cannot exceed account balance");
					}
				} catch (InputMismatchException e) {
					System.out.println("Withdrawal amount must be in decimal format");
				}
				System.out.println("-------------------");
				delay();
				sc.nextLine();
				break;
			}
			case "3": {
				System.out.println("----------------");
				System.out.println("How much money would you like to deposit?");
				try{
					double val = moneyFormatter(sc.nextDouble());
					if(val < 0) {
						System.out.println("Input must be a non-negative number");
					} else if(Double.toString(val).length() > 15) {
						System.out.println("Suspicious deposit amount. Alerting the authorities");
					} else if(as.deposit(currAccount, val)) {
						System.out.println("Deposited " + val);
					}
				} catch (InputMismatchException e) {
					System.out.println("Deposit amount must be in decimal format");
				}
				System.out.println("-------------------");
				delay();
				sc.nextLine();
				break;
			}
			case "4": {
				System.out.println("----------------");
				System.out.println("What account ID are you transferring to?");
				try{
					int otherID = sc.nextInt();
					Account other = as.getAccount(otherID);
					if(other != null) {
						System.out.println("How much money would you like to transfer?");
						double val = moneyFormatter(sc.nextDouble());
						if(val < 0) {
							System.out.println("Input must be a non-negative number");
						} else if(Double.toString(val).length() > 15) {
							System.out.println("Suspicious transfer amount. Alerting the authorities");
						} else if(val <= as.getBalance(currAccount)) {
					
							as.transfer(currAccount, other, val);
						} else {
							System.out.println("Account balance cannot be less than transfer amount");
						}
					} else {
						System.out.println("Account with ID " + otherID + " does not exist");
					}
				} catch (InputMismatchException e) {
					System.out.println("Deposit amount must be in decimal format");
				}
				System.out.println("-------------------");
				delay();
				sc.nextLine();
				break;
			}
			case "Q": {
				terminated = true;
				break;
			}
			default : {
				System.out.println("Invalid Choice");
				break;
			}
			}
			
		}
		
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
	
	
	
	public void employeeMenu() {
		//us.findAllPending()
		boolean terminated = false;
		do {
			Scanner sc = new Scanner(System.in);
			System.out.println("Welcome " + current.getUsername());
			System.out.println("Employee Menu");
			System.out.println("1 - View All Users");
			System.out.println("2 - View Pending Users");
			System.out.println("3 - View Specific User");
			System.out.println("4 - Approve/Deny User");
			System.out.println("5 - View All Accounts");
			System.out.println("6 - View Specific Account");
			System.out.println("Q - Log Out");
			String input = sc.nextLine();
			switch(input.toUpperCase()) {
			case "1": {
				List<User> list = us.findAllUsers();
				printUserList(list);
				System.out.println("-------------------");
				delay();
				//format output of users
				break;
			}
			case "2":{
				List<User> list = us.findAllPending();
				printUserList(list);
				delay();
				break;
			}
			case "3":{
				System.out.println("Please enter the username of the User you would like to view");
				String name = sc.nextLine();
				if(checkUser(name)) {
					User u = us.findUser(name);
					printUser(u);
					System.out.println("-------------------");
					delay();
				} else {
					System.out.println("User not found.");
					System.out.println("-------------------");
					System.out.println("Press any key to continue");
					if(sc.nextLine() == "") {
						break;
					}
				}
				break;			
			}
			case "4":{
				User u;
				System.out.println("Please enter the exact username of the User you would like to approve/deny.");
				String name = sc.nextLine();
				if(checkUser(name)) {
					u = us.findUser(name);
					printUser(u);
					if(u.getRole().equals("Admin")) {
						System.out.println("You do not have permission to approve this User");
					} else {
						System.out.println("[A]pprove, [D]eny, or [Q]uit?");
						String choice = sc.nextLine().toUpperCase();
						switch(choice) {
						case "A":{
							if(u.getApprovalStatus() > 0) {
								System.out.println("User has already been approved");
								break;
							}
							if (u.getRole().equals("Employee")) {
								us.approveEmployee(u);
								System.out.println("Employee " + u.getUsername() + " Approved");
							} else if(u.getRole().equals("Client")){
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
									} else {
										as.delete(a);
										System.out.println("Error: Client approval failed");
										
									}
								} else {
									System.out.println("Client approval failed");
								}
							}
							break;
						}
						case "D":{
							if(us.denyUser(u)) {
								System.out.println("Successfully denied ");
							} else {
								System.out.println("Account was not successfully denied");
							}
							break;
						}
						case "Q":{
							break;
						}
						default:{
							System.out.println("Invalid Input");
							break;
						}
						}
					}
					
				} else {
					System.out.println("User not found.");			
				}
				break;
			}
			case "5": {
				printAllAccounts(as.getAllAccounts());
				System.out.println("-------------------");
				System.out.println("Press any key to continue");
				if(sc.nextLine() == "") {
					break;
				}
				break;
			}
			case "6": {
				System.out.println("Please enter the Account ID that you would like to view");
				try{
					int id = Integer.parseInt(sc.nextLine());
					Account a = as.getAccount(id);
					if(a != null) {
						printAccount(a);
						System.out.println("-------------------");
						System.out.println("Press any key to continue");
						if(sc.nextLine() == "") {
							break;
						}
					} else {
						System.out.println("Account does not exist");
						System.out.println("-------------------");
						System.out.println("Press any key to continue");
						if(sc.nextLine() == "") {
							break;
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid Account Input");
					break;
				}
				break;
				
			}
			case "Q":{
				terminated = true;
				break;
			}
			default:{
				System.out.println("Invalid Option");
				break;
			}
			}
		} while(!terminated);
	}
	
	public void adminMenu() {
		boolean terminated = false;
		
		
		do {
			Scanner sc = new Scanner(System.in);
			System.out.println("Welcome " + current.getUsername());
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
			// TODO: Modify User, Account fields, Perform account transactions
			System.out.println("Q - Quit");
			String input = sc.nextLine();
			switch(input.toUpperCase()) {
			case "1": {
				List<User> list = us.findAllUsers();
				printUserList(list);
				System.out.println("-------------------");
				delay();
				//format output of users
				break;
			}
			case "2":{
				List<User> list = us.findAllPending();
				printUserList(list);
				System.out.println("-------------------");
				delay();
				break;
			}
			case "3":{
				System.out.println("Please enter the username of the User you would like to view");
				String name = sc.nextLine();
				if(checkUser(name)) {
					User u = us.findUser(name);
					printUser(u);
					System.out.println("-------------------");
					delay();
				} else {
					System.out.println("User not found.");
					System.out.println("-------------------");
					System.out.println("Press any key to continue");
					if(sc.nextLine() == "") {
						break;
					}
				}
				break;			
			}
			case "4":{
				User u;
				System.out.println("Please enter the exact username of the User you would like to approve/deny.");
				String name = sc.nextLine();
				if(checkUser(name)) {
					u = us.findUser(name);
					printUser(u);
					System.out.println("[A]pprove, [D]eny, or [Q]uit?");
					String choice = sc.nextLine().toUpperCase();
					switch(choice) {
					case "A":{
						if(u.getApprovalStatus() > 0) {
							System.out.println("User has already been approved");
							break;
						}
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
								} else {
									as.delete(a);
									System.out.println("Error: Client approval failed");
									
								}
							} else {
								System.out.println("Account Approval failed");
							}
						
						} else {
							us.approveEmployee(u);
						}
						break;
					}
					case "D":{
						if(us.denyUser(u)) {
							System.out.println("Successfully denied ");
						} else {
							System.out.println("User was not successfully denied");
						}
						break;
					}
					case "Q":{
						break;
					}
					default:{
						System.out.println("Invalid Input");
						break;
					}
					}
				} else {
					System.out.println("User not found.");			
				}
				break;
			}
			case "5":{
				// Modify user
				System.out.println("What User would you like to modify?");
				String name = sc.nextLine();
				if(checkUser(name)) {
					User u = us.findUser(name);
					printUser(u);
					System.out.println("Which field would you like to modify");
					System.out.println("1 - Username");
					System.out.println("2 - Password");
					System.out.println("3 - Role");
					System.out.println("4 - Approval Status");
					System.out.println("5 - Associated Account ID");
					String choice = sc.nextLine();
					switch(choice) {
					case "1": {
						// Check if current Username is being operated on
						// Check if new Username already exists
						if(current.equals(u)) {
							System.out.println("Cannot change current User's username");
							break;
						}
						System.out.println("Enter the new username. Usernames must be between 4-20 characters in length.");
						String newUser = sc.nextLine();
						if(newUser.length() < 4 || newUser.length() > 20) {
							System.out.println("Username does not match requirements.");
							break;
						} else if(checkUser(newUser)) {
							System.out.println("A User with that username already exists.");
							break;
						} else {
							 if(us.updateUsername(u, newUser)) {
								 System.out.println("Username has been updated to " + newUser);
							 }  else {
								 System.out.println("Unable to change username");
							 }
							 
						}
						break;
					}
					case "2": {
						// Check if current Password is being operated on
						System.out.println("Enter the new password.");
						String newPass = sc.nextLine();
						if(current.equals(u)) {
							if(us.updatePassword(u, newPass)) {
								current.setPassword(DigestUtils.sha256Hex(newPass));
								System.out.println("Password changed to " + newPass);
							} else {
								System.out.println("Update password failed");
							}
						} else {
							if(us.updatePassword(u, newPass)) {
								System.out.println("Password changed to " + newPass);
							} else {
								System.out.println("Update password failed");
							}
						}
						break;
					}
					case "3": {
						// Sanitize Char input
						if(u.equals(current)) {
							System.out.println("Cannot change the role of the user in session");
							break;
						}
						System.out.println("What Role would you like to change the User to?");
						System.out.println("[C]lient --- [E]mployee --- [A]dmin");
						char newRole = sc.nextLine().toUpperCase().charAt(0);
						if(newRole == 'C' || newRole == 'A' || newRole == 'E') {
							if(us.updateRole(u, newRole)) {
								System.out.println("Role has been updated");
							} else {
								System.out.println("Unable to change role");
							}
						} else {
							System.out.println("Invalid Option");
						}
						break;
					}
					case "4": {
						if(u.equals(current)) {
							System.out.println("Cannot change the role of the user in session");
							break;
						}
						System.out.println("Change to: [D]enied, [P]ending, or [A]pproved?");
						System.out.println("[Q] to quit");
						String newStatus = sc.nextLine().toUpperCase().substring(0, 1);
						switch(newStatus) {
						case "D":{
							if(us.denyUser(u)){
								System.out.println("Approval Status changed to Denied");
							} else {
								System.out.println("Unable to change Approval Status");
							}
							break;
						}
						case "P":{
							if(u.getAccount_id() <= 0) {
								if(us.setPending(u)) {
									if(u.getAccount_id() > 0) {
										us.detachAccount(u);
									}
									System.out.println("Approval Status changed to Pending.");
								} else {
									System.out.println("Unable to change Approval Status");
								}
							}
							break;
						}
						case "A":{
							if(u.getApprovalStatus() > 0) {
								System.out.println("User has already been approved");
								break;
							}
							if(u.getRole().equals("Client")) {
								int curr_id = as.getNextIDInSequence();
								if(curr_id != -1 && u.getAccount_id() <= 0) {
									Account a = new Account(curr_id, 0.0, 0);
									if(us.approveClient(u, a)) {
										as.insert(a);
										System.out.println("Successfully approved Client " + u.getUsername() + " with Account ID " + curr_id );
									} else {
										System.out.println("Error: Client approval failed");
									}
								} else {
									System.out.println("Client Account already exists");
								}
							} else {
								if(us.approveEmployee(u)) {
									System.out.println("Employee Approved");
								} else {
									System.out.println("Unable to change Approval Status");
								}
							}
							break;
						}
						case "Q": {
							System.out.println("Leaving Approval Modification Menu");
							break;
						}
						}
						break;
					}
					case "5": {
						// Check if user is employee or client
						// Refuse if so
						if(u.getRole().equals("Client")) {
							System.out.println("Enter the new Account ID");
							try {
								int newID = Integer.parseInt(sc.nextLine());
								if(newID < 0) {
									System.out.println("Account ID must be a non-negative number");
									break;
								}
								Account a = as.getAccount(newID);
								if(a != null) {
									if(us.updateAccount(u, a)) {
										System.out.println("Account ID has been changed");
									} else {
										System.out.println("Unable to change Account ID");
									}
								} else {
									System.out.println("Account does not exist");
								}
							} catch (NumberFormatException e) {
								System.out.println("Account ID must be numerical");
								break;
							}
						} else {
							System.out.println("Non-Clients cannot have Accounts");
						}
						break;
					}
					case "Q": {
						System.out.println("Exiting Modify Menu");
						break;
					}
					default: {
						System.out.println("Invalid Input");
						break;
					}
					}
				} else {
					System.out.println("User not found.");			
				}
				break;
			}
			case "6": {
				System.out.println("What User would you like to delete? Deleting a Client will delete their associated account as well.");
				String name = sc.nextLine();
				if(checkUser(name)) {
					User u = us.findUser(name);
					if(current.getUsername().equals(u.getUsername())){
						System.out.println("Cannot delete current User");
						break;
					} else {
						printUser(u);
						Account a = as.getAccount(u.getAccount_id());
						if(us.delete(u)) {
							if(a != null) {
								as.delete(a);
							}
							System.out.println("User " + name + " deleted");							
						} else {
							System.out.println("Unable to delete user");
						}
					}
					
				} else {
					System.out.println("User does not exist");
				}
				break;
			}
			case "7": {
				printAllAccounts(as.getAllAccounts());
				System.out.println("-------------------");
				System.out.println("Press any key to continue");
				if(sc.nextLine() == "") {
					break;
				}
				break;
			}
			case "8": {
				System.out.println("Please enter the Account ID that you would like to view");
				try{
					int id = Integer.parseInt(sc.nextLine());
					Account a = as.getAccount(id);
					if(a != null) {
						printAccount(a);
						System.out.println("-------------------");
						System.out.println("Press any key to continue");
						if(sc.nextLine() == "") {
							break;
						}
					} else {
						System.out.println("Account does not exist");
						System.out.println("-------------------");
						System.out.println("Press any key to continue");
						if(sc.nextLine() == "") {
							break;
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid Account Input");
					break;
				}
				break;
				
			}
			case "9": {
				System.out.println("What Account would you like to modify?");
				try{
					int id = Integer.parseInt(sc.nextLine());
					Account a = as.getAccount(id);
					if(a != null) {
						System.out.println("Account Verified. Which Operation would you like to perform?");
						System.out.println("1 - Withdrawal");
						System.out.println("2 - Deposit");
						System.out.println("3 - Transfer");
						System.out.println("4 - Cancel Account");
						System.out.println("Q - Quit");
						String option = sc.nextLine().toUpperCase();
						switch(option) {
						case "1": {
							System.out.println("How much would you like to withdraw?");
							double amt = moneyFormatter(Double.parseDouble(sc.nextLine()));
							if(amt < 0) {
								System.out.println("Input must be a non-negative number");
							} else if(Double.toString(amt).length() > 15) {
								System.out.println("Suspicious withdrawal amount. Alerting the authorities. Admins are not above the law");
							} else if (amt > a.getBalance()) {
								System.out.println("Amount withdrawn must be less than or equal to account balance");
							} else {
								if(as.withdraw(a, amt)) {
									System.out.println("Withdrew " + amt + " from Account" + a.getAccount_id());
								} else {
									System.out.println("Unable to withdraw");
								}
							}
							System.out.println("-------------------");
							delay();
							sc.nextLine();
							break;
						}
						case "2": {
							System.out.println("How much would you like to deposit");
							double amt = moneyFormatter(Double.parseDouble(sc.nextLine()));
							if(amt < 0) {
								System.out.println("Input must be a non-negative number");
							} else if(Double.toString(amt).length() > 15) {
								System.out.println("Suspicious deposit amount. Alerting the authorities. Admins are not above the law");
							} else if(as.deposit(a, amt)) {
								System.out.println("Deposit of " + amt + " into Account " + a.getAccount_id() + " Successful");
							} else {
								System.out.println("Unable to deposit");
							}
							System.out.println("-------------------");
							delay();
							sc.nextLine();
							break;
						}
						case "3": {
							System.out.println("Enter the Account ID you would like to transfer to.");
							int to_id = Integer.parseInt(sc.nextLine());
							Account to = as.getAccount(to_id);
							System.out.println("Enter the amount you would like to transfer");
							double amt = moneyFormatter(Double.parseDouble(sc.nextLine()));
							if(amt < 0) {
								System.out.println("Input must be a non-negative number");
							} else if(Double.toString(amt).length() > 15) {
								System.out.println("Suspicious transfer amount. Alerting the authorities");
							} else if(amt > as.getBalance(a)) {
								System.out.println("The bank account you are transferring from does not have enough money");
							} else {
								if(as.transfer(a, to, amt)) {
									System.out.println("Transfer Successful. " + a.getAccount_id() + ": " 
								+ a.getBalance() + ", " + to.getAccount_id() + ": " + to.getBalance());
								} else {
									System.out.println("Unable to transfer");
								}
							}
							System.out.println("-------------------");
							delay();
							sc.nextLine();
							break;
						}
						case "4": {
							int holder = a.getAccount_id();
							if(as.delete(a)) {
								for(User user: us.findAllActive()) {
									if(holder == user.getAccount_id()) {
										us.detachAccount(user);
									}
								}
								System.out.println("Account " + holder + " deleted");	
							} else {
								System.out.println("Unable to delete");
							}
							break;
						}
						case "Q": {
							System.out.println("Quitting");
							break;
						}
						default: {
							System.out.println("Invalid Input");
							break;
						}
						}
						
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid Numerical Input");
					break;
				}
				break;
			}
			case "Q":{
				terminated = true;
				break;
			}
			default:{
				System.out.println("Invalid Option");
				break;
			}
			}
		} while(!terminated);
	}
	
	public boolean displayLogIn() {
		System.out.println("Welcome to the Banking App");
		System.out.println("-------------------------");
		Scanner sc = new Scanner(System.in);
		String input;
		boolean complete = false;
		do {	
			System.out.println("Please [L]og in or [R]egister. [Q] to Exit Application");
			input = sc.nextLine();
			switch(input.toUpperCase()) {
				case "L": {
					System.out.println("Please enter your username");
					String name = sc.nextLine();
					System.out.println("Please enter your password");
					String pass = sc.nextLine();
					if(verifyLogIn(name, pass)) {
							System.out.println("Verified.");
							current = us.findUser(name);
							complete = true;	
					} else {
						System.out.println("Username & Password combination is invalid");
					}
					break;
				}
				case "R": {
					System.out.println("Please enter a username. Usernames must be between 4-20 characters.");
					String name = sc.nextLine();
					if(name.length() < 4 || name.length() > 20) {
						System.out.println("Username does not match requirements.");
						break;
					}
					System.out.println("Please enter a password");
					String pass = sc.nextLine();
					if(!checkUser(name)) {
						String hex_password = DigestUtils.sha256Hex(pass);
						System.out.println("Is this an [E]mployee account, [C]lient account, or [A]dmin Account?");
						String role = sc.nextLine();
						if(role.equalsIgnoreCase("E")) {
							if(us.insert(new User(name, hex_password, "Employee", 0))) {
								System.out.println("Client account: " + name + " created.");
							} else {
								System.out.println("Unable to create user");
							}
							
						}
						if(role.equalsIgnoreCase("C")) {
							if(us.insert(new User(name, hex_password, "Client", 0))) {
								System.out.println("Client account: " + name + " created.");
							} else {
								System.out.println("Unable to create user");
							}
						}
						if(role.equalsIgnoreCase("A")) {
							if(us.insert(new User(name, hex_password, "Admin", 0))) {
								System.out.println("Client account: " + name + " created.");
							} else {
								System.out.println("Unable to create user");
							}
						}
					} else {
						System.out.println("Username already exists");
					}
					break;
				}
				case "Q": {
					System.out.println("Exiting Application");
					complete = true;
					quit = true;
					break;
				}	
				default : {
					System.out.println("Invalid Input");
					break;
				}		
			}
	
		} while (!input.equalsIgnoreCase("Q") && complete != true);
		if(current != null) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public double moneyFormatter(double d) {
		try {
			DecimalFormat df = new DecimalFormat("0.00");
			return Double.parseDouble(df.format(d));
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid Format for Money");
		}
		return -1;
	}
}
