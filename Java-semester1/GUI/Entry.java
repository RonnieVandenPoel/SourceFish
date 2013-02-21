package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.sql.Timestamp;
import javax.swing.JFrame;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Entry {
Timestamp start;
Timestamp end;
String description;
User u;
int id;
String entryid;


public Entry(Date start, Date end, String description,User u,int id){
	this.start = new Timestamp(start.getTime());
	if(end!=null)
	this.end = new Timestamp(end.getTime());
	this.description = description;	
	this.u = u;
	this.id = id;
}

public Entry(Date start, Date end, String description,User u,int id,String entryid){
	this.start = new Timestamp(start.getTime());
	if(end!=null)
	this.end = new Timestamp(end.getTime());
	this.description = description;	
	this.u = u;
	this.id = id;
	this.entryid = entryid;
}

public Entry(Date start, Date end, String description){
	this(start,end,description,null,0);
}
public Entry(Date start, String description){
	this.start = new Timestamp(start.getTime());
	
	this.description = description;
}

public boolean createManual(JFrame[] frame) {
	if (!Methodes.testConnectie()) {
		Methodes.Disconnect(frame, "Connectie verloren, terug naar login scherm");			
	}
	boolean check = false;
	
	DefaultHttpClient client= u.getClient();
    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/manualEntry");
    try {
    	
    	StringEntity create = new StringEntity("{\"begin\":\"" + start  + "\",\"notities\":\"" + description + "\",\"pid\":\"" + getId() + "\",\"eind\":\"" + end + "\"}");
    	
    	create.setContentType("application/json");
    	post.setEntity(create);
    	
    	HttpResponse response = client.execute(post);
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		System.out.println("createmanual: "+line);
    		JSONObject jsonObject = new JSONObject(line);
    		
    		if(jsonObject.has("trid")){
    			int id = jsonObject.getInt("trid");
    			setId(id);
    			check = true;
    		}
    	}

    	} catch (IOException | JSONException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
	
	return check;
}

public boolean create(User u, JFrame[] frame){//creates new entry in db for project p from user u with start and end time
	if (!Methodes.testConnectie()) {
		Methodes.Disconnect(frame, "Connectie verloren, terug naar login scherm");			
	}
	boolean check = false;
	
	DefaultHttpClient client= u.getClient();
    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/newEntry");
    try {
    	
    	StringEntity create = new StringEntity("{\"begin\":\"" + start  + "\",\"notities\":\"" + description + "\",\"pid\":\"" + getId() + "\"}");
    	
    	create.setContentType("application/json");
    	post.setEntity(create);
    	
    	HttpResponse response = client.execute(post);
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		System.out.println(line);
    		JSONObject jsonObject = new JSONObject(line);
    		
    		if(jsonObject.has("trid")){
    			int id = jsonObject.getInt("trid");
    			setId(id);
    			check = true;
    		}
    	}

    	} catch (IOException | JSONException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
    return check;
    
}

public boolean isOpen() {
	if (end == null){
		return true;
		}
	else {
	 return false;
	}
}

public boolean delete(User u, JFrame[] frame){
	if (!Methodes.testConnectie()) {
		Methodes.Disconnect(frame, "Connectie verloren, terug naar login scherm");			
	}
	boolean check = false;
	
	DefaultHttpClient client= u.getClient();
    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/deleteEntry");
    try {
    	
    	StringEntity remove = new StringEntity("{\"trid\":\"" + entryid  + "\"}");
    	
    	remove.setContentType("application/json");
    	post.setEntity(remove);
    	
    	HttpResponse response = client.execute(post);
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		System.out.println(line);
    		
    		check = true;
    	}

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
    return check;
    
}

static int checkOpenEntry(int pid,User u, JFrame[] frame){
	if (!Methodes.testConnectie()) {
		Methodes.Disconnect(frame, "Connectie verloren, terug naar login scherm");			
	}
	int trid = -1;
	DefaultHttpClient client= u.getClient();
	HttpGet getRequest = new HttpGet("http://"+ Methodes.getIp() +"/webservice/getOpenEntry/"+pid);    
	try{			
		HttpResponse resp=client.execute(getRequest);
	//Date start, Date end, String description,Project p			
	BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
	String output = "";
	while ((output = rd.readLine()) != null) {
		JSONObject jsonObject = new JSONObject(output);
		if(jsonObject.has("opentrid"))
			trid = jsonObject.getInt("opentrid");
		else
			trid = -1;
		
	}
	
	} catch (IOException | JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
finally{
		
	}

	return trid;
}


boolean closeEntry(User u, Date d, JFrame[] frame, int pid){//sends end time for last open entry
	if (!Methodes.testConnectie()) {
		Methodes.Disconnect(frame, "Connectie verloren, terug naar login scherm");			
	}
	boolean check = false;
	Timestamp end= new Timestamp(d.getTime());
	
	DefaultHttpClient client= u.getClient();
    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/closeEntry");
    try {
    	StringEntity create;
    	if(description == null)
    		create = new StringEntity("{\"eind\":\"" + end + "\",\"pid\":\"" + pid + "\"}");
    	else
    		create = new StringEntity("{\"eind\":\"" + end + "\",\"pid\":\"" + pid + "\",\"notities\":\""+description+"\"}");
    	
    	create.setContentType("application/json");
    	post.setEntity(create);
    	
    	HttpResponse response = client.execute(post);
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		System.out.println(line);	    		
    		check = true;
    	}

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
    return check;
    
}

public Timestamp getStart(){
	return start;
}
public void setStart(Timestamp d){
	start  = d;
}

public Timestamp getEnd(){
	return end;
}
public void setEnd(Timestamp d){
	end = d;
}

public String getDescription(){
	return description;
}
public void setDescription(String s){
	description = s;
}
public void setId(int i){
	id=i;
}
public int getId(){
	return id;
}

public String getIdToString () {
	return "" + id;
}

@Override public String toString() {
	String s = entryid + " - " + description + " - " + u.firstname + " " + u.name + " " + start.toString();
	if (isOpen()) {
		s = entryid + " - " + description + " - " + u.firstname + " " + u.name + " " + start.toString() + " OPEN ENTRY";
	}
	return s;
}

public String toDisplay(){
	if (isOpen()) {
		return "Name: " + u.getFirstname()+" "+ u.getName()+" \nStart: "+ start+" \nEnd: -  \nDescription: "+ description;
	}
	return "Name: " + u.getFirstname()+" "+ u.getName()+" \nStart: "+ start+" \nEnd: "+ end +" \nDescription: "+ description;
	
}
}
