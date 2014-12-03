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
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends FragmentActivity {
	public final String ARDUINO_IP_ADDRESS = "192.168.240.1";
	private Boolean mStop = false;
	private String dustUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/analog/1"; // Read dust
	private String COUrl = "http://" + ARDUINO_IP_ADDRESS + "/arduino/analog/2"; // Read
																					// CO
	// TODO Change this for Humidity
	private String humidityUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/digital/12"; // Read
	// humidity

	public long sensorTimeMilli;
	public double dustValue;
	public double coValue;
	public double humidityValue;
	public double temperatureValue;
	public double dustVoltageMap;
	public double DustDensity;

	public String sensorStatus;

	public static final String SENSORFILENAME = "temp";

	public String sensorFileName = "";

	private static final String badStatus = "Requires Attention";
	private static final String fairStatus = "Fair";
	private static final String goodStatus = "Good";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
	}

	@Override
	protected void onStart() {
		mStop = false;
		if (threadReceive == null) {
			threadReceive = new Thread(networkRunnableReceive);
			threadReceive.start();
		}
		fillSensorFields();

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
		if (dustValue > 0) {
			CalcLevel(dustValue);
		}

		/**
		 * Change status text colour depending on status
		 */
		sensorStatus = UpdateStatus(DustDensity, coValue, humidityValue);
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
					String humTempString = readFROMURL(humidityUrl);

					try {
						dustValue = Double.parseDouble(dustString);
						coValue = Double.parseDouble(coString);

						String dust = String.valueOf(dustValue);
						String co = String.valueOf(coValue);

						Log.d("dust", dust);
						Log.d("co", co);
						String[] humidityTemp = humTempString.split("\\s+");
						humidityValue = Double.parseDouble(humidityTemp[0]);
						temperatureValue = Double.parseDouble(humidityTemp[1]);

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

	public String UpdateStatus(double myDust, double myCo, double myHum) {
		// Testing for now will refine the data later
		// TODO CO value means nothing, just put in a value to test
		if ((myDust >= 0.40) || (myCo >= 50) || (myHum >= 40)) {
			sensorStatus = badStatus;

			// setNotification(true);
		} else if ((myDust >= 0.3) || (myCo >= 20) || (myHum >= 30)) {
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

			String sensorName = FileHelper.readSpecificFromFile(readFile,
					"Name");
			String sensorArea = FileHelper.readSpecificFromFile(readFile,
					"Area");

			if (sensorName != null && sensorArea != null) {
				TextView sensorTitle = (TextView) findViewById(R.id.sensor_title);
				sensorTitle.setText(sensorName);
				TextView sampleArea = (TextView) findViewById(R.id.current_area);
				sampleArea.setText(sensorArea);

				sensorTimeMilli = System.currentTimeMillis();
				long sensorTimeHours = TimeUnit.MILLISECONDS
						.toHours(sensorTimeMilli);

				String time = String.valueOf(sensorTimeHours);
				Log.d("sensorTime", time);
				// Write refreshed data into file for storage/graphing
				FileHelper.appendFile(readFile, DustDensity, coValue,
						humidityValue, temperatureValue, sensorTimeHours,
						sensorStatus);
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

		// Load humidity count to app
		TextView humCount = (TextView) findViewById(R.id.current_humidity);
		String humidity = String.valueOf(humidityValue);
		humCount.setText(humidity);

		// Load CO count to app
		TextView tempCount = (TextView) findViewById(R.id.current_temperature);
		String temp = String.valueOf(temperatureValue);
		tempCount.setText(temp);

	}

	public void ignoredButton(View view) {
		LocalService.cancelNotification();
	}

	public void changedButton(View view) {
		LocalService.cancelNotification();
		fillSensorFields();
	}

	public void graphButton(View view) {
		Intent graphIntent = new Intent(DetailsActivity.this,
				GraphActivity.class);

		graphIntent.putExtra(SENSORFILENAME, sensorFileName);
		DetailsActivity.this.startActivity(graphIntent);

	}
}