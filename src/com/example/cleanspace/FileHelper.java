package com.example.cleanspace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	public static boolean writeToFile(File fileToWriteTo, String sensorArea) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToWriteTo);

				String sensorFileName = fileToWriteTo.getName();
				String sensorName = sensorFileName.substring(0,
						sensorFileName.lastIndexOf('.'));

				if (sensorName != null && sensorArea != null) {
					fos.write(sensorName.getBytes());
					fos.write('\n');
					fos.write(sensorArea.getBytes());
					fos.write('\n');
				}
				fos.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	/**
	 * For reading from file
	 * 
	 * @param readFromFile
	 * @param fis
	 * @return
	 */
	public static String readFromFile(File readFromFile, InputStream fis) {
		int newLinePassed = 0;
		String tempName = "";
		String tempSampleArea = "";
		if (isExternalStorageReadable()) {
			try {

				fis = new BufferedInputStream(new FileInputStream(readFromFile));
				int t = 0;
				while ((t = fis.read()) != -1) {

					if (t != 10 && newLinePassed == 0) {
						tempName = tempName + Character.toString((char) t);
					} else if (t == 10) {
						newLinePassed = 1;
					} else if (newLinePassed == 1) {
						tempSampleArea = tempSampleArea
								+ Character.toString((char) t);
					}
				}
				newLinePassed = 0;
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String readData = tempName + "," + tempSampleArea;
		return readData;
	}

	/**
	 * To append a file with new data from a sensor
	 * 
	 * @param fileToAppend
	 * @param appendWith
	 * @return
	 */
	public static boolean appendFile(File fileToAppend, String appendWith) {
		if (isExternalStorageWriteable()) {
			try {
				OutputStream fos = new FileOutputStream(fileToAppend);

				if (fileToAppend.exists()) {
					fos.write(appendWith.getBytes());
					fos.write('\n');
				}
				fos.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;
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
