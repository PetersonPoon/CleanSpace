package com.example.cleanspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

public class EditActivity extends Activity {
	public static final String EDITEDTITLE = "temp";
	public static final String EDITEDSAMPLEAREA = "123";

	String oldSensorTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		String oldSampleArea = "";

		Intent intent = getIntent();
		if (null != intent) {
			oldSensorTitle = intent.getStringExtra(EDITEDTITLE);

			oldSampleArea = intent.getStringExtra(EDITEDSAMPLEAREA);

			EditText LoadSensorTitle = (EditText) findViewById(R.id.sensor_title);
			LoadSensorTitle.setText(oldSensorTitle);

			EditText LoadSampleArea = (EditText) findViewById(R.id.current_area);
			LoadSampleArea.setText(oldSampleArea);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			onBackPressed();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveEditActivity(View view) {
		EditText newSensorTitle = (EditText) findViewById(R.id.sensor_title);
		String newTitle = newSensorTitle.getText().toString().trim();

		EditText sampleArea = (EditText) findViewById(R.id.current_area);
		String newSampleArea = sampleArea.getText().toString().trim();

		String fileName = oldSensorTitle + ".txt";// getExternalFilesDir();
		File openSaveFile = new File(getExternalFilesDir(null), fileName);

		File renameSaveFile = new File(getExternalFilesDir(null), newTitle
				+ ".txt");

		if (newTitle == null || newTitle.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please enter a valid name", Toast.LENGTH_SHORT).show();
		} else if (newSampleArea == null || newSampleArea.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please enter a valid area", Toast.LENGTH_SHORT).show();
		} else {
			if (openSaveFile.exists()) {
				openSaveFile.renameTo(renameSaveFile);
			}

			boolean successfulWrite = FileHelper.writeToNewFile(renameSaveFile,
					newSampleArea);
			if (successfulWrite) {
				Toast.makeText(getApplicationContext(),
						"Sensor has been successfully edited",
						Toast.LENGTH_SHORT).show();

				Intent detailsIntent = new Intent(EditActivity.this,
						DetailsActivity.class);

				detailsIntent.putExtra(SENSORFILENAME, newTitle + ".txt");

				EditActivity.this.startActivity(detailsIntent);
			}
		}

	}
}
