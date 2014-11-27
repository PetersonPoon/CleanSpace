package com.example.cleanspace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
	private Boolean mStop = false;
	private NotificationManager notifyMan;
	public final String ARDUINO_IP_ADDRESS = "192.168.240.1";
	private String dustUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/analog/1"; // Read dust
	private String COUrl = "http://" + ARDUINO_IP_ADDRESS + "/arduino/analog/2"; // Read
																					// CO
	private String humidityUrl = "http://" + ARDUINO_IP_ADDRESS
			+ "/arduino/analog/3"; // Read
	// humidity

	// Set these values in order to bind them and send it back?
	public double sensorTime = 1;
	public double dustValue;
	public double coValue;
	public double humidityValue;
	public double temperatureValue;
	public double dustVoltageMap;

	public double DustDensity;
	public String sensorName;

	public String sensorStatus;
	private static final String badStatus = "Requires Attention";
	private static final String fairStatus = "Fair";
	private static final String goodStatus = "Good";

	// Object that receives interactions with clients
	private final IBinder mBinder = new LocalBinder();

	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}

	}

	@Override
	public void onCreate() {
		notifyMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// So we know that service is happening. We should probably only call
		// showNotification when status is bad for future
		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		mStop = false;
		if (threadReceive == null) {
			threadReceive = new Thread(networkRunnableReceive);
			threadReceive.start();
		}

		// TODO: iS THIS IN THE RIGHT PLACE?
		CalcLevel(dustValue);

		UpdateStatus(dustValue, coValue);

		// TODO How do we pass it a file name..I guess there would only be one sensor at a time?
		// Write refreshed data into file for storage/graphing
		FileHelper.appendFile(readFile, DustDensity, coValue, sensorTime,
				sensorStatus);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {

		mStop = true;
		if (threadReceive != null) {
			threadReceive.interrupt();
		}
		// Cancel the persistent notification.
		notifyMan.cancel(NOTIFICATION);

	}

	/**
	 * Return binder with sensor data each time it is binded to
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Show a notification while this service is running for now, but will be
	 * used to notify user when status is bad in future
	 */
	private void showNotification() {
		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification notification = new Notification.Builder(this)
				.setContentTitle("Home Air Monitor")
				.setContentText(
						"Sensor Needs Attention!" + System.currentTimeMillis())
				.setSmallIcon(R.drawable.cleanspace).setContentIntent(pIntent)
				.setAutoCancel(true).build();
		// Send the notification.
		notifyMan.notify(NOTIFICATION, notification);
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
			// setNotification(true);
		} else if ((myDust >= 0.3) || (myCo >= 20)) {
			sensorStatus = fairStatus;
		} else {
			sensorStatus = goodStatus;
		}
		return sensorStatus;
	}

}