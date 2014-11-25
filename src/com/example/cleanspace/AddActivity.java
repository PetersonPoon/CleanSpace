package com.example.cleanspace;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends Activity {

	public static final String SENSORADDED = null;
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

		// Get data user inputed
		newSensorName = (EditText) findViewById(R.id.sensor_title);
		newSampleArea = (EditText) findViewById(R.id.sample_area);

		sensorName = newSensorName.getText().toString().trim();
		sampleArea = newSampleArea.getText().toString().trim();

		File newSensorFile = new File(getExternalFilesDir(null), sensorName
				+ ".txt");

		// If newSensorFile already exists, toast that it can't be saved
		if (newSensorFile.exists()) {
			Toast.makeText(getApplicationContext(),
					"This sensor already exists, try again", Toast.LENGTH_SHORT)
					.show();
		} else if (sensorName == null || sensorName.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please enter a valid name", Toast.LENGTH_SHORT).show();
		} else if (sampleArea == null || sampleArea.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please enter a valid area", Toast.LENGTH_SHORT).show();
		} else {

			boolean successfulWrite = FileHelper.writeToNewFile(newSensorFile,
					sampleArea, true);
			if (successfulWrite) {
				Toast.makeText(getApplicationContext(),
						"New sensor has been saved", Toast.LENGTH_SHORT).show();

				Intent sensorNameIntent = new Intent(AddActivity.this,
						MainActivity.class);
				sensorNameIntent.putExtra(SENSORFILENAME, sensorName + ".txt");
				sensorNameIntent.putExtra(SENSORADDED, true);
				AddActivity.this.startActivity(sensorNameIntent);
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

}
