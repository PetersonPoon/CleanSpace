package com.example.cleanspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	// Click details button to open new activity
	public void openDetailsActivity(View view) {
		Intent detailsIntent = new Intent(MainActivity.this,
				DetailsActivity.class);
		// For future when we need to pass data to DetailsActivity:
		// detailsIntent.putExtra(name, value);
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
