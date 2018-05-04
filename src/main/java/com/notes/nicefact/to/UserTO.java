package com.notes.nicefact.to;

public class UserTO {

	String email;
	
	String name;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserTO [email=" + email + ", name=" + name + "]";
	}
	
}
