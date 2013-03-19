package com.sourcefish.tools;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;

public class SourceFishHttpClient {
	static public DefaultHttpClient getClient(String username, String password) {
		DefaultHttpClient client = new DefaultHttpClient();
		Credentials cred=new UsernamePasswordCredentials(username,password);
		client.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
		client.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, AuthPolicy.DIGEST);		
		return client;
		
	}
}
