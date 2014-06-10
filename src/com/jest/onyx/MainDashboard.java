package com.jest.onyx;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.jest.getdata.DataPoint;
import com.jest.getdata.DataType;
import com.jest.getdata.User;
import com.jest.graphs.Bar;
import com.jest.graphs.BarGraph;
import com.jest.jest.R;

public class MainDashboard extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_dashboard);

		// Currently, only Basketball Freethrow. But bar charts should be sport-specific and configurable
		ArrayList<DataPoint> dpList = User.get().getData(DataType.BB_FREETHROW);
		// Currently: most recent shot. Should be average of day's shots
		float presentScore = (float) dpList.get(dpList.size() - 1).get(DataPoint.SCORE);
		// Currently: average of all data. Should only be average of current week's shots
		float weeklyScore = 0;
		for (DataPoint dp : dpList) weeklyScore += dp.get(DataPoint.SCORE);
		weeklyScore /= dpList.size();
		
		ArrayList<Bar> FTpoints = new ArrayList<Bar>();
		Bar FTpresent = new Bar();
		FTpresent.setColor(Color.parseColor("#FFB90F"));
		FTpresent.setName("Today");
		FTpresent.setValue(presentScore);
	
		Bar FTweekly = new Bar();
		FTweekly.setColor(Color.parseColor("#3399FF"));
		FTweekly.setName("This Week");
		FTweekly.setValue(weeklyScore);
		
		FTpoints.add(FTpresent);
		FTpoints.add(FTweekly);

		BarGraph FT = (BarGraph) findViewById(R.id.FTgraph);
		FT.setBars(FTpoints);

		IconicFontDrawable iconicFontDrawable = new IconicFontDrawable(
			getBaseContext());
		iconicFontDrawable.setIcon(FontAwesomeIcon.ARROW_UP);
		iconicFontDrawable.setIconColor(this.getResources().getColor(
			R.color.onyxyellow));

		findViewById(R.id.iconUp).setBackground(iconicFontDrawable);

		((TextView) findViewById(R.id.improvementScore))
			.setText(getString(R.string.improvement_string, 3, "week"));

		ArrayList<Bar> Ppoints = new ArrayList<Bar>();
		Bar Ppresent = new Bar();
		Ppresent.setColor(Color.parseColor("#FFB90F"));
		Ppresent.setName("Today");
		Ppresent.setValue(2);								// Dummy data
		Bar Pweekly = new Bar();
		Pweekly.setColor(Color.parseColor("#3399FF"));
		Pweekly.setName("This Week (Avg)");
		Pweekly.setValue(2.6);								// Dummy data
		Ppoints.add(Ppresent);
		Ppoints.add(Pweekly);

		BarGraph P = (BarGraph) findViewById(R.id.Pgraph);
		P.setBars(Ppoints);

		IconicFontDrawable iconicFontDrawable2 = new IconicFontDrawable(
			getBaseContext());
		iconicFontDrawable2.setIcon(FontAwesomeIcon.ARROW_DOWN);
		iconicFontDrawable2.setIconColor(getResources().getColor(
			R.color.onyxyellow));

		findViewById(R.id.iconDown).setBackground(iconicFontDrawable2);

		((TextView) findViewById(R.id.hourCount)).setText(getString(
			R.string.hourcount_string, 2, "today"));

		((TextView) findViewById(R.id.progressRap)).setText(getString(
			R.string.progress_string, 6));

		((TextView) findViewById(R.id.skillsList)).setText(getString(
			R.string.skillslist_string, "Release Speed"));

		((TextView) findViewById(R.id.needsList)).setText(getString(
			R.string.needslist_string, "Elbow Angle"));

		((TextView) findViewById(R.id.goals)).setText(getString(
			R.string.goals_string, "met", "week"));

	}

	/** Called when the user clicks Details Button */
	public void magicCharts(View view) {
		startActivity(new Intent(this, SkillSetList.class));
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
