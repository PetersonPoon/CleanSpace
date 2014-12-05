package com.example.cleanspace;

import static com.example.cleanspace.DetailsActivity.SENSORFILENAME;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

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

		TextView sensorName = (TextView) findViewById(R.id.sensor_title);

		String gotSensorName = FileHelper.readSpecificFromFile(readFromFile,
				"Name");

		sensorName.setText(gotSensorName);

		graphDustData();
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

		GraphViewData[] plotData = new GraphViewData[graphData.size() / 2];
		int plotSize = 0;
		double x = 0;
		for (int i = 0; i < graphData.size() - 1; i = i + 2) {
			int j = 0;
			j = i + 1;

			String[] dustVal;
			String[] timeVal;
			String dustSplit = graphData.get(j);
			String timeSplit = graphData.get(i);

			dustVal = dustSplit.split(": ");
			timeVal = timeSplit.split(": ");

			double dustReading = Double.parseDouble(dustVal[1]);
			double time = Double.parseDouble(timeVal[1]);
//			String format = formatLabel(time, true);
//			Log.d("Format", format);

			plotData[plotSize] = new GraphViewData(time, dustReading);
			plotSize++;
			x++;
		}

		/*
		 * GraphViewSeries dustPlot = new GraphViewSeries(new GraphViewData[] {
		 * new GraphViewData(1, 3), new GraphViewData(2, 1.5), new
		 * GraphViewData(3, 5) });
		 */

		// GraphViewSeries dustPlot = new GraphViewSeries(new
		// GraphViewSeries(plotData));

		GraphView graphView = new LineGraphView(this, "Dust Levels");

		graphView.addSeries(new GraphViewSeries(plotData));

		LinearLayout layout = (LinearLayout) findViewById(R.id.plot1);
		layout.addView(graphView);
		// Do some graphing stuff
	}

}
// final java.text.DateFormat dateTimeFormatter = DateFormat
// .getTimeFormat(getApplicationContext());
//
// protected String formatLabel(double value, boolean isValueX) {
// if (isValueX) {
// // transform number to time
// return dateTimeFormatter.format(new Date((long) value * 1000));
// } else {
// return super.formatLabel(value, isValueX);
// }
// }
// };
