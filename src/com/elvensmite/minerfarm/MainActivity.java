package com.elvensmite.minerfarm;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	//---------------------- Key Values -------------------------------//
	static String apiShort = "";
	static String apiLong = "";
	//-----------------------------------------------------------------//
	//---------------------- Components -------------------------------//
	TextView testView;
	//-----------------------------------------------------------------//
	//---------------------- String Info ------------------------------//
	static ArrayList<String> Comment = new ArrayList<String>();
	static ArrayList<String> Miner = new ArrayList<String>();
	static String timeElapsed;
	static ArrayList<String> HashAvg = new ArrayList<String>();
	static String Hash5s;
	static ArrayList<String> Accepted = new ArrayList<String>();
	static ArrayList<String> Rejected = new ArrayList<String>();
	
	//-----------------------------------------------------------------//
	static boolean runThread = false;
	
	
	Timer t;
	public MainActivity() {
		this.t = new Timer();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiShort = preferences.getString("shortAPI", apiShort);
        apiLong = preferences.getString("longAPI", apiLong);
        testView = (TextView) findViewById(R.id.textView1); 
    }

    public void onResume() {
    	super.onResume();
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiShort = preferences.getString("shortAPI", apiShort);
        apiLong = preferences.getString("longAPI", apiLong);
    }
    
    public void onRestart() {
    	super.onRestart();
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        apiShort = preferences.getString("shortAPI", apiShort);
        apiLong = preferences.getString("longAPI", apiLong);   	
    	
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent Settings = new Intent(this, SettingsActivity.class);
        	startActivity(Settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void testScript(View v) {
    	String tempString = "https://api.miner.farm/v1/"+apiShort+"/farm";
    	System.out.println("API Short: "+apiShort);
    	System.out.println("API Long: "+apiLong);
    	try {
    	new downloadInfo().execute(tempString);
    	} catch(Exception E) {
    		System.out.println("Error while trying to load information.");
    	}
    	updateInfo();
    }
    
    public void updateInfo() {
    
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						while(runThread) {
								testView.setText("");
							for(int i=0;i<Comment.size();i++) {
								double acceptVal = Double.parseDouble(Accepted.get(0));
								double rejectVal = Double.parseDouble(Rejected.get(0));
								double percent = (rejectVal/(acceptVal)*100);
								percent = Math.floor(percent*100)/100;								
								testView.append(Html.fromHtml("Rig Name:"+Comment.get(i)+"\n"+
												"Miner: "+Miner.get(i)+"\n"+
												"Time Elapsed: "+timeElapsed+"\n"+
												"Summary\n "+
												"Hash Average: "+HashAvg.get(0)+"\n"+
												"Accepted/Rejected: "+Accepted.get(0)+"/"+Rejected.get(0)+"("+percent+" %)\n\n"));
								
							}
							for(int i=1;i<HashAvg.size();i++) {
								double acceptVal = Double.parseDouble(Accepted.get(i));
								double rejectVal = Double.parseDouble(Rejected.get(i));
								double percent = (rejectVal/acceptVal)*100;
								percent = Math.floor(percent*100)/100;
								testView.append("GPU="+i+":\n "+
												"Hashrate: "+HashAvg.get(i)+"\n"+
												"Accepted/Rejected: "+Accepted.get(i)+"/"+Rejected.get(i)+"("+percent+" %)\n"
												);
							}
							runThread=false;
						}
					}
					
				});
				
			}
			
		},0,1000);
    	
    	
    }
    
    
}
