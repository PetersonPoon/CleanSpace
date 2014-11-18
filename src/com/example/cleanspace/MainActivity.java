package com.example.cleanspace;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * This activity will display the general details of our one sensor Details
 * include: Name, dust condition (good/bad)
 * 
 * FUTURE: Display details on more than one sensor
 * 
 * @author echiang
 * 
 */
public class MainActivity extends Activity {

	String sensorTitle = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateButtons();
	}

	private void populateButtons() {

		File f = new File(getExternalFilesDir(null), "");
		File file[] = f.listFiles();

		for (int i = 0; i < file.length; i++) {
			Button sensorButton = new Button(this);

			sensorButton.setId(i);
			sensorTitle = file[i].getName();
			sensorButton.setText(sensorTitle);

			LinearLayout ll = (LinearLayout) findViewById(R.id.button_layout);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			ll.addView(sensorButton, lp);

			sensorButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					Intent detailsIntent = new Intent(MainActivity.this,
							DetailsActivity.class);
					detailsIntent.putExtra(SENSORFILENAME, sensorTitle);
					Log.d("here", sensorTitle);
					MainActivity.this.startActivity(detailsIntent);
				}

			});
		}
	}

	// Click details button to open new activity
	public void openDetailsActivity(View view) {
		Intent detailsIntent = new Intent(MainActivity.this,
				DetailsActivity.class);
		detailsIntent.putExtra(SENSORFILENAME, sensorTitle);
		MainActivity.this.startActivity(detailsIntent);
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
		} else if (id == R.id.refresh_button) {
			// TODO: Code here to get new data
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
