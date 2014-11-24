package com.example.cleanspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

public class FileHelper {
	/**
	 * For writing to a file
	 * 
	 * @param fileToWriteTo
	 * @param sensorArea
	 * @return
	 */
	public static boolean writeToNewFile(File fileToWriteTo, String sensorArea) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToWriteTo);
				String sensorFileName = fileToWriteTo.getName();
				String sensorName = sensorFileName.substring(0,
						sensorFileName.lastIndexOf('.'));

				if (sensorName != null && sensorArea != null) {
					fos.write(sensorName.getBytes());
					fos.write("/".getBytes());
					fos.write(sensorArea.getBytes());
					fos.write("/".getBytes());
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
			double coData, double timeCollected) {
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
	 * @return Name and area
	 */
	public static String readFromFile(File readFromFile) {
		// int newLinePassed = 0;
		String tempName = "";
		String tempSampleArea = "";

		String readLine = null;
		if (isExternalStorageReadable()) {
			try {
				FileReader freader = new FileReader(readFromFile);
				BufferedReader inputFile = new BufferedReader(freader);
				while ((readLine = inputFile.readLine()) != null) {
					String[] arrayLine = readLine.split("/");
					tempName = arrayLine[0];
					tempSampleArea = arrayLine[1];
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
