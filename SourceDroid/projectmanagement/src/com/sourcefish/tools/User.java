package com.sourcefish.tools;

import java.util.Date;

public class User {
	public String username;		
	public int rechten;
	
	private String[] rechtennamen = {"Creator","Admin","Medewerker"}; 
	
	public User() {
		
	}
	public User(String username, int rechten) {
		this.username = username;
		this.rechten = rechten;
	}
}
