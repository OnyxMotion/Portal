package com.jest.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

//Talk to the manager.
public class MotionDatabaseManager {

	private MotionDatabase mDb;
	private Context mContext;

	public MotionDatabaseManager(Context context) {
		mContext = context;
		mDb = new MotionDatabase(mContext);
		mDb.open();
		//Toast.makeText(mContext, "Creating MDM", Toast.LENGTH_LONG).show();
	}

	public long createMotionLabel(String description, String set, int score) {
		return mDb.insertItem(MotionDatabase.TABLE_NAME_LABELS, description, set, Integer.toString(score));
	}

	public long createMotionSet(String set_name, float coeff1, float coeff2) {
		return mDb.insertItem(MotionDatabase.TABLE_NAME_SETS, set_name, Float.toString(coeff1), Float.toString(coeff2));
	}

	public ArrayList<String> getSets() {
		String query = "SELECT * FROM " + MotionDatabase.TABLE_NAME_SETS + " WHERE 1";
		Cursor c = mDb.rawQuery(query);
		int len = c.getCount();
		ArrayList<String> setNames = new ArrayList<String>();
		//oh god this is terrible:
		while (c.moveToNext()) {
			setNames.add(c.getString(c.getColumnIndex("set_name")));
		}
		return setNames;
	}

	// Returns all motion labels of a particular motion set (e.g. when using a
	// set for training)
	public ArrayList<MotionLabel> getMotionLabelsBySet(String set_name) {
		String query = "SELECT * FROM " + MotionDatabase.TABLE_NAME_LABELS + " WHERE set_name=" + set_name;
		Cursor c = mDb.rawQuery(query);
		int len = c.getCount();
		ArrayList<MotionLabel> labels = new ArrayList<MotionLabel>();
		while (c.moveToNext()) { // defines a row
			labels.add(new MotionLabel(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("description")), c.getString(c
					.getColumnIndex("set_name")), c.getInt(c.getColumnIndex("score"))));
		}
		return labels;
	}

	public boolean doesSetExist(String set_name) {
		String query = "SELECT * FROM " + MotionDatabase.TABLE_NAME_SETS + " WHERE set_name="+"'"+set_name+"'";
		Cursor c = mDb.rawQuery(query);
		int len = c.getCount();
		if (len > 0)
			return true;
		return false;
	}

	public void updateMotionSetCoeffs(String set_name, float coeff1, float coeff2) {
		String q = "UPDATE " + MotionDatabase.TABLE_NAME_SETS + " SET coeff1=" + coeff1 + ",coeff2=" + coeff2 + " WHERE set_name="+"'"+set_name+"'";
		mDb.rawQuery(q);
	}

	public ArrayList<MotionLabel> getMotionLabels() {

		String query = "SELECT * FROM " + MotionDatabase.TABLE_NAME_LABELS + " WHERE 1";
		Cursor c = mDb.rawQuery(query);
		int len = c.getCount();
		ArrayList<MotionLabel> labels = new ArrayList<MotionLabel>();
		while (c.moveToNext()) { // defines a row
			labels.add(new MotionLabel(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("description")), c.getString(c
					.getColumnIndex("set_name")), c.getInt(c.getColumnIndex("score"))));
		}
		return labels;
	}

	// row: from the above. Typical flow would involve:
	// long row = createMotionLabel(desc), addMotionData(row, dataArray)
	public boolean addMotionData(long row, float[] dataArrayX, float[] dataArrayY, float[] dataArrayZ) {
		long insertTest = 0;
		int len = dataArrayX.length;
		for (int i = 0; i < len; i++) {
			insertTest = mDb.insertItem(MotionDatabase.TABLE_NAME_DATA, String.valueOf(row), String.valueOf(dataArrayX[i]), String.valueOf(dataArrayY[i]),
					String.valueOf(dataArrayZ[i]));
			if (insertTest == -1) {
				return false; // didn't work, we have a problem
			}
		}
		return true;
	}

	public DataArrays getMotionData(long row) {
		ArrayList<Float> returnDataX = new ArrayList<Float>();
		ArrayList<Float> returnDataY = new ArrayList<Float>();
		ArrayList<Float> returnDataZ = new ArrayList<Float>();
		// WARNING - hardcoded strings
		// TODO - fix and refactor...
		String query = "SELECT * FROM " + MotionDatabase.TABLE_NAME_DATA + " WHERE which_label =" + row;
		Cursor c = mDb.rawQuery(query);
		while (c.moveToNext()) { // defines a row
			returnDataX.add(c.getFloat(c.getColumnIndex("dataX")));
			returnDataY.add(c.getFloat(c.getColumnIndex("dataY")));
			returnDataZ.add(c.getFloat(c.getColumnIndex("dataZ")));
		}
		DataArrays dat = new DataArrays(returnDataX, returnDataY, returnDataZ);
		return dat;
	}

	public int deleteMotionData(long row) {
		int x = mDb.deleteRowByValue(MotionDatabase.TABLE_NAME_LABELS, "_id", String.valueOf(row));
		int y = mDb.deleteRowByValue(MotionDatabase.TABLE_NAME_DATA, "which_label", String.valueOf(row));
		return x + y; // should correspond to size of motion data being deleted,
						// +1 for the entry in label table
	}

	public class DataArrays {
		public ArrayList<Float> dataX;
		public ArrayList<Float> dataY;
		public ArrayList<Float> dataZ;

		public DataArrays(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
			this.dataX = x;
			this.dataY = y;
			this.dataZ = z;
		}

	}

	public class MotionLabel {
		public long row;
		public String description;
		public String set;
		public int score;

		public MotionLabel(long r, String desc, String set, int score) {
			this.row = r;
			this.description = desc;
			this.set = set;
			this.score = score;
		}
	}

	public void close() {
		mDb.close();
	}

}
