package com.jest.getdata;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

/**
 * A static class that handles all edits to User data
 * @author Vivek
 *
 */
public class DataHelper {

	private static final int CODE = 1234567890;
	
	private static Context context;
	
	private DataHelper() {
		User.setCode(CODE, this);
		DataType.setCode(CODE, this);
		DataPoint.setCode(CODE, this);
	}
	
	/**
	 * Provides a context to the DataHelper so that it can perform File I/O
	 * @param	c	The context provided
	 */
	public static void initialize(Context c) {
		context = c;
		new DataHelper();
	}
	
	/**
	 * Adds all DataPoints from file to User
	 */
	public static void readAllDataFromFile() {
		if (context == null) throw new Error("DataHelper not initialized");
		
		DataPoint currentPoint;
		String line; 
		User user = User.get();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
				context.openFileInput("com_vkapps")));
			while ((line = br.readLine()) != null) {
		    	currentPoint = new DataPoint(CODE);
		    	currentPoint.setFileInputLine(
		    		new StringTokenizer(line,"\t"), CODE);
		    	user.addData(currentPoint, CODE);
		    }
		    br.close();
		} catch(Exception e) {
			Log.e("ERROR",e.toString());
		}
		
		user.sortAllData(CODE);
	}
	
	/**
	 * Saves all User DataPoints to file
	 */
	public static void writeAllDataToFile() {
		if (context == null) throw new Error("DataHelper not initialized");
		try {
			FileOutputStream fos = context.openFileOutput(
				"com_vkapps",Context.MODE_PRIVATE);
			for (DataType dt : User.get().getAllMotion())
				for (DataPoint dp : dt.getData())
					fos.write(dp.getFileOutputLine(CODE).getBytes());
			fos.close();
		} catch(Exception e) {
			Log.e("ERROR",e.toString());
		}
	}
	
	/**
	 * Deletes all User DataPoints from file
	 */
	public static void clearAllDataFromFile() {
		if (context == null) throw new Error("DataHelper not initialized");
		try {
			FileOutputStream fos = context.openFileOutput(
				"com_vkapps",Context.MODE_PRIVATE);
			fos.write("".getBytes());
			fos.close(); 
		} catch(Exception e) {
			Log.e("ERROR",e.toString());
		}
	}
	
	/**
	 * Saves test data to a file to then be read by readAllDataFromFile()
	 */
	public static void writeTestDataToFile() {
		if (context == null) throw new Error("DataHelper not initialized");
		String line, t = "\t";
		Calendar cal = Calendar.getInstance();
		try {
			FileOutputStream fos = context.openFileOutput(
				"com_vkapps",Context.MODE_PRIVATE);
			
//			Test Data Format:
//			line = "" + id + t + sport + t + type + t;
//			for (int i = 0; i < DataPoint.DATA_SIZE; i++)
//				line += "" + data[i] + t;
//			line += "" + calendar.getTimeInMillis() + "\n";
			
			line = "" + 1001 + t + 0 + t + 0 + t + 24.3 + t + 38.1235 + t + -9.342 + t + 0. + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			cal.add(Calendar.DAY_OF_YEAR,6);
			line = "" + 1002 + t + 0 + t + 0 + t + 2.123 + t + 23.6543 + t + 12.498 + t + -2.41 + t +cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			cal.add(Calendar.DAY_OF_YEAR,11);
			line = "" + 1003 + t + 0 + t + 0 + t + -31.21 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			fos.close();
		} catch(Exception e) {
			Log.e("ERROR",e.toString());
		}
	}
	
	/**
	 * Initializes User with test DataPoints written to and read from file
	 * @param	c	The context required to perform File IO
	 */
	public static void testFileIO(Context c) {
		initialize(c);
		writeTestDataToFile();
		readAllDataFromFile();
	}
	
}
