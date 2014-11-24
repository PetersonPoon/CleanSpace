package com.example.cleanspace;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateButtons();
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

		for (int i = 0; i < file.length; i++) {
			Button sensorButton = new Button(this);

			sensorFileTitle = file[i].getName();

			String sensorTitle = sensorFileTitle.substring(0,
					sensorFileTitle.lastIndexOf('.'));

			sensorButton.setText(sensorTitle);

			LinearLayout ll = (LinearLayout) findViewById(R.id.button_layout);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

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
