package com.sourcefish.tools.io;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sourcefish.projectmanagement.ServerListenerInterface;
import com.sourcefish.tools.AsyncServerPosts;
import com.sourcefish.tools.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncServerSync extends AsyncTask<String, Integer, String> {
	Activity activity;
	Context context;
	private ProgressDialog dialog;
	private ServerListenerInterface parent;
	int teSyncen;
	public AsyncServerSync(Activity activity, Context context, int teSyncen) {
		this.activity = activity;
		this.context = context;
		this.parent = (ServerListenerInterface) activity;
		dialog = new ProgressDialog(activity);
		this.teSyncen = teSyncen;
	}
	
	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("Sync...");
        this.dialog.show();
    }
	
	protected void onPostExecute(String s)
	{
		if(dialog.isShowing())
			dialog.dismiss();
		
		parent.getServerResponse(s);
	}
	
	@Override
	protected String doInBackground(String... params) {
		/*
		//eerste nieuwe projecten, daarna edits, daarna entries .get checks voor async voor elke functie, vergeet niet de juiste pid op te slaan
		try {
				//String json = "{\"projectname\":\"" + name.getText().toString() + "\",\"client\":\"" + cust.getText().toString() + "\",\"summary\":\"" + desc.getText().toString() + "\"}";
				String json;
				
				SharedPreferences prefs = context.getSharedPreferences("data", 0);
				JSONArray array = new JSONArray(prefs.getString("json", "[]"));
				for (int i =0; i < array.length(); i++) {		
					JSONObject object = array.getJSONObject(i);
					if (object.getInt("online")>=0) {
						json = "{\"projectname\":\"" + object.getString("projectname") + "\",\"client\":\"" + object.getString("client") + "\",\"summary\":\"" + object.getString("description") + "\"}";
						AsyncServerPosts task = new AsyncServerPosts(context, Tasks.NEWPROJECT, activity);
						StringEntity entity = new StringEntity(json);
						task.execute(entity);
						JSONObject result = new JSONObject(task.get());				
						if (result.has("pid") && !(result.isNull("pid"))) {
							int pid = result.getInt("pid");
							// maak string entity met entries en slaag op in array
							//slaag alle entry objecten op in een arraylist
							ArrayList<JSONObject> entries = new ArrayList<JSONObject>();
							//zet json om in json array met entries
							JSONArray entryarray = object.getJSONArray("entries");
							for (int y = 0; y < entryarray.length(); y++ ) {
								entries.add(entryarray.getJSONObject(y)); // add entries in in lijst
							}
							for (JSONObject obj : entries) {
								json = new String("{\"begin\":\"" + obj.getString("start")  + "\",\"notities\":\"" + obj.getString("notes") + "\",\"pid\":\"" + pid + "\",\"eind\":\"" + obj.getString("end") + "\"}");
								AsyncServerPosts taak = new AsyncServerPosts(context, Tasks.MANUALENTRY, activity);
								StringEntity entitystring = new StringEntity(json);
								task.execute(entitystring);
							}
						}
						else {
							Log.i("syncerror", "error bij maken nieuw project");
							return "";
						}
					} else if (object.has("edit")) {
						json = "{\"projectname\":\"" + object.getString("projectname") + "\",\"client\":\"" + object.getString("client") + "\",\"summary\":\"" + object.getString("description") + "\",\"pid\":\"" + object.getInt("pid") + "\"}";
						StringEntity entity = new StringEntity(json);
						AsyncServerPosts task = new AsyncServerPosts(context, Tasks.EDITPROJECT, activity);
						task.execute(entity);
						JSONObject result = new JSONObject(task.get());	
						if (result.has("error")) {
							Log.i("errorsynceditis",result.getString("error"));
							return "";
						}
					}
					int pid = -1;
					if (object.has("pid")) {
						pid = object.getInt("pid");
					}
					JSONArray entryarray = object.getJSONArray("entries");
					ArrayList<JSONObject> entries = new ArrayList<JSONObject>();
					for (int y = 0; y < entryarray.length();y++) {
						JSONObject obj = entryarray.getJSONObject(y);
						if (obj.has("edit")) {
							entries.add(obj);
						}				
					}
					for (JSONObject obju : entries) {
						json = new String("{\"begin\":\"" + obju.getString("start")  + "\",\"notities\":\"" + obju.getString("notes") + "\",\"pid\":\"" + pid + "\",\"eind\":\"" + obju.getString("end") + "\"}");
						AsyncServerPosts taak = new AsyncServerPosts(context, Tasks.MANUALENTRY, activity);
						StringEntity entitystring = new StringEntity(json);
						taak.execute(entitystring);
					}
				}		
				JSONConversion.deleteDataNaSync(context);
		return "";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values); */	
		String message = "";
		JSONArray json = null;
		boolean succes1 = true;
		boolean succes2 = true;
		boolean succes3 = true;
		try {
		
			SharedPreferences prefs = context.getSharedPreferences("data", 0);
		
			json = new JSONArray(prefs.getString("json", "[]"));
		
			switch (teSyncen) {
			case 1: //nieuwe projecten die offlien zijn gemaakt + hun entries
				succes1 = nieuweProjecten(getNieuweProjecten(json));
				break;
			case 2: //bestaande projcten die edits hebben
				succes2 = editProjecten(getEditProjecten(json));
				break;
			case 3: // nieuwe projecten die offlien zijn gemaakt + hun entries &  bestaande projcten die edits hebben
				succes1 = nieuweProjecten(getNieuweProjecten(json));
				succes2 = editProjecten(getEditProjecten(json));
				break;
			case 4: // bestaande projecten met nieuwe entries
				succes3 = entryProjecten(getEntries(json));
				break;
			case 5: // bestaande projecten met nieuwe entries &  nieuwe projecten die offlien zijn gemaakt + hun entries
				succes1 = nieuweProjecten(getNieuweProjecten(json));
				succes3 = entryProjecten(getEntries(json));
				break;
			case 6: //  bestaande projcten die edits hebben & bestaande projecten met nieuwe entries
				succes2 = editProjecten(getEditProjecten(json));
				succes3 = entryProjecten(getEntries(json));
				break;
			case 7: //ALLEEEEUUUUUSSSS
				succes1 = nieuweProjecten(getNieuweProjecten(json));
				succes2 = editProjecten(getEditProjecten(json));
				succes3 = entryProjecten(getEntries(json));
				break;				
			}		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (succes1 && succes2 & succes3) {
			JSONConversion.deleteDataNaSync(context);
		}
		
		return message;
	}
	
	private boolean nieuweProjecten(ArrayList<JSONObject> projecten) throws JSONException, UnsupportedEncodingException, InterruptedException, ExecutionException {
		boolean succes = true;
		for (JSONObject project : projecten) {
			String json = "{\"projectname\":\"" + project.getString("projectname") + "\",\"client\":\"" + project.getString("client") + "\",\"summary\":\"" + project.getString("description") + "\"}";
			AsyncServerPosts task = new AsyncServerPosts(context, Tasks.NEWPROJECT, activity);
			StringEntity entity = new StringEntity(json);
			task.execute(entity);
			JSONObject result = new JSONObject(task.get());	
			if (result.has("pid")) {
				int pid = result.getInt("pid");
				ArrayList<JSONObject> entries = getEntries(project);
				for (JSONObject entry : entries) {
					json = new String("{\"begin\":\"" + entry.getString("start")  + "\",\"notities\":\"" + entry.getString("notes") + "\",\"pid\":\"" + pid + "\",\"eind\":\"" + entry.getString("end") + "\"}");
					task = new AsyncServerPosts(context, Tasks.MANUALENTRY, activity);
					StringEntity entitystring = new StringEntity(json);
					task.execute(entitystring);
					result = new JSONObject(task.get());	
					if (result.has("error")) {
						Log.i("error -1",result.getString("error"));
						succes = false;
					}
				}
				Log.i("status 1", task.get());
			}
			else {
				Log.i("error 12", task.get());
				succes = false;
			}
		}
		return succes;
	}
	
	private boolean editProjecten(ArrayList<JSONObject> projecten) throws JSONException, UnsupportedEncodingException, InterruptedException, ExecutionException{
		boolean succes = true;
		for (JSONObject project : projecten) {
			String json = "{\"projectname\":\"" + project.getString("projectname") + "\",\"client\":\"" + project.getString("client") + "\",\"summary\":\"" + project.getString("description") + "\",\"pid\":\"" + project.getInt("pid") + "\"}";
			StringEntity entity = new StringEntity(json);
			AsyncServerPosts task = new AsyncServerPosts(context, Tasks.EDITPROJECT, activity);
			task.execute(entity);
			JSONObject result = new JSONObject(task.get());	
			if (result.has("error")) {
				Log.i("error 5569",result.getString("error"));
				succes = false;
			}
		}	
		return succes;
	}
	
	private boolean entryProjecten(ArrayList<JSONObject> entries) throws JSONException, UnsupportedEncodingException, InterruptedException, ExecutionException{
		boolean succes = true;
		
		for (JSONObject entry : entries) {
			String json = new String("{\"begin\":\"" + entry.getString("start")  + "\",\"notities\":\"" + entry.getString("notes") + "\",\"pid\":\"" + entry.getString("pid") + "\",\"eind\":\"" + entry.getString("end") + "\"}");
			AsyncServerPosts task = new AsyncServerPosts(context, Tasks.MANUALENTRY, activity);
			StringEntity entitystring = new StringEntity(json);
			task.execute(entitystring);
			JSONObject result = new JSONObject(task.get());	
			if (result.has("error")) {
				Log.i("error 2054488",result.getString("error"));
				succes = false;
			}
		}
		
		return succes;
	}
	
	private ArrayList<JSONObject> getNieuweProjecten(JSONArray json) throws JSONException {
		ArrayList<JSONObject> nieuweprojecten = new ArrayList<JSONObject>();
		
		for (int i = 0; i < json.length();i++) {
			JSONObject project = json.getJSONObject(i);
			if (project.getInt("online") >= 0) {
				nieuweprojecten.add(project);
				Log.i("nieuw project ", project.toString());
			}
		}
		
		return nieuweprojecten;
	}
	
	private ArrayList<JSONObject> getEditProjecten(JSONArray json) throws JSONException {
		ArrayList<JSONObject> editprojecten = new ArrayList<JSONObject>();
		
		for (int i = 0; i < json.length();i++) {
			JSONObject project = json.getJSONObject(i);
			if (project.has("edit")) {
				editprojecten.add(project);
				Log.i("edit project ", project.toString());
			}
		}
		
		return editprojecten;
	}
	
	private ArrayList<JSONObject> getEntries(JSONArray json) throws JSONException {
		ArrayList<JSONObject> entrieslijst = new ArrayList<JSONObject>();
		
		for (int i = 0; i < json.length();i++) {
			JSONObject project = json.getJSONObject(i);
			if (project.getInt("online") == -1) {
				JSONArray entries = project.getJSONArray("entries");
				int pid = project.getInt("pid");
				for (int y = 0; y < entries.length(); y++) {
					JSONObject entry = entries.getJSONObject(y); 
					if (entry.has("edit")) {
						entry.put("pid", pid);
						entrieslijst.add(entry);
						Log.i("syncentry ", entry.toString());
					}
				}
			}
		}
		
		return entrieslijst;
	}
	
	private ArrayList<JSONObject> getEntries(JSONObject json) throws JSONException {
		ArrayList<JSONObject> entrieslijst = new ArrayList<JSONObject>();		
		
			JSONObject project = json;
			if (project.getInt("online") != -1) {
				JSONArray entries = project.getJSONArray("entries");
				for (int y = 0; y < entries.length(); y++) {
					JSONObject entry = entries.getJSONObject(y); 
					if (entry.has("edit")) {						
						entrieslijst.add(entry);
						Log.i("syncentry 2 ", entry.toString());
					}
				}
			}
		
		
		return entrieslijst;
	}


}
