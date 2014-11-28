package com.example.cleanspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Start the service when intent is received
 * 
 * @author eychiang
 * 
 */
public class AutoStartService extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, LocalService.class));
	}
}
