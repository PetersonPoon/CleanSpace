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
			double coData, double timeCollected, String sensorStatus) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToWriteTo, true);
				if (fileToWriteTo != null) {
					String dustString = String.valueOf(dustData);
					String coString = String.valueOf(coData);
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
	 * For reading from file
	 * 
	 * @param readFromFile
	 * @return name and area
	 */
	public static String readNameAndAreaFromFile(File readFromFile) {
		String tempName = null;
		String tempSampleArea = null;
		String sensorNameString = "Sensor Name: ";
		String sensorAreaString = "Sample Area: ";

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
					if (arrayLine.get(i).contains(sensorNameString)) {
						tempName = arrayLine.get(i);
						String[] nameSplit = tempName.split(":");
						tempName = nameSplit[1].trim();

					} else if (arrayLine.get(i).contains(sensorAreaString)) {
						tempSampleArea = arrayLine.get(i);
						String[] areaSplit = tempSampleArea.split(":");
						tempSampleArea = areaSplit[1].trim();
					}

					if (tempName != null && tempSampleArea != null) {
						i = 0;
					}
				}

				inputFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String readData = tempName + "," + tempSampleArea;
		return readData;
	}

	public static String readStatusFromFile(File readFromFile) {
		String currStatus = null;
		String statusString = "Status: ";

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
					if (arrayLine.get(i).contains(statusString)) {
						currStatus = arrayLine.get(i);
						String[] statusSplit = currStatus.split(":");
						currStatus = statusSplit[1].trim();
					}

					if (currStatus != null) {
						i = 0;
					}
				}

				inputFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return currStatus;

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
