package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class User {

	
	String username;
	String firstname;
	String name;
	String password;
	String email;
	Date registrationDate;
	int rechten;
	int id;
	private String[] rechtennamen = new String[3];
	
	
	public User(String username,String password,String firstname,String name,String email)
	{
		this.username=username;
		this.firstname = firstname;
		this.name = name;
		this.email = email;				
		this.password = password;
		setRechtenNamen();
	}
	public User(String username,String password){ 
		this.username = username;		
		this.password = password;
		setRechtenNamen();
	}
	
	public User(int id, String firstname,String name){
		this.id = id;
		this.firstname = firstname;
		this.name = name;
		setRechtenNamen();
		
	}
	
	private void setRechtenNamen() {
		rechtennamen[0] = "Creator";
		rechtennamen[1] = "Admin";
		rechtennamen[2] = "employee";
	}
	
	public String getPassword(){
		return password;
	}
	
	public DefaultHttpClient getClient(){
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
		return client;
		
	
	}
	
	public boolean register(){
		
		DefaultHttpClient client=new DefaultHttpClient();
	    HttpPost post = new HttpPost("http://" + Methodes.getIp() +"/register/registerUser");
	    try {
	    	StringEntity registreerstring = new StringEntity("{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\",\"firstname\":\"" + firstname + "\",\"lastname\":\"" + name + "\"}");
	    	registreerstring.setContentType("/application/json");
	    	post.setEntity(registreerstring);
	    	
	    	HttpResponse response = client.execute(post);
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		System.out.println(line);
	    		return false;
	    	}

	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    
	    return true;
	}
	
	public boolean promoteUser(Project p,User u, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
	
		DefaultHttpClient client= getClient();
	    HttpPost post = new HttpPost("http://" + Methodes.getIp() +"/webservice/promoteProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + p.getID() + "\",\"username\":\"" + u.getUsername()+ "\"}");
	    	create.setContentType("/application/json");
	    	post.setEntity(create);
	    	
	    	HttpResponse response = client.execute(post);
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		System.out.println(line);	    		
	    		check = false;
	    	}

	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    finally{
		client.getConnectionManager().shutdown();		
	    }
		return check;
	}
	
	public boolean demoteUser(Project p,User demote, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
	
		DefaultHttpClient client= getClient();
	    HttpPost post = new HttpPost("http://" + Methodes.getIp() +"/webservice/demoteProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + p.getID() + "\",\"username\":\"" + demote.getUsername()+ "\"}");
	    	create.setContentType("/application/json");
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
	
	public boolean getRightsFromDB(JFrame frame, int pid,User u) {
		boolean check = false;
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		System.out.println(u);
		
		DefaultHttpClient client= u.getClient();
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/getRid/"+pid+"/"+this.username);
		try{
		HttpResponse resp = client.execute(getRequest);
		
		
		BufferedReader br  = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		
		String output="";
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		
			while ((output = br.readLine()) != null) {	
				System.out.println(output);
				JSONObject message = new JSONObject(output);
				this.rechten = Integer.parseInt(message.getString("rid"));								
				check = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		    }
		
		
		return check;
	}
	
	/* public void getIdFromDb(JFrame frame) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		String naam = this.username;
		String passwoord = this.password;
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(naam,passwoord);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List authprefs=new ArrayList(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	
		
		
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/getUserId/");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (Exception e1) {
			
			
			e1.printStackTrace();	
			
		}
		//System.out.println(resp);
		
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		try {
			while ((output = br.readLine()) != null) {				
					try {
						System.out.println(output);
						JSONObject message = new JSONObject(output);
						this.id = Integer.parseInt(message.getString("uid"));					
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		    }
	} */
	
	
	public void fillUserData(JFrame frame) {	
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		
		DefaultHttpClient client=getClient();
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/getUser/0");
		try{
		HttpResponse resp = client.execute(getRequest);
		
		BufferedReader br = new BufferedReader(   new InputStreamReader((resp.getEntity().getContent())));
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
			while ((output = br.readLine()) != null) {		
						System.out.println(output);
						JSONObject message = new JSONObject(output);
						this.email = message.getString("email");
						this.name = message.getString("achternaam");
						this.firstname = message.getString("voornaam");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		finally{
			client.getConnectionManager().shutdown();		
		    }		
	}
	
	public String getUsername(){
		return username;
	}
	public void setUsername(String s){
		username=s;
	}
	
	public String getFirstname(){
		return firstname;
	}
	public void setFirstname(String s){
		firstname=s;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String s){
		name=s;
	}
	
	public String getEmail(){
		return email;
	}
	public void setEmail(String s){
		email=s;
	}
	
	public int getRechten(){
		return rechten;		
	}
	public void setRechten(int i){
		rechten = i;
	}
	
	public @Override String toString(){
		if (rechten == 0) {
		return firstname + " " + name;
		}
		else {
			return firstname + " " + name + " - " + rechtennamen[rechten-1];
		}
	}
}
