package com.example.cleanspace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity {

	EditText newSensorName;
	EditText newSampleArea;

	String sensorName;
	String sampleArea;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	/**
	 * Create file if sensor name does not already exist. If file exists, no
	 * file created, toast that existing sensor is in system, try new name
	 * 
	 * @param view
	 * @throws IOException
	 */
	public void saveNewSensor(View view) throws IOException {

		// If file is accessible
		if (isExternalStorageWriteable()) {
			FileOutputStream fos;

			// Get data user inputed
			newSensorName = (EditText) findViewById(R.id.sensor_title);
			newSampleArea = (EditText) findViewById(R.id.sample_area);

			sensorName = newSensorName.getText().toString();
			sampleArea = newSampleArea.getText().toString();

			Log.d("area", sampleArea);
			Log.d("name", sensorName);

			File myPath = new File(sensorName);

			Log.d("path", myPath.toString());

			// If file exists, make toast
			// REPEATED FILES ARE ALLOWED??
			if (myPath.exists() && myPath.isDirectory()) {
				Toast.makeText(getApplicationContext(),
						"This sensor already exists, try again",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					if (sensorName == null || sensorName.isEmpty()) {
						Toast.makeText(getApplicationContext(),
								"Please enter a valid name", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (sampleArea == null || sampleArea.isEmpty()) {
						Toast.makeText(getApplicationContext(),
								"Please enter a valid area", Toast.LENGTH_SHORT)
								.show();
						return;
					} else {

						// Open file, write in sample area
						fos = openFileOutput(sensorName, Context.MODE_PRIVATE);
						fos.write(sampleArea.getBytes());
						fos.close();

						Toast.makeText(getApplicationContext(),
								"New sensor has been saved", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
