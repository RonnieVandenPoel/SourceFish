package SourceFishModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Project {
	int id;
	String projectName;
	String customer;
	Timestamp startDate;
	Timestamp endDate;
	String description;
	List<User> users;
	List<Entry> entries = new ArrayList<Entry>();
	
	public Project(String projectName,String customer,String description,User u){
		this.projectName = projectName;
		this.customer = customer;
		this.description = description;
		users= new ArrayList<User>();
		users.add(u);
	}
	
	public Project(String projectName,String customer,String description, List<User> users,int id){
		this.projectName = projectName;
		this.customer = customer;
		this.description = description;
		this.users= users;
		this.id = id;
	}
	//test constructor
	public Project(int id,User u ){
		this.id=id;
		users= new ArrayList<User>();
		users.add(u);		
	}
	
	public boolean getEntries(User u){
		boolean check =false;
		this.entries = new ArrayList<Entry>();
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
		HttpGet getRequest = new HttpGet(
				"http://projecten3.eu5.org/webservice/getProjectById/" + id);
		try{			
			HttpResponse resp=client.execute(getRequest);
		//Date start, Date end, String description,Project p			
    	BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    	String output = "";
    	while ((output = rd.readLine()) != null) {
    		//System.out.println(output);
    		JSONObject jsonObject = new JSONObject(output);
    		JSONArray array = jsonObject.optJSONArray("entries");
    		for(int i = 0; i< array.length(); i++){
    			JSONObject entry = (JSONObject)array.get(i);
    			String description= entry.getString("notities");
    			String name = entry.getString("achternaam");
    			String firstname = entry.getString("voornaam");
    			Date start =Timestamp.valueOf(entry.getString("begin"));
    			Date end =Timestamp.valueOf(entry.getString("eind"));
    			int uid = entry.getInt("uid");
    			int trid = entry.getInt("trid");
    			//int rid = entry.getInt("rid");
    			if(entries.add(new Entry(start,end,description,this,new User(uid,firstname,name),trid)))
    				check = true;
    			//System.out.println(new Entry(start,end,description,this,new User(uid,firstname,name)));
    			
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

	public boolean adduser(User u,User add){
		boolean check = false;
		users.add(add);
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/addProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + id + "\",\"username\":\"" + add.getUsername()+ "\"}");
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
	
	public boolean removeUser(User u,User remove){
		boolean check = false;
		users.add(remove);
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/removeProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + id + "\",\"username\":\"" + remove.getUsername()+ "\"}");
	    	create.setContentType("/application/json");
	    	post.setEntity(create);
	    	
	    	HttpResponse response = client.execute(post);
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		System.out.println(line);
	    		check =removeUser(remove.getUsername());
	    		
	    	}

	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    finally{
		client.getConnectionManager().shutdown();		
	    }
	    return check;			
		}
	
	public boolean removeUser(String username){// methode verwijdert alleen een user in client niet db
		boolean result = false;
		User u;
		Iterator<User> it = users.iterator() ;
		if(it.hasNext()){
			do {
				u = it.next();
				if(u.getUsername() == username){
					it.remove();
					result = true;
				}
			}while(it.hasNext()&& !result);
		}
		return result;
	}
	
	public boolean create(User u){
		boolean check = false;
		
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
	    HttpPost post = new HttpPost("http://projecten3.eu5.org/webservice/addProject");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"projectname\":\"" + projectName + "\",\"client\":\"" + customer + "\",\"summary\":\"" + description + "\"}");
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
	public int getID(){
		return id;			
	}
	
	public String getProjectName(){
		return projectName;
	}
	public void setProjectName(String s){
		projectName = s;
	}
	
	public String getCustomer(){
		return customer;		
	}
	public void setCustomer(String s){
		customer = s;
	}
	
	public Timestamp getStartDate(){
		return startDate;
	}
	public void setStartDate(Timestamp d){
		startDate= d;
	}
	
	public Timestamp getEndDate(){
		return endDate;
	}
	public void setEndDate(Timestamp d){
		endDate= d;
	}
	
	public String getDescription(){
		return description;
	}
	public void setDescription (String s){
		description =s;
	}
	
	public List<Entry> getDeEntries(){
		return entries;
	}
}
