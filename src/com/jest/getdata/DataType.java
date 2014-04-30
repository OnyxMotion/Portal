package com.jest.getdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;

/**
 * The motion type that contains a list of associated DataPoints
 * @author Vivek
 *
 */
public class DataType {

	private static int CODE;
	
	/**
	 * The unique identifier for the sport
	 */
	public static final int
		BASKETBALL = 0,
		GOLF = 1,
		TENNIS = 2;
	// Add additional sports here, increasing the index for each new sport
	
	/**
	 * The unique identifier for the type of motion
	 */
	public static final int
		BB_FREETHROW = 0,
		BB_3POINT = 1,
		BB_LAYUP = 2;
	// Add additional motions here, increasing the index for each new type

	private int sport, type;
	
	private ArrayList<DataPoint> data;
	
	// Probably should have other fields, like level (beginner vs advanced)
	
	/**
	 * Create a new DataType from a DataPoint within it
	 * @param dp	The DataPoint whose category is the new DataType
	 */
	public DataType(DataPoint dp) {
		sport = dp.getSport();
		type = dp.getType();
		data = new ArrayList<DataPoint>();
		data.add(dp);
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
	 * Get the DataType's associated sport
	 * @return		The DataType's associated sport
	 */
	public final int getSport() {
		return sport;
	}
	
	/**
	 * Get the DataType's associated motion type and ID
	 * @return		The DataType's associated motion type and ID
	 */
	public final int getType() {
		return type;
	}
	
	/**
	 * Get all DataPoints for this DataType's motion type
	 * @return		The list of all DataPoints
	 */
	public final ArrayList<DataPoint> getData() {
		return data;
	}
	
	/**
	 * Add a DataPoint to the list
	 * @param 	dp		the DataPoint to add
	 * @param 	code	the code to restrict access to this function
	 */
	public void add(DataPoint dp, int code) {
		if (code != CODE) throw new Error("Incorrect code");
		data.add(dp);
	}
	
	/**
	 * Sort the DataPoints by date and time recorded and remove duplicates
	 * @param 	code	the code to restrict access to this function
	 */
	public void sort(int code) {
		if (code != CODE) throw new Error("Incorrect code");
		Collections.sort(data, new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint dp1, DataPoint dp2) {
				int temp = dp1.getCalendar().compareTo(dp2.getCalendar());
				if (temp != 0) return temp;
				else return dp2.getID() - dp1.getID();
			}
		});
		
		LinkedHashSet<DataPoint> setItems = new LinkedHashSet<DataPoint>(data);
		data.clear();
		data.addAll(setItems);
	}
	
}
