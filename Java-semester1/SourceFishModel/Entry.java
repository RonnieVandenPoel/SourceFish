package SourceFishModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Entry {
Timestamp start;
Timestamp end;
String description;
Project p;
User u;
int id;

public Entry(Date start, Date end, String description,Project p,User u,int id){
	this.start = new Timestamp(start.getTime());
	if(end!=null)
	this.end = new Timestamp(end.getTime());
	this.description = description;
	this.p = p;
	this.u = u;
	this.id = id;
}

public Entry(Date start, Date end, String description,Project p){
	this(start,end,description,p,null,0);
}
public Entry(Date start, String description, Project p){
	this(start,null,description,p);
}

public boolean create(User u){//creates new entry in db for project p from user u with start and end time
	boolean check = false;
	
	DefaultHttpClient client = new DefaultHttpClient();
	Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
	client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
	List<String> authprefs=new ArrayList<String>(1);
	authprefs.add(AuthPolicy.DIGEST);
	client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/newEntry");
    try {
    	
    	StringEntity create = new StringEntity("{\"begin\":\"" + start  + "\",\"notities\":\"" + description + "\",\"pid\":\"" + p.getID() + "\"}");
    	
    	create.setContentType("application/json");
    	post.setEntity(create);
    	
    	HttpResponse response = client.execute(post);
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		System.out.println(line);
    		JSONObject jsonObject = new JSONObject(line);
    		int id = jsonObject.getInt("trid");
    		System.out.println(id);
    		setId(id);
    		check = true;
    	}

    	} catch (IOException | JSONException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
    return check;
    
}

public boolean delete(User u){
	boolean check = false;
	
	DefaultHttpClient client = new DefaultHttpClient();
	Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
	client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
	List<String> authprefs=new ArrayList<String>(1);
	authprefs.add(AuthPolicy.DIGEST);
	client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/deleteEntry");
    try {
    	
    	StringEntity remove = new StringEntity("{\"trid\":\"" + id  + "\"}");
    	
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


public boolean closeEntry(User u, Date d){//sends end time for last open entry
	boolean check = false;
	this.end= new Timestamp(d.getTime());
	
	DefaultHttpClient client = new DefaultHttpClient();
	Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
	client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
	List<String> authprefs=new ArrayList<String>(1);
	authprefs.add(AuthPolicy.DIGEST);
	client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/closeEntry");
    try {
    	
    	StringEntity create = new StringEntity("{\"eind\":\"" + end + "\",\"pid\":\"" + p.getID() + "\"}");
    	
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

public String toString(){
	return u.getFirstname()+" "+ u.getName()+" started at: "+ start+" , ended at: "+ end +" and did: "+ description;
	
}
}
