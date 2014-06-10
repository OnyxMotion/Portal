package com.jest.getdata;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * The DataPoint for a single motion including all measurements
 * @author Vivek
 *
 */
public class DataPoint {

	private int id, sport, type;

	private Calendar calendar;
	
	private static int CODE;
	
	/**
	 * The identifier of the specific measurement associated with the DataPoint
	 */
	public static final int 
		SCORE = 0,
		ELBOW_ANGLE = 1,
		RELEASE_ANGLE = 2,
		RELEASE_SPEED = 3;
	// Add additional data associated with the DataPoint here, increase the number
	// Also increase the size of the data array below, that stores the measurements
	private static final int DATA_SIZE = 4;
	private float [] data;
	
	/**
	 * Create a new DataPoint with today's date and zero values for all measurements
	 * @param 	code		The code ensuring the function is only called by DataHelper	
	 */
	public DataPoint(int code) {
		if (code != CODE) throw new Error("Incorrect code");
		calendar = Calendar.getInstance();
		data = new float[DATA_SIZE];
	}
	
	/**
	 * Set the CODE to value - only called from DataHelper.DataHelper()
	 * @param 	value	The value to set the CODE
	 * @param 	dh		The DataHelper instance, ensuring function is only called from DataHelper
	 */
	public static void setCode(int value, DataHelper dh) {
		if (dh == null) throw new Error("Incorrect call of setCode()");
		CODE = value;
	}
	
	/**
	 * Get the unique identifier for the DataPoint
	 * @return				The DataPoint's unique identifier
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Get the DataPoint's associated sport
	 * @return				The DataPoint's associated sport
	 */
	public final int getSport() {
		return sport;
	}
	
	/**
	 * Get the DataPoint's associated motion type
	 * @return				The DataPoint's associated motion type
	 */
	public final int getType() {
		return type;
	}
	
	/**
	 * Get the date and time on which the DataPoint was generated
	 * @return				The DataPoint's date and time
	 */
	public final Calendar getCalendar() {
		return calendar;
	}
	
	/**
	 * Get the measurement for this DataPoint
	 * @param 	measurement the measurement to return
	 * @return 				the measurement value
	 * @throws 	Error 		if measurement parameter is not valid
	 */
	public final double get(int measurement) {
		if (measurement < 0 || measurement >= DATA_SIZE)
			throw new Error("Measurement incorrect in DataPoint.get()");
		return data[measurement];
	}
	
	/**
	 * Set the DataPoint member variables from input arrays
	 * @param 	ints		The id, sport and type associated with the DataPoint
	 * @param 	measures	The data associated with the DataPoint
	 * @param 	time		Milliseconds from Jan 1 1970 associated with the DataPoint
	 */
	public void setVarInput(int [] ints, float [] measures, long time) {
		id = ints[0];
		sport = ints[1];
		type = ints[2];
		calendar.setTimeInMillis(time);
		for (int i = 0; i < DataPoint.DATA_SIZE; i++)
			data[i] = measures[i];
			
	}
	
	/**
	 * Set the DataPoint member variables from file input
	 * @param 	token		the input file string
	 * @param 	code		the code to restrict access to this function
	 */
	public void setFileInputLine(StringTokenizer token, int code) {
		if (code != CODE) throw new Error("Incorrect code");
		id = Integer.parseInt(token.nextToken());
		sport = Integer.parseInt(token.nextToken());
		type = Integer.parseInt(token.nextToken());
		for (int i = 0; i < DataPoint.DATA_SIZE; i++)
			data[i] = Float.parseFloat(token.nextToken());
		calendar.setTimeInMillis(Long.parseLong(token.nextToken()));
	}
	
	/**
	 * Get the DataPoint as a string to be put in an output file
	 * @param 	code		the code to restrict access to this function
	 * @return				the string to be put in an output file
	 */
	public final String getFileOutputLine(int code) {
		if (code != CODE) throw new Error("Incorrect code");
		String line, t = "\t";
		line = "" + id + t + sport + t + type + t;
		for (int i = 0; i < DataPoint.DATA_SIZE; i++)
			line += "" + data[i] + t;
		line += "" + calendar.getTimeInMillis() + "\n";
		return line;
	}
}
