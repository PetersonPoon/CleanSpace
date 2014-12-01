package com.example.cleanspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class FileHelper {
	/**
	 * For writing to a file
	 * 
	 * @param fileToWriteTo
	 * @param sensorArea
	 * @param newFile
	 *            boolean : if it is just an edit to name or area, prefix with
	 *            Edit area
	 * @return
	 */
	public static boolean writeToNewFile(File fileToWriteTo, String sensorArea,
			Boolean newFile) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToWriteTo, true);
				String sensorFileName = fileToWriteTo.getName();
				String sensorName = sensorFileName.substring(0,
						sensorFileName.lastIndexOf('.'));

				if (sensorName != null && sensorArea != null) {
					if (newFile == true) {
						fos.write("Sensor Name: ".getBytes());
						fos.write(sensorName.getBytes());
						fos.write("/".getBytes());
						fos.write("Sample Area: ".getBytes());
						fos.write(sensorArea.getBytes());
						fos.write("/".getBytes());
					} else {
						fos.write("Edited Sensor Name: ".getBytes());
						fos.write(sensorName.getBytes());
						fos.write("/".getBytes());
						fos.write("Edited Sample Area: ".getBytes());
						fos.write(sensorArea.getBytes());
						fos.write("/".getBytes());
					}
					fos.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	/**
	 * To append a file with new data from a sensor
	 * 
	 * @param fileToAppend
	 * @param appendWith
	 * @return
	 */
	public static void appendFile(File fileToWriteTo, double dustData,
			double coData, double humidityData, double temperatureData,
			double timeCollected, String sensorStatus) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToWriteTo, true);
				if (fileToWriteTo != null) {
					String dustString = String.valueOf(dustData);
					String coString = String.valueOf(coData);
					String humidityString = String.valueOf(humidityData);
					String temperatureString = String.valueOf(temperatureData);
					String timeString = String.valueOf(timeCollected);

					fos.write("Time Collected: ".getBytes());
					fos.write(timeString.getBytes());
					fos.write("/".getBytes());
					fos.write("Dust Data: ".getBytes());
					fos.write(dustString.getBytes());
					fos.write("/".getBytes());
					fos.write("CO Data: ".getBytes());
					fos.write(coString.getBytes());
					fos.write("/".getBytes());
					fos.write("Humidity Data: ".getBytes());
					fos.write(humidityString.getBytes());
					fos.write("/".getBytes());
					fos.write("Temperature Data: ".getBytes());
					fos.write(temperatureString.getBytes());
					fos.write("/".getBytes());
					fos.write("Status: ".getBytes());
					fos.write(sensorStatus.getBytes());
					fos.write("/".getBytes());
					fos.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return most recent data field from file. (End of file) given a tag to
	 * search for and file name
	 * 
	 * @param readFromFile
	 * @param specificDataToGet
	 * @return
	 */
	public static String readSpecificFromFile(File readFromFile,
			String specificDataToGet) {
		String tagToParseFor = null;
		String sensorNameString = "Sensor Name: ";
		String sensorAreaString = "Sample Area: ";
		String dustString = "Dust Data: ";
		String coString = "CO Data: ";
		String humidityString = "Humidity Data: ";
		String temperatureString = "Temperature Data: ";
		String statusString = "Status: ";
		// For future use when graphing String timeString = "Time Collected: ";

		if (specificDataToGet.equals("Name")) {
			tagToParseFor = sensorNameString;
		} else if (specificDataToGet.equalsIgnoreCase("Area")) {
			tagToParseFor = sensorAreaString;
		} else if (specificDataToGet.equalsIgnoreCase("Dust")) {
			tagToParseFor = dustString;
		} else if (specificDataToGet.equalsIgnoreCase("CO")) {
			tagToParseFor = coString;
		} else if (specificDataToGet.equalsIgnoreCase("Humidity")) {
			tagToParseFor = humidityString;
		} else if (specificDataToGet.equalsIgnoreCase("Temperature")) {
			tagToParseFor = temperatureString;
		} else if (specificDataToGet.equalsIgnoreCase("Status")) {
			tagToParseFor = statusString;
		}
		String foundString = null;

		String readLine;
		String[] line;

		if (isExternalStorageReadable()) {
			try {
				FileReader freader = new FileReader(readFromFile);
				BufferedReader inputFile = new BufferedReader(freader);

				List<String> arrayLine = new ArrayList<String>();

				while ((readLine = inputFile.readLine()) != null) {
					line = readLine.split("/");
					for (int k = 0; k < line.length; k++) {
						arrayLine.add(line[k]);
					}
				}

				for (int i = arrayLine.size() - 1; i >= 0; i--) {
					if (arrayLine.get(i).contains(tagToParseFor)) {
						foundString = arrayLine.get(i);
						String[] statusSplit = foundString.split(":");
						foundString = statusSplit[1].trim();
					}
					// Only take the name at the bottom of list, stop loop after
					// it is found
					if (foundString != null) {
						i = 0;
					}
				}

				inputFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return foundString;
	}

	public static ArrayList<String> readSpecificForGraph(File readFromFile,
			String specificDataToGet) {

		String tagToParseFor = null;
		String sensorNameString = "Sensor Name: ";
		String sensorAreaString = "Sample Area: ";
		String dustString = "Dust Data: ";
		String coString = "CO Data: ";
		String humidityString = "Humidity Data: ";
		String temperatureString = "Temperature Data: ";
		String statusString = "Status: ";
		String timeString = "Time Collected: ";

		if (specificDataToGet.equals("Name")) {
			tagToParseFor = sensorNameString;
		} else if (specificDataToGet.equalsIgnoreCase("Area")) {
			tagToParseFor = sensorAreaString;
		} else if (specificDataToGet.equalsIgnoreCase("Dust")) {
			tagToParseFor = dustString;
		} else if (specificDataToGet.equalsIgnoreCase("CO")) {
			tagToParseFor = coString;
		} else if (specificDataToGet.equalsIgnoreCase("Humidity")) {
			tagToParseFor = humidityString;
		} else if (specificDataToGet.equalsIgnoreCase("Temperature")) {
			tagToParseFor = temperatureString;
		} else if (specificDataToGet.equalsIgnoreCase("Status")) {
			tagToParseFor = statusString;
		}

		String readLine;
		String[] line;
		ArrayList<String> graphArray = null;

		if (isExternalStorageReadable()) {
			try {
				FileReader freader = new FileReader(readFromFile);
				BufferedReader inputFile = new BufferedReader(freader);

				List<String> arrayLine = new ArrayList<String>();

				while ((readLine = inputFile.readLine()) != null) {
					line = readLine.split("/");
					for (int k = 0; k < line.length; k++) {
						arrayLine.add(line[k]);
					}
				}

				// This is for graphing. Go through list of data with no
				// slashes, take the tag we need and the time
				graphArray = new ArrayList<String>();
				for (int i = 0; i >= arrayLine.size(); i++) {
					if (arrayLine.get(i).contains(tagToParseFor)
							|| arrayLine.get(i).contains(timeString)) {
						graphArray.add(arrayLine.get(i));
					}
				}
				inputFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d("graphdata", graphArray.toString());
		return graphArray;
	}

	public static boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
