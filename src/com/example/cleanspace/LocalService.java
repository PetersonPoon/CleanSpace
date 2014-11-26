package com.example.cleanspace;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
	private NotificationManager notifyMan;

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
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}