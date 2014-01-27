//package com.jest.phone;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.androidplot.Plot;
//import com.androidplot.util.Redrawer;
//import com.androidplot.xy.BoundaryMode;
//import com.androidplot.xy.LineAndPointFormatter;
//import com.androidplot.xy.SimpleXYSeries;
//import com.androidplot.xy.XYPlot;
//import com.androidplot.xy.XYStepMode;
//import com.jest.analysis.MotionAnalyzer;
//import com.jest.database.MotionDatabaseManager.DataArrays;
//import com.jest.jest.R;
//
//public class ViewMotionDataFragment extends Fragment {
//
//	private static final int HISTORY_SIZE = 500; // number of points to plot in
//	private static XYPlot sensorHistoryPlot = null;
//	private static SimpleXYSeries xSeries;
//	private static SimpleXYSeries ySeries;
//	private static SimpleXYSeries zSeries;
//	private static SimpleXYSeries xHSeries = null;
//	private static SimpleXYSeries yHSeries = null;
//	private static SimpleXYSeries zHSeries = null;
//	private static Redrawer redrawer = null;
//
//	// For the comparison graph, when used:
//	private static final int HISTORY_SIZE2 = 500; // number of points to plot in
//	private static XYPlot sensorHistoryPlot2 = null;
//	private static SimpleXYSeries xSeries2;
//	private static SimpleXYSeries ySeries2;
//	private static SimpleXYSeries zSeries2;
//	private static SimpleXYSeries xHSeries2 = null;
//	private static SimpleXYSeries yHSeries2 = null;
//	private static SimpleXYSeries zHSeries2 = null;
//	private static Redrawer redrawer2 = null;
//
//	private MotionAnalyzer MA = null;
//	
//	private static TextView title;
//	private Spinner compareTo;
//	private Button similarity;
//	private Button forces;
//	private ArrayList<String> spinnerMotionLabelsArray = null;
//	private long[] motionLabelRows = null;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		MA = new MotionAnalyzer(HISTORY_SIZE);
//		
//		View v = inflater.inflate(R.layout.view_data_fragment, container, false);
//
//		// GRAPH RELATED
//		sensorHistoryPlot = (XYPlot) v.findViewById(R.id.sensorHistoryPlotFragment);
//		sensorHistoryPlot2 = (XYPlot) v.findViewById(R.id.sensorHistoryPlotFragment2);
//		initGraph();
//		initComparisonGraph();
//
//		title = (TextView) v.findViewById(R.id.fragment_title);
//		compareTo = (Spinner) v.findViewById(R.id.choose_reference);
//		similarity = (Button) v.findViewById(R.id.play_pause);
//		forces = (Button) v.findViewById(R.id.save_data);
//		setInputListeners();
//
//		return v;
//	}
//
//	private void setInputListeners() {
//
//		if (spinnerMotionLabelsArray == null || motionLabelRows == null) {
//			spinnerMotionLabelsArray = new ArrayList<String>();
//			String[] descs = ViewMotionDataListFragment.getLabelDescriptions();
//			int len = descs.length;
//			for (int i = 0; i < len; i++) {
//				spinnerMotionLabelsArray.add(i, descs[i]);
//			}
//			motionLabelRows = ViewMotionDataListFragment.getLabelRows();
//		}
//		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
//				spinnerMotionLabelsArray);
//		compareTo.setAdapter(spinnerArrayAdapter);
//		compareTo.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//				String selection = spinnerMotionLabelsArray.get(position);
//				long row = motionLabelRows[position];
//				//Toast.makeText(getActivity(), "Spinner item selected: " + selection + "/" + row, Toast.LENGTH_LONG).show();
//
//				// CHANGE DATA ON THE COMPARISON (below) GRAPH:
//				DataArrays data = PhoneActivity.MDM.getMotionData(row);
//				int len = data.dataX.size();
//				ArrayList<Float> x = data.dataX;
//				ArrayList<Float> y = data.dataY;
//				ArrayList<Float> z = data.dataZ;
//				showNewComparisonData(len, x, y, z);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parentView) {
//				// your code here
//			}
//
//		});
//
//		similarity.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//THIS IS WHERE ANALYSIS IS DONE
//				
////				float[] res = MA.testAlgorithm1(xHSeries, yHSeries, zHSeries, xHSeries2, yHSeries2, zHSeries2);
////				title.setText("Scores:::" + " x: "+res[0] + " y: "+res[1] + " z: "+res[2] + " // Overall: "+res[3]);
////				
////				
////				// TESTING WHAT TRANSFORMS LOOK LIKE ON DATA //
////				MA.getTestResults(xHSeries, yHSeries, zHSeries, xHSeries2, yHSeries2, zHSeries2);
////				ArrayList<Float> testX1 = MA.float2ArrayList(MA.getX1());
////				ArrayList<Float> testY1 = MA.float2ArrayList(MA.getY1());
////				ArrayList<Float> testZ1 = MA.float2ArrayList(MA.getZ1());
////				ArrayList<Float> testX2 = MA.float2ArrayList(MA.getX2());
////				ArrayList<Float> testY2 = MA.float2ArrayList(MA.getY2());
////				ArrayList<Float> testZ2 = MA.float2ArrayList(MA.getZ2());
////				showNewData(testX1.size(), testX1, testY1, testZ1);
////				showNewComparisonData(testX2.size(), testX2, testY2, testZ2);
//				
//			}
//		});
//		forces.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//	}
//
//	public static void updateTitle(String newTitle) {
//		title.setText(newTitle);
//	}
//
//	public static void showNewData(int len, ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
//
//		xSeries.setModel(Arrays.asList(new Number[] { x.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//		ySeries.setModel(Arrays.asList(new Number[] { y.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//		zSeries.setModel(Arrays.asList(new Number[] { z.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//
//		clearGraphDataHistory();
//		for (int i = 0; i < len; i++) {
//			xHSeries.addLast(null, x.get(i));
//			yHSeries.addLast(null, y.get(i));
//			zHSeries.addLast(null, z.get(i));
//		}
//
//		// the redrawing part
//		sensorHistoryPlot.setDomainBoundaries(0, len, BoundaryMode.FIXED);
//		redrawer.start();
//	}
//
//	private void showNewComparisonData(int len, ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
//
//		xSeries2.setModel(Arrays.asList(new Number[] { x.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//		ySeries2.setModel(Arrays.asList(new Number[] { y.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//		zSeries2.setModel(Arrays.asList(new Number[] { z.get(0) }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
//
//		clearCompareGraphDataHistory();
//		for (int i = 0; i < len; i++) {
//			xHSeries2.addLast(null, x.get(i));
//			yHSeries2.addLast(null, y.get(i));
//			zHSeries2.addLast(null, z.get(i));
//		}
//
//		// the redrawing part
//		sensorHistoryPlot2.setDomainBoundaries(0, len, BoundaryMode.FIXED);
//		redrawer2.start();
//	}
//
//	private static void clearGraphDataHistory() {
//		if (xHSeries != null && yHSeries != null && zHSeries != null) {
//			int len = xHSeries.size(); // precondition: all x/y/z history
//												// lengths always kept the same
//			for (int i = 0; i < len; i++) {
//				xHSeries.removeFirst();
//				yHSeries.removeFirst();
//				zHSeries.removeFirst();
//			}
//		}
//	}
//
//	private static void clearCompareGraphDataHistory() {
//		if (xHSeries2 != null && yHSeries2 != null && zHSeries2 != null) {
//			int len = xHSeries2.size(); // precondition: all x/y/z history
//												// lengths always kept the same
//			for (int i = 0; i < len; i++) {
//				xHSeries2.removeFirst();
//				yHSeries2.removeFirst();
//				zHSeries2.removeFirst();
//			}
//		}
//	}
//
//	private void initGraph() {
//		xSeries = new SimpleXYSeries("X");
//		ySeries = new SimpleXYSeries("Y");
//		zSeries = new SimpleXYSeries("Z");
//
//		xHSeries = new SimpleXYSeries("aX");
//		xHSeries.useImplicitXVals();
//		yHSeries = new SimpleXYSeries("aY");
//		yHSeries.useImplicitXVals();
//		zHSeries = new SimpleXYSeries("aZ");
//		zHSeries.useImplicitXVals();
//
//		sensorHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
//		sensorHistoryPlot.addSeries(xHSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
//		sensorHistoryPlot.addSeries(yHSeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
//		sensorHistoryPlot.addSeries(zHSeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
//		sensorHistoryPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
//		sensorHistoryPlot.setDomainStepValue(HISTORY_SIZE / 10);
//		sensorHistoryPlot.setTicksPerRangeLabel(3);
//		sensorHistoryPlot.setDomainLabel("Sample Index");
//		sensorHistoryPlot.getDomainLabelWidget().pack();
//		sensorHistoryPlot.setRangeLabel("Acceleration (M/s2)");
//		sensorHistoryPlot.getRangeLabelWidget().pack();
//
//		sensorHistoryPlot.setRangeValueFormat(new DecimalFormat("#"));
//		sensorHistoryPlot.setDomainValueFormat(new DecimalFormat("#"));
//
//		int maxValue = 360; 
//		int minValue = maxValue * -1;
//		sensorHistoryPlot.setRangeBoundaries(minValue, maxValue, BoundaryMode.FIXED);
//		sensorHistoryPlot.calculateMinMaxVals();
//
//		redrawer = new Redrawer(Arrays.asList(new Plot[] { sensorHistoryPlot }), 100, false);
//
//	}
//
//	private void initComparisonGraph() {
//		xSeries2 = new SimpleXYSeries("X");
//		ySeries2 = new SimpleXYSeries("Y");
//		zSeries2 = new SimpleXYSeries("Z");
//
//		xHSeries2 = new SimpleXYSeries("aX");
//		xHSeries2.useImplicitXVals();
//		yHSeries2 = new SimpleXYSeries("aY");
//		yHSeries2.useImplicitXVals();
//		zHSeries2 = new SimpleXYSeries("aZ");
//		zHSeries2.useImplicitXVals();
//
//		sensorHistoryPlot2.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
//		sensorHistoryPlot2.addSeries(xHSeries2, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
//		sensorHistoryPlot2.addSeries(yHSeries2, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
//		sensorHistoryPlot2.addSeries(zHSeries2, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
//		sensorHistoryPlot2.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
//		sensorHistoryPlot2.setDomainStepValue(HISTORY_SIZE / 10);
//		sensorHistoryPlot2.setTicksPerRangeLabel(3);
//		sensorHistoryPlot2.setDomainLabel("Sample Index");
//		sensorHistoryPlot2.getDomainLabelWidget().pack();
//		sensorHistoryPlot2.setRangeLabel("Acceleration (M/s2)");
//		sensorHistoryPlot2.getRangeLabelWidget().pack();
//
//		sensorHistoryPlot2.setRangeValueFormat(new DecimalFormat("#"));
//		sensorHistoryPlot2.setDomainValueFormat(new DecimalFormat("#"));
//
//		int maxValue = 360; // TODO - make this not be emperical... Based on
//							// device's accelerometer range.
//		int minValue = maxValue * -1;
//		sensorHistoryPlot2.setRangeBoundaries(minValue, maxValue, BoundaryMode.FIXED);
//		sensorHistoryPlot2.calculateMinMaxVals();
//
//		redrawer2 = new Redrawer(Arrays.asList(new Plot[] { sensorHistoryPlot2 }), 100, false);
//
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		redrawer.start();
//		if (redrawer2 != null)
//			redrawer2.start();
//	}
//
//	@Override
//	public void onPause() {
//		redrawer.pause();
//		if (redrawer2 != null)
//			redrawer2.start();
//		super.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		redrawer.finish();
//		if (redrawer2 != null)
//			redrawer2.finish();
//		super.onDestroy();
//	}
//	
//	
//	
//	
//
//}
