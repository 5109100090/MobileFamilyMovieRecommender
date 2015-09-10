package com.android.recommender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class HTTPTransfer extends AsyncTask<String, Void, String>{
	
	public final static String BASE_URL = "http://118.139.23.225:8080/ReviewServer/webresources/";
	
	public HTTPTransfer()
	{
	}

	@Override
	protected String doInBackground(String... args) {
		String url = args[0];
		String resmsg = "";
		// Making HTTP request
		try {
			HttpClient httpclient = new DefaultHttpClient();
			//http://10.1.1.17:8080/ReviewServer/webresources/com.entity.session
			HttpGet request = new HttpGet(BASE_URL + url);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(instream));
			String s = "";
			
			while ((s = buffer.readLine()) != null) {
				resmsg += s;
			}
			Log.i("Respones is ", resmsg);
		} catch(Exception e){
			e.printStackTrace();
		}
		return resmsg;
	}
	
	@Override
	protected void onPostExecute(String result){
		//textView.setText(result);
	}
}

