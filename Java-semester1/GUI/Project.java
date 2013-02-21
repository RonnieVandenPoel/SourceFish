package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

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
	private User user;
	public int id;
	public String projectName;
	public String customer;
	public Timestamp startDate;
	public Timestamp endDate;
	public String description;
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Entry> entries = new ArrayList<Entry>();
	
	public Project(String projectName,String customer,String description,User u){
		this.projectName = projectName;
		this.customer = customer;
		this.description = description;
		
		
	}
	
	public Project(String projectName,String customer,String description, ArrayList<User> users,int id){
		this.projectName = projectName;
		this.customer = customer;
		this.description = description;
		this.users= users;
		this.id = id;
	}
	
	public Project(String naam, String klant, String start, String descriptie, String id) {
		this.projectName = naam;
		this.customer = klant;
		this.startDate = Timestamp.valueOf(start);
		this.description = descriptie;
		this.id = Integer.parseInt(id);		
	}
	
	
	//aanmaken van een project obj
	public Project(int id,User u, JFrame frame ){
		this.setUser(u);
		this.id=id;
		users= new ArrayList<User>();
		getData(frame, this.user);
		setUsersInProject(frame,u);
		System.out.println("ponies r cool");
	}	
	
	public Project(int id,User u){
		this.setUser(u);
		this.id=id;
		
	}	
	
	public boolean setUsersInProject(JFrame frame,User u) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check =false;
		this.entries = new ArrayList<Entry>();
		DefaultHttpClient client= u.getClient();
		HttpGet getRequest = new HttpGet(
				"http://"+ Methodes.getIp() +"/webservice/getUsersInProject/" + id);
		try{			
			HttpResponse resp=client.execute(getRequest);
		//uid,uname,voonaam,achternaam,			
    	BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    	String output = "";
    	while ((output = rd.readLine()) != null) {
    		System.out.println(output);
    		JSONObject object = new JSONObject(output);
    		JSONArray array = object.optJSONArray("users");
    		for(int i = 0;i< array.length();i++){
    			JSONObject json = array.getJSONObject(i);
    			int uid = json.getInt("uid");
    			String username = json.getString("uname");
    			String firstname = json.getString("voornaam");
    			String name = json.getString("achternaam");
    			User usr= new User(uid,firstname,name);
    			usr.setUsername(username);
    			if(user == null)
    				user = u;
    			usr.getRightsFromDB(frame, this.id,user);
    			users.add(usr);
    			System.out.println(user);
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

	public boolean getData(JFrame frame, User u) {		
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check =false;
		this.entries = new ArrayList<Entry>();
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(u.getUsername(),u.getPassword());
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authprefs);
		HttpGet getRequest = new HttpGet(
				"http://"+ Methodes.getIp() +"/webservice/getProjectById/" + id);
		try{			
			HttpResponse resp=client.execute(getRequest);
		//Date start, Date end, String description,Project p			
    	BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    	String output = "";
    	while ((output = rd.readLine()) != null) {
    		System.out.println(output);
    		JSONObject jsonObject = new JSONObject(output);
    		
			this.projectName = jsonObject.getString("projectnaam");
			this.customer = jsonObject.getString("opdrachtgever");
			this.setStartDate(Timestamp.valueOf(jsonObject.getString("begindatum")));						
			description = jsonObject.getString("omschrijving");
    		
    	}

    	} catch (IOException | JSONException e) {
    		e.printStackTrace();
    	}
    finally{
	client.getConnectionManager().shutdown();		
    }
		return check;
	}
	
	public boolean getEntries(User u, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check =false;
		
		DefaultHttpClient client= u.getClient();
		HttpGet getRequest = new HttpGet(
				"http://"+ Methodes.getIp() +"/webservice/getProjectById/" + id);
		try{			
			HttpResponse resp=client.execute(getRequest);
		//Date start, Date end, String description,Project p			
    	BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    	String output = "";
    	boolean oneLoop = true;
    	while ((output = rd.readLine()) != null) {
    		if (oneLoop) {
    		Synchronisatie.setOfflineEntries(this.id, output, this.user.username);
    		}
    		oneLoop = false;
    		System.out.println(output);
    		JSONObject jsonObject = new JSONObject(output);
    		JSONArray array = jsonObject.optJSONArray("entries");
    		for(int i = 0; i< array.length(); i++){
    			JSONObject entry = (JSONObject)array.get(i);
    			String description= entry.getString("notities");
    			String name = entry.getString("achternaam");
    			String firstname = entry.getString("voornaam");
    			
    			String begin = entry.getString("begin");
    			String eind = entry.getString("eind");
    			
    			System.out.println(eind);
    			
    			Date start =Timestamp.valueOf(begin);
    			Date end;
    			if (eind.equals("0000-00-00 00:00:00")) {
    				end = null;
    			}
    			else {
    				
    				end =Timestamp.valueOf(eind);
    			}
    			int uid = entry.getInt("uid");
    			String trid = entry.getString("trid");
    			//int rid = entry.getInt("rid");
    			if(entries.add(new Entry(start, end, description, u,this.id,trid)))
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

	public boolean adduser(User u,User add, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
		users.add(add);
		DefaultHttpClient client= u.getClient();
	    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/addProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + id + "\",\"username\":\"" + add.getUsername()+ "\"}");
	    	create.setContentType("/application/json");
	    	post.setEntity(create);
	    	System.out.println("{\"pid\":\"" + id + "\",\"username\":\"" + add.getUsername()+ "\"}");
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
	
	public boolean removeUser(User u,User remove, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
		users.add(remove);
		DefaultHttpClient client= u.getClient();
	    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/removeProjectUser");
	    try {
	    	//projectname, client ,summary
	    	StringEntity create = new StringEntity("{\"pid\":\"" + id + "\",\"username\":\"" + remove.getUsername()+ "\"}");
	    	create.setContentType("/application/json");
	    	post.setEntity(create);
	    	System.out.println("{\"pid\":\"" + id + "\",\"username\":\"" + remove.getUsername()+ "\"}");
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
	
	public boolean create(User u, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
		DefaultHttpClient client= u.getClient();
	    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/addProject");
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
	
	public boolean deleteProject(User u, JFrame frame){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;		
		
		DefaultHttpClient client= u.getClient();
	    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/deleteProject");
	    try {
	    	
	    	StringEntity delete = new StringEntity("{\"pid\":\"" + id + "\"}");
	    	
	    	delete.setContentType("application/json");
	    	post.setEntity(delete);
	    	
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
	    	if(check) {
	    		Home.main(null, u);
	    		frame.dispose();
	    		
	    	}
		client.getConnectionManager().shutdown();		
	    }
	    return check;
	}
	
	public boolean closeProject(User u, Date d, JFrame frame){//sends end time for last open entry
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		boolean check = false;
		this.endDate= new Timestamp(d.getTime());
		
		DefaultHttpClient client= u.getClient();
	    HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/closeProject");
	    try {
	    	
	    	StringEntity create = new StringEntity("{\"end\":\"" + endDate + "\",\"pid\":\"" + id + "\"}");
	    	
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
	
	
	
	public ArrayList<User> getUsers() {
		return this.users;
	}

	public String getID(){
		return "" + id;			
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
	
	public ArrayList<Entry> getAllEntriesInList(){
		return entries;
	}
	@Override public String toString() {
		return projectName;
	}
	public void setUser(User u) {
		this.user = u;
	}
}
