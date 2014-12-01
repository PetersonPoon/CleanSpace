package com.example.cleanspace;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

public class GraphActivity extends Activity {

	String sensorFileName;
	File readFromFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		Intent intent = getIntent();
		if (null != intent) {
			sensorFileName = intent.getStringExtra(SENSORFILENAME);
		}
		readFromFile = new File(getExternalFilesDir(null), sensorFileName);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			return true;
		}
		super.onBackPressed();
		return true;
	}

	public void graphDustData() {

		ArrayList<String> graphData = FileHelper.readSpecificForGraph(
				readFromFile, "Dust");

		// Do some graphing stuff
	}

	public void graphCoData() {

		ArrayList<String> graphData = FileHelper.readSpecificForGraph(
				readFromFile, "CO");

		// Do some graphing stuff
	}

	public void graphHumidityData() {

		ArrayList<String> graphData = FileHelper.readSpecificForGraph(
				readFromFile, "Humidity");

		// Do some graphing stuff
	}

	public void graphTempData() {

		ArrayList<String> graphData = FileHelper.readSpecificForGraph(
				readFromFile, "Temperature");

		// Do some graphing stuff
	}
}
