package com.jest.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jest.jest.R;

public class BuildMotionSet extends Activity {

	private TextView title;
	private LinearLayout parent;

	private TextView[] comparisonScores;
	private EditText[] labelledScores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.build_motion_set);

		Intent intent = getIntent();
		String setName = intent.getStringExtra("set");

		parent = (LinearLayout) findViewById(R.id.build_motion_set_parent);

		title = (TextView) findViewById(R.id.build_motion_set_title);
		title.setText("Building set for: " + setName);

		// TODO
		/*
		 * 1. get all the motion data we need for every label in the set:
		 * 
		 * (do this for all the rows of labels with the given set name, probably
		 * need to make extra method in MotionDatabaseManager) DataArrays data =
		 * MainActivity.MDM.getMotionData(row); int len = data.dataX.size();
		 * ArrayList<Float> x = data.dataX; ArrayList<Float> y = data.dataY;
		 * ArrayList<Float> z = data.dataZ;
		 * 
		 * 2. do all the cross-correlation analysis BUT BEFORE 3, display at the
		 * top the min and max values so the "scorer/debugger" has some idea 3.
		 * display UI with the results (x,y,z,r) and [edittext for user score]
		 * 4. take in all the values, do dat exponential regression based on r =
		 * sqrt(x*x+y*y+z*z)
		 */

	}
}
