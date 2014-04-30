package com.jest.getdata;

import java.util.ArrayList;

/**
 * The user and all associated data
 * @author Vivek
 *
 */
public class User {

	private static int CODE;
	
	/**
	 * Unique ID for the user
	 */
	int id;

	/**
	 * The identifying feature of the user
	 */
	String username, password, email;
	
	/**
	 * The user's owned/installed software packages
	 */
	ArrayList<String> ownedPackages, installedPackages;
	
	/**
	 * The user's dataPoints, grouped by Type of motion
	 */
	private ArrayList<DataType> sportList;
	
	private static User currentUser;
	
	// private, so only one user at a time
	private User() {
		ownedPackages = new ArrayList<String>();
		installedPackages = new ArrayList<String>();
		sportList = new ArrayList<DataType>();
	}
	
	/**
	 * Get user instance
	 * @return current user
	 */
	public static User get() {
		if (currentUser == null) currentUser = new User();
		return currentUser;
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
	 * Get all motion data associated with the user 
	 * @return			The user's dataPoints, grouped by Type of motion
	 */
	public final ArrayList<DataType> getAllMotion() {
		return sportList;
	}
	
	/**
	 * Get the DataType including all associated DataPoints for a specific motion type
	 * @param	type	The specific motion type
	 * @return			The DataType associated with type, or null if user has no data of that type
	 */
	public final DataType getMotion(int type) {
		for (DataType dt : sportList)
			if (dt.getType() == type) return dt;
		return null;
	}

	/**
	 * Get the data for a specific motion type
	 * @param	type	The specific motion type
	 * @return			The DataPoints associated with type, or null if user has no data of that type
	 */
	public final ArrayList<DataPoint> getData(int type) {
		DataType dt = getMotion(type);
		if (dt != null) return dt.getData();
		return null;
	}
	
	/**
	 * Add a DataPoint to the User
	 * @param 	dp		The DataPoint to add
	 * @param 	code	the code to restrict access to this function
	 */
	public void addData(DataPoint dp, int code) {
		if (code != CODE) throw new Error("Incorrect code");
		DataType dt = getMotion(dp.getType());
		if (dt != null) dt.add(dp,code);
		else sportList.add(new DataType(dp));
	}
	
	/**
	 * Sort all DataPoints by date and time recorded
	 * @param 	code	the code to restrict access to this function
	 */
	public void sortAllData(int code) {
		if (code != CODE) throw new Error("Incorrect code");
		for (DataType dt : sportList)
			dt.sort(code);
	}
	
}
