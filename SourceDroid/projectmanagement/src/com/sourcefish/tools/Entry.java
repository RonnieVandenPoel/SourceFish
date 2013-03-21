package com.sourcefish.tools;

import java.io.Serializable;
import java.sql.Timestamp;

public class Entry implements Serializable {

	private static final long serialVersionUID = 2435417523458057675L;
	
	public Timestamp start;
	public Timestamp end;
	public String description;
	public User u;	
	public String entryid;
	
	public Entry() {
		
	}
	
	public Entry(Timestamp start, String description, User u, String entryid) {
		this.start =start;
		this.description = description;
		
		this.u = u;
		this.entryid = entryid;
	}
	
	public Entry(Timestamp start, String description, Timestamp end, User u, String entryid) {
		this.end = end;
		this.start =start;
		this.description = description;
		
		this.u = u;
		this.entryid = entryid;
	}
	
	
	public String toString() {
		String s = entryid + " - " + description + " - " + u.username + start.toString();
		if (isOpen()) {
			s = entryid + " - " + description + " - " + u.username + start.toString() + " OPEN ENTRY";
		}
		return s;
	}
	
	public boolean isOpen() {
		if (end == null){
			return true;
			}
		else {
		 return false;
		}
	}
}
