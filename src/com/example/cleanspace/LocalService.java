package com.example.cleanspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

	int thisIsWrong;

	ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track
																// of all
																// current
																// registered
																// clients.

	// TODO: I don't quite get what these are for yet, but maybe we can make
	// them for setting each string value for dust, co, humidity, temp?
	int mValue = 0; // Holds last value set by a client.
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_GET_SENSOR_NAME = 3;
	static final int MSG_SET_SENSOR_VALUES = 4;
	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target
																		// we
																		// publish
																		// for
																		// clients
																		// to
																		// send
																		// messages
																		// to
																		// IncomingHandler.

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	class IncomingHandler extends Handler { // Handler of incoming messages from
											// clients.
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_GET_SENSOR_NAME:
				sensorName = msg.toString();
			case MSG_SET_SENSOR_VALUES:
				// get all sensor values, write to file?
				File readFile = new File(getExternalFilesDir(null), sensorName);

				FileHelper.appendFile(readFile, DustDensity, coValue,
						sensorTime, sensorStatus);
				break;

			default:
				super.handleMessage(msg);
			}
		}
	}

	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

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
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		mStop = false;
		if (threadReceive == null) {
			threadReceive = new Thread(networkRunnableReceive);
			threadReceive.start();
		}

		// TODO: iS THIS IN THE RIGHT PLACE?
		CalcLevel(dustValue);

		UpdateStatus(dustValue, coValue);

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