package com.jest.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MotionDatabase {

	// Make sure you edit the DB_PATH variable appropriately; just the package
	// name, i.e. ../com.[your_package_name]/databases/
	// private static String DB_PATH =
	// "/data/data/com.example.sportal/databases/";
	private static String DB_PATH = null;
	private static String DATABASE_NAME = "MOTION_DATABASE";
	public static String TABLE_NAME_LABELS = "MotionLabels";
	public static String TABLE_NAME_DATA = "MotionData";
	public static String TABLE_NAME_SETS = "MotionSets";

	private static final int DATABASE_VERSION = 1;

	private static Context mCtx;
	private static SQLiteDatabase db = null;
	private DatabaseHelper dbHelper = null;

	// For all tables.
	// TODO: refactor this entire file which is a mess...
	private static String[][] columnNames = { { "description", "set_name", "score" }, { "which_label", "dataX", "dataY", "dataZ" },
			{ "set_name", "coeff1", "coeff2" } };

	private static final String TABLE_CREATE_SQL_LABELS = "create table if not exists " + TABLE_NAME_LABELS + " (" + "_id" + " integer primary key autoincrement, "
			+ "set_name" + " text, " + "score" + " integer, " + "description" + " text " + ");";

	private static final String TABLE_CREATE_SQL_DATA = "create table if not exists " + TABLE_NAME_DATA + " (" + "_id" + " integer primary key autoincrement, "
			+ "which_label" + " integer, " + "dataX" + " real, " + "dataY" + " real, " + "dataZ" + " real " + ");";

	private static final String TABLE_CREATE_SQL_SETS = "create table if not exists " + TABLE_NAME_SETS + " (" + "_id" + " integer primary key autoincrement, " + "set_name"
			+ " text, " + "coeff1" + " real, " + "coeff2" + " real " + ");";

	private static final String TABLE_CREATE_ALL[] = { TABLE_CREATE_SQL_LABELS, TABLE_CREATE_SQL_DATA, TABLE_CREATE_SQL_SETS };

	private int tableNameToInt(String tableName) {
		if (tableName.equals(TABLE_NAME_LABELS))
			return 0; // oh god, order matters... this is terrible.
		else if (tableName.equals(TABLE_NAME_DATA))
			return 1;
		else if (tableName.equals(TABLE_NAME_SETS))
			return 2;
		else
			return -1;
	}

	public MotionDatabase(Context c) {
		mCtx = c;
		DB_PATH = c.getFilesDir().getParentFile().getPath() + "/databases/";
		// Toast.makeText(c, "DB_PATH: "+DB_PATH, Toast.LENGTH_LONG).show();
	}

	public MotionDatabase open() throws android.database.SQLException {
		dbHelper = new DatabaseHelper();
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	// NOTE: column values must be passed in as arguments in order corresponding
	// to TABLE_CREATE_SQL
	public long insertItem(String tableName, String... columnValues) {
		ContentValues data = new ContentValues();
		int i;
		int lim = columnValues.length;
		Log.d("INSERTITEM", "lim: " + lim);
		for (i = 0; i < lim; i++)
			data.put(columnNames[tableNameToInt(tableName)][i], columnValues[i]);

		return db.insert(tableName, null, data);
		// returns row, or -1 if there was an error.
	}

	// Monster query method. SEE: DatabaseTestActivity.java for extensive
	// examples.
	// searchCritera of the form { columnName, criterion } & each SQSkvpair can
	// have a negate condition if wanted
	public ArrayList<SQSrow> query(String tableName, SQSkvpair... searchCriteria) {

		String q = "SELECT * FROM " + tableName + " WHERE ";

		int i = 0;
		int len = searchCriteria.length;
		SQSkvpair item;
		while (i < len) {
			item = searchCriteria[i];
			q += item.getColName() + item.getComparator() + "'" + item.getData() + "'" + " AND ";
			i++;
		}
		q += "1"; // finishes the query with correct syntax for extra "and"

		Cursor c = db.rawQuery(q, null);

		// There may be multiple rows. Will return an array of SQSrows, i.e. 2D
		// array with all (row_i, col_j) values
		int numCols = c.getColumnCount();
		ArrayList<SQSrow> results = new ArrayList<SQSrow>();
		int jj;

		while (c.moveToNext()) { // defines a row
			SQSrow row = new SQSrow();
			for (jj = 0; jj < numCols; jj++) {
				row.addData(new SQSkvpair(c.getColumnName(jj), c.getString(jj)));
			}
			results.add(row);
		}

		return results;
	}

	public Cursor rawQuery(String query) { // meh
		return db.rawQuery(query, null);
	}

	// Simple delete method.
	// Example of a delete function... Feel free to create additional ones based
	// pretty much exactly on this.
	public int deleteRowByValue(String tableName, String column, String deleteByThisValue) {
		return db.delete(tableName, column + "=" + DatabaseUtils.sqlEscapeString(deleteByThisValue), null);
	} // returns # of rows affected; Could be many, or zero.

	public boolean isEmpty(String tableName) {

		String q = "SELECT * FROM " + tableName + " WHERE 1";
		Cursor c = db.rawQuery(q, null);

		if (c.getCount() == 0)
			return true;
		return false;

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /// The more exotic SQL methods below here have not been tested but work
	// similar to query, which does work ////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// More advanced delete method
	public void deleteRow(String tableName, SQSkvpair... searchCriteria) {

		String q = "DELETE FROM " + tableName + " WHERE ";

		int i = 0;
		int len = searchCriteria.length;
		SQSkvpair item;
		while (i < len) {
			item = searchCriteria[i];
			q += item.getColName() + item.getComparator() + "'" + item.getData() + "'" + " AND ";
			i++;
		}
		q += "1"; // finishes the query with correct syntax for extra "and"

		db.execSQL(q);

	}

	// Currently available: updating for 1 column at a time
	public void updateRow(String tableName, String colToUpdate, String newValue, SQSkvpair... searchCriteria) {

		String q = "UPDATE " + tableName + " SET " + colToUpdate + "=" + "'" + newValue + "'" + " WHERE ";

		int i = 0;
		int len = searchCriteria.length;
		SQSkvpair item;
		while (i < len) {
			item = searchCriteria[i];
			q += item.getColName() + item.getComparator() + "'" + item.getData() + "'" + " AND ";
			i++;
		}
		q += "1"; // finishes the query with correct syntax for extra "and"

		db.execSQL(q);

	}

	// //////////////////////////////////////// End untested methods
	// ///////////////////////////////////////////////////////

	// Most important method: using an input data file, build table's data for
	// the first time
	// Pass in the file name, e.g. "book1tabs", extension not needed
	// TODO: rework the whichTable/tableName variables. Definitely don't need
	// both.
	void importDataFromFile(String tableName, String file) throws Exception {

		InputStream in = mCtx.getResources().openRawResource(mCtx.getResources().getIdentifier(file, "raw", mCtx.getPackageName()));

		BufferedReader is = new BufferedReader(new InputStreamReader(in, "UTF8"));

		int numCols = columnNames.length;
		String reader = "";
		while ((reader = is.readLine()) != null) {

			String[] RowData = reader.split("\\t");
			ContentValues data = new ContentValues();
			int j;
			for (j = 0; j < numCols; j++) {
				data.put(columnNames[tableNameToInt(tableName)][j], RowData[j]);
			}
			db.insert(tableName, null, data);

		}
		in.close();

	}

	// This is a very important method, says if a particular database exists or
	// not.
	// Run this method when the app is opened. Only create database (prepare
	// tables, call db.open(), etc)
	// when this method returns false.
	private static boolean checkDataBase() {

		return false;
		// Toast.makeText(mCtx, "Calling checkDB", Toast.LENGTH_LONG).show();
		// SQLiteDatabase checkDB = null;
		// try {
		// checkDB = SQLiteDatabase.openDatabase(DB_PATH + DATABASE_NAME, null,
		// SQLiteDatabase.OPEN_READONLY);
		// checkDB.close();
		// } catch (SQLiteException e) {
		// // This should always happen on the first time:
		// Toast.makeText(mCtx, "Creating database for the first time...",
		// Toast.LENGTH_LONG).show();
		// }
		// return checkDB != null ? true : false;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper() {
			super(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
			// Toast.makeText(mCtx, "DatabaseHelper constructor",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Note: this check creates warnings in logcat about database
			// stuff... don't worry
			// e.g. SELECT android_metadata or something like that shows up as
			// an error
			// Toast.makeText(mCtx, "DatabaseHelper onCreate",
			// Toast.LENGTH_LONG).show();
			// if (!checkDataBase()) {
			// Toast.makeText(mCtx, "Creating databases...X",
			// Toast.LENGTH_LONG).show();
			for (String create : TABLE_CREATE_ALL) {
				db.execSQL(create);
			}
			// }
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			// not used, but can upgrade DB versions
		}
	}

}
