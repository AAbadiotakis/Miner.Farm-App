package com.elvensmite.minerfarm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class downloadInfo extends AsyncTask<String, Void, String> {

	private HttpClient createHttpClient()
	{
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
	    return new DefaultHttpClient(conMgr, params);
	}
	
	
	
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		for (String url : urls) {
			HttpClient client = createHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("APIKey",MainActivity.apiLong));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse execute = client.execute(httppost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
				new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					response += s;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	System.out.println(response);
	return response;
	}
	
	protected void onPostExecute(String result) {
		try {
		MainActivity.Comment.clear();
		//REGEX Comment
		Pattern Comment = Pattern.compile("(\"Comment\":\")([^\"]*)");
		Matcher m = Comment.matcher(result);
		while(m.find()){
			MainActivity.Comment.add(m.group(2));
		}
		//REGEX TimeElapsed
		Pattern time = Pattern.compile("(\"Elapsed=)([^,]*)");
		m = time.matcher(result);
		Long tempTimeElapsed = null;
		while(m.find()) {
			tempTimeElapsed = Long.parseLong(m.group(2));
		}
		boolean notDone = true;
		int numDays = 0;
		int numHours = 0;
		int numMins = 0;
		while(notDone) {
			if(tempTimeElapsed > 86400) {
				numDays++;
				tempTimeElapsed = tempTimeElapsed/86400;
			}else if(tempTimeElapsed > 3600) {
				numHours++;
				tempTimeElapsed = tempTimeElapsed/3600;
			}else if(tempTimeElapsed > 60) {
				numMins++;
				tempTimeElapsed = tempTimeElapsed/60;
			}else{
				notDone = false;
			}
		}
		if(numDays > 0) {
			MainActivity.timeElapsed = numDays+" Day(s) "+numHours+" Hour(s) "+numMins+" Minute(s) "+tempTimeElapsed+" Second(s)";
		}else if(numHours > 0) {
			MainActivity.timeElapsed = numHours+" Hour(s) "+numMins+" Minute(s) "+tempTimeElapsed+" Second(s)";			
		}else if(numMins > 0) {
			MainActivity.timeElapsed = numMins+" Minute(s) "+tempTimeElapsed+" Second(s)";			
		}else {
			MainActivity.timeElapsed = tempTimeElapsed+" Second(s)";
		}
		
		MainActivity.Miner.clear();
		//REGEX Miner
		Pattern Miner = Pattern.compile("(\"Miner=)([^,]*)");
		m = Miner.matcher(result);
		while(m.find()){
			MainActivity.Miner.add(m.group(2));
		}
		MainActivity.HashAvg.clear();
		//REGEX HashAvg
		Pattern HashAvg = Pattern.compile("(MHS av=)([^,]*)");
		m = HashAvg.matcher(result);
		while(m.find()){
			double tempVal = Double.parseDouble(m.group(2));
			if(tempVal > 1) {
				MainActivity.HashAvg.add(m.group(2) + " MHS");
			}else{
				tempVal = tempVal*1000;
				MainActivity.HashAvg.add(m.group(2) + " KHS");
			}
		}
		MainActivity.Accepted.clear();
		//REGEX Accepted
		Pattern Accepted = Pattern.compile("(,Accepted=)([^,]*)");
		m = Accepted.matcher(result);
		while(m.find()){
			MainActivity.Accepted.add(m.group(2));
		}
		MainActivity.Rejected.clear();
		//REGEX Rejected
		Pattern Rejected = Pattern.compile("(,Rejected=)([^,]*)");
		m = Rejected.matcher(result);
		while(m.find()){
			MainActivity.Rejected.add(m.group(2));
		}
		MainActivity.runThread = true;
		} catch(Exception e) {
			
		}
	}

}
