package com.sourcefish.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClient {
	static public DefaultHttpClient getClient(String username, String password){
		DefaultHttpClient client=new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		List<String> authprefs=new ArrayList<String>(1);
		authprefs.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, authprefs);
		return client;	
	}
}
