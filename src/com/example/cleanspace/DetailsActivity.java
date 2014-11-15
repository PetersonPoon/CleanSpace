package com.example.cleanspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;

import static com.example.cleanspace.EditActivity.EDITEDTITLE; //@string/first_sensor_details"
import static com.example.cleanspace.EditActivity.EDITEDSAMPLEAREA;

public class DetailsActivity extends Activity {
	public final String ARDUINO_IP_ADDRESS = "192.168.240.1";
	private Boolean mStop = false;
	
	public double value;
	public double VoltageMap;
	public double DustDensity;
	
	public String ODSStatus; 
	
	String newSensorTitle = "Furnace Filter";
	static String testName = "qwerty";
	public static final String SENSORNAME = "temp";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		
		String temp = "";
		String newSampleArea = "";
		String tempSampleArea = "";
		int newLinePassed = 0;
		
		String sensorFileName = "";
		Intent intent = getIntent();
		if(null != intent){
			sensorFileName = intent.getStringExtra(SENSORNAME);
		}
		
		if(isExternalStorageReadable()){			
			File FileExists = new File(getExternalFilesDir(null), sensorFileName);
			InputStream fis = null;
			
			try {				
			
				fis = new BufferedInputStream(new FileInputStream(FileExists));
				int t = 0;	
				while((t = fis.read()) != -1){
					
					if(t != 10 && newLinePassed == 0){
						temp = temp + Character.toString((char)t);						
					}else if (t == 10){
						newLinePassed = 1;
					}else if (newLinePassed ==1){						
						tempSampleArea = tempSampleArea + Character.toString((char)t);
					}
				}
				newLinePassed = 0;
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			 newSensorTitle = "Furnace Sensor";
			 newSampleArea = "123";
		}
		
		if (temp != null){
			TextView sensorTitle = (TextView) findViewById(R.id.sensor_title);
			sensorTitle.setText(temp);
		}else if(newSensorTitle != ""){
			TextView sensorTitle = (TextView) findViewById(R.id.sensor_title);
			sensorTitle.setText(newSensorTitle);
			
		}
		
		
		if (tempSampleArea != null){
			TextView sampleArea = (TextView) findViewById(R.id.current_area);
			sampleArea.setText(tempSampleArea);
		}
		else if(newSampleArea != null){	
			TextView sampleArea = (TextView) findViewById(R.id.current_area);
			sampleArea.setText(newSampleArea);
		}
		
	}

	@Override
	protected void onStart(){
		mStop = false;
		if(threadReceive == null){
			threadReceive = new Thread(networkRunnableReceive);
			threadReceive.start();
		}	
		super.onStart();
	}
	
	@Override
	protected void onStop(){
		mStop = true;
		if(threadReceive != null){
			threadReceive.interrupt();
		}
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.refresh_button) { //To write the data to the file whenever we refresh
			setContentView(R.layout.activity_details);
			TextView myText = (TextView) findViewById(R.id.currentStatus);
			CalcLevel(value);
			UpdateStatus(DustDensity);
			myText.setText(String.valueOf(ODSStatus));
			return true;
		} else if (id == R.id.edit_button) {
			Intent editIntent = new Intent(DetailsActivity.this,
					EditActivity.class);
			TextView newSensorTitle = (TextView) findViewById(R.id.sensor_title);
			String newTitle = newSensorTitle.getText().toString();
			
			TextView newSampleArea = (TextView) findViewById(R.id.current_area);
			String newArea = newSampleArea.getText().toString();
			
			editIntent.putExtra(EDITEDTITLE, newTitle);
			editIntent.putExtra(EDITEDSAMPLEAREA, newArea);
			DetailsActivity.this.startActivity(editIntent);
		}
		return super.onOptionsItemSelected(item);
	}


	private static Thread threadReceive = null;
	private final Runnable networkRunnableReceive = new Runnable(){
		@Override
		public void run() {
			String url = "http://"+ARDUINO_IP_ADDRESS+"/arduino/analog/1"; //Read from analog 0
	
			while(mStop == false){
				try {
					String tempString = readFROMURL(url);
					try {
						value = Double.parseDouble(tempString);
					} 
					catch (Exception e){
						e.printStackTrace();
					}
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			threadReceive = null;
			}
	};

	public String readFROMURL(String passURL){
		URL tempURL;
		StringBuilder builder = new StringBuilder();
		try {
			tempURL = new URL(passURL);
			URLConnection urlConnect = tempURL.openConnection();
			BufferedReader mcData = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
			String inputLine;
			while ((inputLine = mcData.readLine()) != null){ 
				builder.append(inputLine);
			}
			mcData.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public void CalcLevel(double data) {
		VoltageMap = (data * 3.3) / 1024.0;
		DustDensity = (0.17 * VoltageMap) - 0.1;
	}
	
	public void UpdateStatus(double myDust) {
		//Testing for now will refine the data later
		if(myDust >= 0.40) { ODSStatus = "Require Attention"; }
		else if (myDust >= 0.3) { ODSStatus = "Fair"; }
		else
			ODSStatus = "Good";	
	}
	
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
}
