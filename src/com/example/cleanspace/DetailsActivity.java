package com.example.cleanspace;

import static com.example.cleanspace.EditActivity.EDITEDSAMPLEAREA;
import static com.example.cleanspace.EditActivity.EDITEDTITLE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	public final String ARDUINO_IP_ADDRESS = "192.168.240.1";
	private Boolean mStop = false;
	private String dustUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/analog/1"; // Read dust
	private String COUrl = "http://" + ARDUINO_IP_ADDRESS + "/arduino/analog/2"; // Read
																					// CO
	// TODO Change this for Humidity
	private String humidityUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/analog/3"; // Read
	// humidity

	public double sensorTime = 1;
	public double dustValue;
	public double coValue;
	public double humidityValue;
	public double temperatureValue;
	public double dustVoltageMap;
	public double DustDensity;

	public String sensorStatus;

	public static final String SENSORFILENAME = "temp";

	public String sensorFileName = "";
	String newSensorTitle = "Furnace Filter";
	static String testName = "qwerty";
	public static final String SENSORNAME = "temp";

	private static final String badStatus = "Requires Attention";
	private static final String fairStatus = "Fair";
	private static final String goodStatus = "Good";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		fillSensorFields();
	}

	@Override
	protected void onStart() {
		mStop = false;
		if (threadReceive == null) {
			threadReceive = new Thread(networkRunnableReceive);
			threadReceive.start();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		mStop = true;
		if (threadReceive != null) {
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
		} else if (id == R.id.refresh_button) { // To write the data to the file
												// whenever we refresh
			fillSensorFields();

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
	 * Call this method to fill in sensor fields
	 * 
	 * Currently fills in: Dust Value CO Value Status (With colour coding)
	 */
	private void fillSensorFields() {
		// Get Dust data
		TextView myText = (TextView) findViewById(R.id.currentStatus);
		CalcLevel(dustValue);

		/**
		 * Change status text colour depending on status
		 */
		sensorStatus = UpdateStatus(DustDensity, coValue);
		myText.setText(String.valueOf(sensorStatus));
		if (sensorStatus == badStatus) {
			myText.setTextColor(Color.RED);
		} else if (sensorStatus == fairStatus) {
			myText.setTextColor(Color.BLUE);
		} else {
			myText.setTextColor(Color.GREEN);
		}

		LoadSensorDetails();
	}

	private static Thread threadReceive = null;
	private final Runnable networkRunnableReceive = new Runnable() {
		@Override
		public void run() {

			while (mStop == false) {
				try {
					String dustString = readFROMURL(dustUrl);
					String coString = readFROMURL(COUrl);
					try {
						dustValue = Double.parseDouble(dustString);
						coValue = Double.parseDouble(coString);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			threadReceive = null;
		}
	};

	public String readFROMURL(String passURL) {
		URL tempURL;
		StringBuilder builder = new StringBuilder();
		try {
			tempURL = new URL(passURL);
			URLConnection urlConnect = tempURL.openConnection();
			BufferedReader mcData = new BufferedReader(new InputStreamReader(
					urlConnect.getInputStream()));
			String inputLine;
			while ((inputLine = mcData.readLine()) != null) {
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

	/**
	 * Dust Value in mg/ m^3
	 * 
	 * @param data
	 */
	private void CalcLevel(double data) {
		dustVoltageMap = ((1023 - data) * 3.3) / 1024.0;
		DustDensity = (0.17 * dustVoltageMap) - 0.1;
	}

	public String UpdateStatus(double myDust, double myCo) {
		// Testing for now will refine the data later
		// TODO CO value means nothing, just put in a value to test
		if ((myDust >= 0.40) || (myCo >= 50)) {
			sensorStatus = badStatus;
		} else if ((myDust >= 0.3) || (myCo >= 20)) {
			sensorStatus = fairStatus;
		} else {
			sensorStatus = goodStatus;
		}
		return sensorStatus;
	}

	private void LoadSensorDetails() {

		Intent intent = getIntent();
		if (null != intent) {
			sensorFileName = intent.getStringExtra(SENSORFILENAME);
			File readFile = new File(getExternalFilesDir(null), sensorFileName);

			String readData = FileHelper.readFromFile(readFile);
			String[] sensorData = readData.split(",");

			if (sensorData != null) {
				TextView sensorTitle = (TextView) findViewById(R.id.sensor_title);
				sensorTitle.setText(sensorData[0]);
				TextView sampleArea = (TextView) findViewById(R.id.current_area);
				sampleArea.setText(sensorData[1]);

				// Write refreshed data into file for storage/graphing
				FileHelper.appendFile(readFile, DustDensity, coValue,
						sensorTime);
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
