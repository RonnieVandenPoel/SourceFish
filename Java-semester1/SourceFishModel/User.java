package SourceFishModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class User {

	
	String username;
	String firstname;
	String name;
	String password;
	String email;
	Date registrationDate;
	int rechten;
	Boolean actief;
	int id;
	
	
	public User(String username,String password,String firstname,String name,String email)
	{
		this.username=username;
		this.firstname = firstname;
		this.name = name;
		this.email = email;				
		this.password = password;
	}
	public User(String username,String password){ 
		this.username = username;		
		this.password = password;
	}
	
	public User(int id, String firstname,String name){
		this.id = id;
		this.firstname = firstname;
		this.name = name;
		
	}
	
	public String getPassword(){
		return password;
	}
	
	public boolean login() throws ClientProtocolException, IOException{
		boolean result =false;
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
		HttpGet getRequest = new HttpGet(
				"http://projecten3.eu5.org/webservice/hello/" + username);
		
		HttpResponse resp=client.execute(getRequest);
				
		if(resp.getStatusLine().getStatusCode()==200)
		{
			
			result=true;
			
		}
		
		client.getConnectionManager().shutdown();
		
		
		return result;
	}

	public boolean register(){
		DefaultHttpClient client=new DefaultHttpClient();
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/register/registerUser");
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
	
	public boolean promoteUser(Project p,User promoter){
		boolean check = false;
	
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/promoteProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + p.getID() + "\",\"username\":\"" + promoter.getUsername()+ "\"}");
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
	
	public boolean demoteUser(Project p,User demote){
		boolean check = false;
	
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/demoteProjectUser");
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
	
	public String getUsername(){
		return username;
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
}
