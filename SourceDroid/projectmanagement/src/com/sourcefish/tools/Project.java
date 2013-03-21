package com.sourcefish.tools;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Project implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6707663194519826253L;
	
	public String name;
	public int id;
	public String customer;
	public Timestamp startDate;
	public Timestamp endDate;
	public String description;
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Entry> entries = new ArrayList<Entry>();
	
	public Project() {
		//leeg project
	}
	
	public Project(String name, int id, String customer, Timestamp startDate, Timestamp endDate, String description, ArrayList<User> users, ArrayList<Entry> entries) {
		this.name = name;
		this.id = id;
		this.customer = customer;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.users = users;
		this.entries = entries;
	}
}
