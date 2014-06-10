package com.jest.onyx;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fima.chartview.ChartView;
import com.fima.chartview.LinearSeries;
import com.fima.chartview.LinearSeries.LinearPoint;
import com.jest.getdata.DataPoint;
import com.jest.getdata.DataType;
import com.jest.getdata.User;
import com.jest.jest.R;
import com.jest.onyx.SkillChartAdapter.LabelOrientation;

public class SkillListDetail extends Activity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skill_set_list);

		
		int measurement = getIntent().getExtras().getInt("MEASUREMENT");
		String title = "";
		switch (measurement) {
			case DataPoint.SCORE: title = "Score"; break;
			case DataPoint.ELBOW_ANGLE: title = "Elbow Angle"; break;
			case DataPoint.RELEASE_ANGLE: title = "Release Angle"; break;
			case DataPoint.RELEASE_SPEED: title = "Release Speed"; break;
			default: break;
		}
		setTitle(title);
		
		// Find the chart view
		ChartView chartView = (ChartView) findViewById(R.id.chart_view);

		// Create the data points
		LinearSeries series = new LinearSeries();
		series.setLineColor(0xFF0099CC);
		series.setLineWidth(2);

		ArrayList<DataPoint> dpList = User.get().getData(DataType.BB_FREETHROW);
		for (int i = 0; i < dpList.size(); i++)
			series.addPoint(new LinearPoint(
				(double) (i + 1),
				(double) dpList.get(i).get(measurement)));

		// Add chart view data
		chartView.addSeries(series);
		chartView.setLeftLabelAdapter(new SkillChartAdapter(
			this, LabelOrientation.VERTICAL, SkillChartAdapter.FLOAT));
		chartView.setBottomLabelAdapter(new SkillChartAdapter(
			this, LabelOrientation.HORIZONTAL, SkillChartAdapter.INT));

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