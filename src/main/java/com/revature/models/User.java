package com.revature.models;

public class User {
	//private int id;
	private String username;
	// password is a String of sha256 encryption
	private String password;
	private String role;
	private int approvalStatus;
	private int account_id;
	
	public User(String username, String password, String role, int approvalStatus, int account_id) {
		super();
		//this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.approvalStatus = approvalStatus;
		this.account_id = account_id;
	}
	public User(String username, String password, String role, int approvalStatus) {
		super();
		//this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.approvalStatus = approvalStatus;
		this.account_id = -1;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + account_id;
		result = prime * result + approvalStatus;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (account_id != other.account_id)
			return false;
		if (approvalStatus != other.approvalStatus)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", role=" + role + ", approvalStatus="
				+ approvalStatus + ", account_id=" + account_id + "]";
	}
	
	

	
}