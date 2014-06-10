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
	 * @param	c		The context provided
	 */
	public static void initialize(Context c) {
		context = c;
		new DataHelper();
	}
	
	/**
	 * Set the current user from credentials - currently only works for admin user
	 * @param name		The username associated with the user
	 * @param password	The password associated with the user
	 * @return			true if the user is in the database and was set to the current user
	 * 					false if the user is not in the database
	 */
	public static boolean setUser(String name, String password) {
		if (!name.equals("admin") || !password.equals("admin")) return false;
		User.get().setCredentials(1001, name, password, "test@onyxmotion.com",
			CODE);
		return true;
	}
	
	/**
	 * Adds a DataPoint to the user from nine parameters and sorts the DataPoints
	 * @param	v1		The motion id
	 * @param 	v2		The motion sport
	 * @param 	v3		The motion type
	 * @param 	v4		The time in milliseconds from January 1, 1970
	 * @param 	v5		The score
	 * @param 	v6		The elbow angle
	 * @param 	v7		The release angle
	 * @param 	v8		The release speed
	 * @param 	v9		Not used by this function yet
	 */
	public static void addDataToUser(float v1, float v2, float v3, float v4,
		float v5, float v6, float v7, float v8, float v9) {
		
		DataPoint dp = new DataPoint(CODE);
		int[] ints = {Math.round(v1),Math.round(v2),Math.round(v3)};
		long time = (long) Math.round(v4);
		float[] measurements = {v5, v6, v7, v8};
		dp.setVarInput(ints, measurements, time);
		
		User.get().sortAllData(CODE);
		
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
			line = "" + 1002 + t + 0 + t + 0 + t + 30.2 + t + 23.6543 + t + 12.498 + t + -2.41 + t +cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			cal.add(Calendar.DAY_OF_YEAR,11);
			line = "" + 1003 + t + 0 + t + 0 + t + 33.7 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 35.9 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 39.8 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 46.4 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 52.1 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 56.8 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 60.2 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 63.4 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 69.2 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			line = "" + 1003 + t + 0 + t + 0 + t + 73.1 + t + -12.31 + t + 0. + t + 9.42 + t + cal.getTimeInMillis() + "\n";
			fos.write(line.getBytes());
			
			fos.close();
		} catch(Exception e) {
			Log.e("ERROR",e.toString());
		}
	}
	
	/**
	 * Initializes User with test DataPoints written to and read from file
	 * @param	c		The context required to perform File IO
	 */
	public static void testFileIO(Context c) {
		initialize(c);
		writeTestDataToFile();
		readAllDataFromFile();
	}
	
}
