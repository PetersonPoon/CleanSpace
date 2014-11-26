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
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
	static final int MSG_SET_DUST_VALUE = 3;
	static final int MSG_SET_CO_VALUE = 4;
	static final int MSG_SET_HUM_VALUE = 5;
	static final int MSG_SET_TEMP_VALUE = 6;
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
			case MSG_SET_DUST_VALUE:
				// dustValue = ;
				break;
			case MSG_SET_CO_VALUE:
				// coValue = ;
				break;
			case MSG_SET_HUM_VALUE:
				// humidityValue ;
				break;
			case MSG_SET_TEMP_VALUE:
				// temperatureValue = ;
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void sendMessageToUI(int intvaluetosend) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as an Integer
				mClients.get(i)
						.send(Message.obtain(null, MSG_SET_DUST_VALUE,
								thisIsWrong, 0));

				// Send data as a String
				Bundle b = new Bundle();
				b.putString("str1", "ab" + intvaluetosend + "cd");
				Message msg = Message.obtain(null, MSG_SET_DUST_VALUE);
				msg.setData(b);
				mClients.get(i).send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
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
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification

		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		// Set the icon, scrolling text and timestamp
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