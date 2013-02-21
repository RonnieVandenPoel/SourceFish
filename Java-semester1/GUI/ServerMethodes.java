package gui;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerMethodes {
	
	
	/*
	 * functions for gui.Home
	 */
	public static void setLatestProject(JLabel lblLaatsteProject, JButton btnOpenLaatste, User user ,JFrame frame,ArrayList<Project>projecten) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		String pid ="";
		DefaultHttpClient client =user.getClient();
	
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/getLastProject");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block			
			e1.printStackTrace();	
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login menu.");			
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
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
						pid = message.getString("pid");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		finally{
			client.getConnectionManager().shutdown();		
		    }			
		for (Project proj : projecten) {
			if (proj.getID().equals(pid)) {
				lblLaatsteProject.setText(proj.projectName);
				btnOpenLaatste.setEnabled(true);
				Home.setLatestpid(pid);
				break;
			}
		}	
	}
	
	public static void setProjecten(User user ,JFrame frame,ArrayList<Project>projecten) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame};
			Methodes.Disconnect(frames, "Connectie verloren, terug naar login scherm");			
		}
		DefaultHttpClient client =user.getClient();		
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/getProjects");
		try{
		HttpResponse resp = client.execute(getRequest);
		BufferedReader br = new BufferedReader(new InputStreamReader((resp.getEntity().getContent())));		
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		boolean oneLoop = true;
			while ((output = br.readLine()) != null) {	
				if (oneLoop) {
					Synchronisatie.setOfflineProjects(output, user.username);
					oneLoop = false;
				}
					JSONArray array = new JSONArray(output);
					for (int i = 0; i < array.length(); i++) {						
						JSONObject project = new JSONObject(array.get(i).toString());
						System.out.println(project.getString("pid"));
						String pid = project.getString("pid");
						String projectnaam = project.getString("projectnaam");
						String opdrachtgever = project.getString("opdrachtgever");
						String begindatum = project.getString("begindatum");						
						String omschrijving = project.getString("omschrijving");						
						Project projectentry = new Project(projectnaam,opdrachtgever,begindatum,omschrijving,pid);
						projecten.add(projectentry);
						}			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		    }		
	}
	/*
	 * functions for gui.Rapportgui
	 */
	public static void createRapport(String[] uid ,Project project,String modus,User user) {
		// TODO Auto-generated method stub
				
				
				DefaultHttpClient client = user.getClient();
				 HttpPost post = new HttpPost("http://"+ Methodes.getIp() +"/webservice/showPDF");
				    try {
				 		JSONObject json = new JSONObject();
				    	json.put("uids",uid);
				    	json.put("pid", project.getID());
				    	
				    		json.put("modus", modus);
				    	
				    	System.out.println(json);
				    	
				    	StringEntity create = new StringEntity(json.toString());
				    	
				    	create.setContentType("application/json");
				    	post.setEntity(create);
				    	
				    	HttpResponse response = client.execute(post);
				    	
				    	
				  
				    	saveFile(response.getEntity().getContent(),project);
				   
				    
				    	if (Desktop.isDesktopSupported()) {
				    	    try {
				    	    	File myFile = new File(project.getProjectName()+".pdf");
				    	        Desktop.getDesktop().open(myFile);
				    	    } catch (IOException ex) {
				    	        // no application registered for PDFs
				    	    }
				    	}

				    	} catch (IOException | JSONException e) {
				    		e.printStackTrace();
				    	}
			finally{
					client.getConnectionManager().shutdown();
				}
	}
	public static void saveFile(InputStream is,Project project) throws IOException{

	      OutputStream os=new FileOutputStream(new File(project.getProjectName()+".pdf"));        
	      byte[] bytes = IOUtils.toByteArray(is);
	      os.write(bytes);
	      os.close();
	    }
	/*
	 * functions gui.login
	 */
	public static boolean testLogin(String naam, String passwoord) {
		boolean test = false;
		User user = new User(naam, passwoord);		
		DefaultHttpClient client=user.getClient();
		HttpGet getRequest = new HttpGet(
				"http://" + Methodes.getIp() + "/webservice/tryLogin");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		finally{
			client.getConnectionManager().shutdown();		
		    }	
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			test = false;
		}
		else {
			test = true;
		}
		return test;
	}
	/*
	 * functions gui.permissions
	 */
	public static boolean getUnenrolledUsersFromDB(JFrame frame,JFrame teSluitenFrame,User logged,Project project){
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame, teSluitenFrame};
			Methodes.Disconnect(frames, "Connection lost, returning to login");			
		}
		List<User> uList = new ArrayList<User>();
		boolean check =false;
		DefaultHttpClient client=logged.getClient();
		HttpGet getRequest = new HttpGet(
				"http://"+ Methodes.getIp() +"/webservice/getUsersOutProject/" + project.getID());
		try{			
			HttpResponse resp=client.execute(getRequest);
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
					User add=new User(uid,firstname,name);
					add.setUsername(username);
					uList.add(add);
					System.out.println(uList);
				}
			}
		} catch (IOException | JSONException e) {
    		e.printStackTrace();
    	}
		finally{
			Permissions.setuList(uList);
			client.getConnectionManager().shutdown();		
		}
			return check;
	}	
	/*
	 * functions gui.WijzigProject
	 */
	public static boolean updateProject(String json,JFrame frame,JFrame teSluitenFrame,User user) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame, teSluitenFrame};
			Methodes.Disconnect(frames, "Connection lost, returning to login");			
		}
		boolean test = false;
		DefaultHttpClient client = user.getClient();
	    HttpPost post = new HttpPost("http://" + Methodes.getIp() + "/webservice/changeProject");
	    try {
	    	StringEntity updatestring = new StringEntity(json);
	    	updatestring.setContentType("/application/json");
	    	post.setEntity(updatestring);
	    	
	    	HttpResponse response = client.execute(post);
	    	System.out.println(" begin of wijzig" + response.toString());
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		System.out.println(line);	 
	    		JSONObject message;
				try {
					message = new JSONObject(line);
					String ok = message.getString("msg");
					if(ok.equals("1 rijen aangepast")) {
						test = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					try {
						message = new JSONObject(line);
						@SuppressWarnings("unused")
						String ok = message.getString("error");
						Methodes.message("You have no permission to change this project.");
						test = true;
						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					e.printStackTrace();
				}
				
	    	}

	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    
	    finally{
			client.getConnectionManager().shutdown();		
		    }
	    System.out.println("end of wijzigen");
	    return test;
	}
	
	/*
	 * functions gui.WijzigUser
	 */
	public static void updateUser(String json,User user,JFrame frame,JFrame teSluitenFrame) {
		if (!Methodes.testConnectie()) {
			JFrame[] frames = {frame, teSluitenFrame};
			Methodes.Disconnect(frames, "Connection lost, returning to login");			
		}	
		DefaultHttpClient client = user.getClient();
	    HttpPost post = new HttpPost("http://" + Methodes.getIp() + "/webservice/updateUser");
	    try {
	    	StringEntity updatestring = new StringEntity(json);
	    	updatestring.setContentType("/application/json");
	    	post.setEntity(updatestring);
	    	
	    	HttpResponse response = client.execute(post);
	    	System.out.println("wijzig" + response.toString());
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String line = "";
	    	while ((line = rd.readLine()) != null) {
	    		System.out.println(line);	    		
	    	}

	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    
	    finally{
			client.getConnectionManager().shutdown();		
		    }
	}

}
