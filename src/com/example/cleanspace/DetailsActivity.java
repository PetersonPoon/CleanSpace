package com.example.cleanspace;

import static com.example.cleanspace.EditActivity.EDITEDSAMPLEAREA;
import static com.example.cleanspace.EditActivity.EDITEDTITLE;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	// public final String ARDUINO_IP_ADDRESS = "192.168.240.1";
	// private String dustUrl = "http://" + ARDUINO_IP_ADDRESS
	// + "/arduino/analog/1"; // Read dust
	// private String COUrl = "http://" + ARDUINO_IP_ADDRESS +
	// "/arduino/analog/2"; // Read
	// // CO
	// // TODO Change this for Humidity
	// private String humidityUrl = "http://" + ARDUINO_IP_ADDRESS
	// + "/arduino/analog/3"; // Read
	// humidity

	public double sensorTime = 1;
	public double dustValue;
	public double coValue;
	public double humidityValue;
	public double temperatureValue;
	public double dustVoltageMap;
	public double DustDensity;

	private static final String badStatus = "Requires Attention";
	private static final String fairStatus = "Fair";
	private static final String goodStatus = "Good";
	public String sensorStatus;

	public static final String SENSORFILENAME = "temp";

	public String sensorFileName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		LoadSensorDetails();
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
		} else if (id == R.id.refresh_button) { // To write the data to the file
			LoadSensorDetails();

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

	/**
	 * Call this method to fill in status fields
	 * 
	 */
	private void fillSensorFields(File fileName) {
		// Get Dust data
		TextView myText = (TextView) findViewById(R.id.currentStatus);
		/**
		 * Change status text colour depending on status
		 */
		sensorStatus = FileHelper.readStatusFromFile(fileName);
		myText.setText(String.valueOf(sensorStatus));

		if (sensorStatus == badStatus) {
			myText.setTextColor(Color.RED);
		} else if (sensorStatus == fairStatus) {
			myText.setTextColor(Color.BLUE);
		} else {
			myText.setTextColor(Color.GREEN);
		}

	}

	private void LoadSensorDetails() {

		Intent intent = getIntent();
		if (null != intent) {
			sensorFileName = intent.getStringExtra(SENSORFILENAME);
			File readFile = new File(getExternalFilesDir(null), sensorFileName);

			String readData = FileHelper.readNameAndAreaFromFile(readFile);
			String[] sensorData = readData.split(",");

			if (sensorData != null) {
				TextView sensorTitle = (TextView) findViewById(R.id.sensor_title);
				sensorTitle.setText(sensorData[0]);
				TextView sampleArea = (TextView) findViewById(R.id.current_area);
				sampleArea.setText(sensorData[1]);

				fillSensorFields(readFile);
				// Write refreshed data into file for storage/graphing
				FileHelper.appendFile(readFile, DustDensity, coValue,
						sensorTime, sensorStatus);
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Sensor data not able to load, Please try again",
					Toast.LENGTH_SHORT).show();
		}

		// Load dust count to app
		TextView currentCount = (TextView) findViewById(R.id.particle_value);
		double dustCount = (double) Math.round(DustDensity * 100000) / 100000;
		String count = String.valueOf(dustCount);
		currentCount.setText(count);

		// Load CO count to app
		TextView coCount = (TextView) findViewById(R.id.co_value);
		String co = String.valueOf(coValue);
		coCount.setText(co);

	}
}
