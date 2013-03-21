package com.sourcefish.tools;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	private static final long serialVersionUID = 6456232544039240766L;
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
