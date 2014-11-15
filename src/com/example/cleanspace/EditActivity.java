package com.example.cleanspace;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;

import static com.example.cleanspace.DetailsActivity.SENSORNAME;

public class EditActivity extends Activity {
	public static final String EDITEDTITLE = "temp";
	public static final String EDITEDSAMPLEAREA = "123";
	
	String newSensorTitle = "Furnace Filter";
	static String testName = "qwerty";
	String oldSensorTitle = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		
		String oldSampleArea = "";
		
		Intent intent = getIntent();
		if(null != intent){
			oldSensorTitle = intent.getStringExtra(EDITEDTITLE);
			
			oldSampleArea = intent.getStringExtra(EDITEDSAMPLEAREA);
			
			
			EditText LoadSensorTitle = (EditText) findViewById(R.id.sensor_title);
			LoadSensorTitle.setText(oldSensorTitle);
			
			EditText LoadSampleArea = (EditText) findViewById(R.id.current_area);
			LoadSampleArea.setText(oldSampleArea);
		}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void saveEditActivity(View view) {
		EditText newSensorTitle = (EditText) findViewById(R.id.sensor_title);
		String newTitle = newSensorTitle.getText().toString();
		
		EditText sampleArea = (EditText) findViewById(R.id.current_area);
		String newSampleArea = sampleArea.getText().toString();
		
		String FileName = oldSensorTitle + ".txt";//getExternalFilesDir();		
		File OpenSaveFile = new File(getExternalFilesDir(null), FileName);
		
		File renameSaveFile = new File(getExternalFilesDir(null), newTitle + ".txt" );
		
		if(OpenSaveFile.exists()){
			OpenSaveFile.renameTo(renameSaveFile);			
			
		}
		if (isExternalStorageWriteable()) {
			//FileOutputStream fos;
			
			try {
				OutputStream fos = new FileOutputStream(renameSaveFile);
				if(newSensorTitle != null){				
					fos.write(newTitle.getBytes());						
				}
				
				if(newSampleArea != null){
					fos.write('\n');
					fos.write(newSampleArea.getBytes());					
				}
				fos.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		Intent detailsIntent = new Intent(EditActivity.this,
				DetailsActivity.class);
		// For future when we need to pass data to DetailsActivity:
		// detailsIntent.putExtra(name, value);
		
		detailsIntent.putExtra(SENSORNAME, newTitle + ".txt");
		EditActivity.this.startActivity(detailsIntent);
	}
	
	public boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
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
