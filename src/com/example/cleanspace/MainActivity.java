package com.example.cleanspace;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity will display the general details of our one sensor Details
 * include: Name, dust condition (good/bad)
 * 
 * 
 * @author echiang
 * 
 */
public class MainActivity extends Activity {
	View.OnClickListener listeners[];
	String sensorFileTitle;
	Messenger mService = null;
	boolean mIsBound;

	// LocalService mBoundService;
	//
	// private ServiceConnection mConnection = new ServiceConnection() {
	// public void onServiceConnected(ComponentName className, IBinder service)
	// {
	// mBoundService = ((LocalService.LocalBinder) service).getService();
	// }
	//
	// public void onServiceDisconnected(ComponentName className) {
	// // This is called when the connection with the service has been
	// // unexpectedly disconnected -- that is, its process crashed.
	// // Because it is running in our same process, we should never
	// // see this happen.
	// mBoundService = null;
	// }
	// };
	//
	// void doBindService() {
	// // Can we somehow pass the sensorFileTitle to the service?
	// bindService(new Intent(this, LocalService.class), mConnection,
	// Context.BIND_AUTO_CREATE);
	// mIsBound = true;
	// }
	//
	// void doUnbindService() {
	// if (mIsBound) {
	// // If we have received the service, and hence registered with it,
	// // then now is the time to unregister.
	//
	// // Detach our existing connection.
	// unbindService(mConnection);
	// mIsBound = false;
	// }
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// try {
	// doUnbindService();
	// } catch (Throwable t) {
	// Log.e("MainActivity", "Failed to unbind from the service", t);
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateButtons();
		Context context = this;

		// Start background service
		// use this to start and trigger a service
		// Intent startServiceIntent = new Intent(context, LocalService.class);
		// potentially add data to the intent
		// context.startService(startServiceIntent);

		// TODO
		// Binds to get data, but how do we set how often it binds?
		// doBindService();
	}

	private class newClick implements OnClickListener {
		private String btnString;

		public newClick(String currentButton) {
			btnString = currentButton + ".txt";
		}

		public void onClick(View view) {
			Intent detailsIntent = new Intent(MainActivity.this,
					DetailsActivity.class);
			// view.
			detailsIntent.putExtra(SENSORFILENAME, btnString);
			MainActivity.this.startActivity(detailsIntent);
		}
	};

	private void populateButtons() {

		File f = new File(getExternalFilesDir(null), "");
		File file[] = f.listFiles();
		LinearLayout ll;
		String goodStatus = "Good";
		String fairStatus = "Fair";
		String badStatus = "Requires Attention";

		for (int i = 0; i < file.length; i++) {
			Button sensorButton = new Button(this);

			sensorFileTitle = file[i].getName();

			String sensorTitle = sensorFileTitle.substring(0,
					sensorFileTitle.lastIndexOf('.'));

			sensorButton.setText(sensorTitle);

			ll = (LinearLayout) findViewById(R.id.button_layout);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			sensorButton.setOnClickListener(new newClick(sensorTitle));

			String status = FileHelper.readSpecificFromFile(file[i], "Status");

			if (status != null) {
				Log.d("status main", status);
				if (status.equalsIgnoreCase(goodStatus)) {
					sensorButton.setTextColor(Color.parseColor("#30983a"));
				} else if (status.equalsIgnoreCase(fairStatus)) {
					sensorButton.setTextColor(Color.parseColor("#ffcc00"));
				} else if (status.equalsIgnoreCase(badStatus)) {
					sensorButton.setTextColor(Color.RED);
				}
			}

			sensorButton.setOnClickListener(new newClick(sensorTitle));
			ll.addView(sensorButton, lp);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.add_button) {
			Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
			MainActivity.this.startActivity(addIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
