package com.jest.onyx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jest.jest.R;

public class MainDashboard extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_dashboard);
		
		((TextView) this.findViewById(R.id.hourCount)).setText(
				getString(R.string.hourcount_string,3,"week"));
		
		((TextView) this.findViewById(R.id.improvementScore)).setText(
				getString(R.string.improvement_string,3,"week"));
		
		((TextView) this.findViewById(R.id.skillsList)).setText(
				getString(R.string.skillslist_string,"Release Speed"));
		
		((TextView) this.findViewById(R.id.needsList)).setText(
				getString(R.string.needslist_string,"Elbow Angle"));
		
		((TextView) this.findViewById(R.id.goals)).setText(
				getString(R.string.goals_string,"met","week"));


	}
	
	/** Called when the user clicks Details Button */
	public void magicCharts(View view) {
		
		startActivity(new Intent(this, MainDashboard.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	

}
