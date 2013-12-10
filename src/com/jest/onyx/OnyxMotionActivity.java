package com.jest.onyx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.jest.analysis.MotionAnalyzer;
import com.jest.database.DBhelper;
import com.jest.database.MotionDatabaseManager;
import com.jest.database.MotionDatabaseManager.DataArrays;
import com.jest.database.MotionDatabaseManager.MotionLabel;
import com.jest.jest.R;
import com.jest.razor.RazorExample;

public class OnyxMotionActivity extends Activity implements OnTouchListener, SensorEventListener {

	private static final int HISTORY_SIZE = 400; // number of points to plot in
	private static XYPlot sensorHistoryPlot = null;
	private static SimpleXYSeries xSeries;
	private static SimpleXYSeries ySeries;
	private static SimpleXYSeries zSeries;
	private static SimpleXYSeries xHSeries = null;
	private static SimpleXYSeries yHSeries = null;
	private static SimpleXYSeries zHSeries = null;
	private static Redrawer redrawer = null;

	// For the comparison graph, when used:
	private static final int HISTORY_SIZE2 = 700; // number of points to plot in
	private static XYPlot sensorHistoryPlot2 = null;
	private static SimpleXYSeries xSeries2;
	private static SimpleXYSeries ySeries2;
	private static SimpleXYSeries zSeries2;
	private static SimpleXYSeries xHSeries2 = null;
	private static SimpleXYSeries yHSeries2 = null;
	private static SimpleXYSeries zHSeries2 = null;
	private static Redrawer redrawer2 = null;

	// //////////////////// Database Related ///////////////////////
	public static MotionDatabaseManager MDM = null;
	private String[] labelDescriptions;
	private long[] labelRows;
	private String[] labelSets;
	private int[] labelScores;
	private long[] motionLabelRows = null;

	private static TextView title;
	private TextView referenceText;
	private static TextView scoreText;
	public static final int MENU_CONNECT = Menu.FIRST;
	public static final int MENU_DEBUG = Menu.FIRST + 1;
	private Spinner chooseReferenceSpinner;
	private Button playPauseButton;
	private Button saveDataButton;
	private ArrayList<String> spinnerMotionLabelsArray = null;

	private static boolean playPauseVal = true;
	private static PointF minXY; // used for touch screen actions
	private static PointF maxXY;

	// testing - internal sensors
	private SensorManager sensorManager = null;
	private boolean sensorReady = false;

	// /////////////// ANALYSIS VARIABLES ///////////////
	private static MotionAnalyzer MA = null;
	private static float[] xTemplate = null;
	private static float[] yTemplate = null;
	private static float[] zTemplate = null;
	private static LinkedList<Float> xStreaming = null;
	private static LinkedList<Float> yStreaming = null;
	private static LinkedList<Float> zStreaming = null;
	private static int templateLength = 0; // only need to analyze the length of
											// the
	// template in real-time
	private static int streamLength = 0;
	private static int analysesCompleted = 0;
	private static int analysisEveryX = 10; // motion analysis will occur every
	private final static Object lock = new Object();

	// sampleRate/analysisEveryX time
	// interval

	// ///////////////// Networking //////////////////////
	private BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onyx_motion_activity);

		// ////// DATABASE ////////////
		MDM = new MotionDatabaseManager(OnyxMotionActivity.this);
		loadReferenceData();

		// ///// DATA STRUCTURES & ALGORITHMS //////////
		MA = new MotionAnalyzer(HISTORY_SIZE);
		xStreaming = new LinkedList<Float>();
		yStreaming = new LinkedList<Float>();
		zStreaming = new LinkedList<Float>();

		// GRAPH RELATED
		sensorHistoryPlot = (XYPlot) findViewById(R.id.sensorHistoryPlotFragment);
		// sensorHistoryPlot2 = (XYPlot)
		// findViewById(R.id.sensorHistoryPlotFragment2);
		initGraph();
		// initGraph2();

		// OTHER UI
		title = (TextView) findViewById(R.id.fragment_title);
		referenceText = (TextView) findViewById(R.id.reference_title);
		scoreText = (TextView) findViewById(R.id.score_text);
		chooseReferenceSpinner = (Spinner) findViewById(R.id.choose_reference);
		playPauseButton = (Button) findViewById(R.id.play_pause);
		saveDataButton = (Button) findViewById(R.id.save_data);
		setInputListeners();

		// FOR TESTING - internal sensors
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	private void loadReferenceData() {
		ArrayList<MotionLabel> labelDescs = MDM.getMotionLabels();
		labelDescriptions = DBhelper.toLabelDescriptions(labelDescs);
		labelRows = DBhelper.toLabelRows(labelDescs);
		labelSets = DBhelper.toLabelSets(labelDescs);
		labelScores = DBhelper.toLabelScores(labelDescs);
	}

	private void setInputListeners() {
		playPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playPauseVal = !playPauseVal;
			}
		});
		playPauseButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				resetGraphZoom();
				return true;
			}
		});
		saveDataButton.setOnClickListener(new OnClickListener() {
			// The purpose of DEBUG is to grab and display data for debugging
			// and data analysis
			@Override
			public void onClick(View v) {
				// TEST: current boundaries of what we're looking at
				Toast.makeText(getApplicationContext(), "Min: " + minXY.x + " | Max: " + maxXY.x, Toast.LENGTH_LONG).show();
				Intent i = new Intent(OnyxMotionActivity.this, SaveDataActivity.class);
				startActivity(i);

			}
		});
		setupReferenceSpinner();

	}

	private void setupReferenceSpinner() {
		// //////////// Spinner - set up reference motion list ////////////
		// if (spinnerMotionLabelsArray == null || motionLabelRows == null) {
		spinnerMotionLabelsArray = new ArrayList<String>();
		String[] descs = getLabelDescriptions();
		int len = descs.length;
		for (int i = 0; i < len; i++) {
			spinnerMotionLabelsArray.add(i, descs[i]);
		}
		motionLabelRows = getLabelRows();
		// }
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(OnyxMotionActivity.this, android.R.layout.simple_spinner_dropdown_item,
				spinnerMotionLabelsArray);
		chooseReferenceSpinner.setAdapter(spinnerArrayAdapter);
		chooseReferenceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				playPauseVal = !playPauseVal;
				String selection = spinnerMotionLabelsArray.get(position);
				long row = motionLabelRows[position];
				// Toast.makeText(getActivity(), "Spinner item selected: " +
				// selection + "/" + row, Toast.LENGTH_LONG).show();

				// CHANGE DATA ON THE COMPARISON (below) GRAPH:
				DataArrays data = OnyxMotionActivity.MDM.getMotionData(row);
				int len = data.dataX.size(); // all sizes the same - a
												// precondition for saving data
				// NOTE - this is the current reference template which we will
				// want to be doing
				// real-time analysis against
				xTemplate = MotionAnalyzer.toFloatArray(data.dataX, len);
				yTemplate = MotionAnalyzer.toFloatArray(data.dataY, len);
				zTemplate = MotionAnalyzer.toFloatArray(data.dataZ, len);
				templateLength = xTemplate.length;

				referenceText.setText("Reference motion: " + selection);
				// showNewData2(len, x, y, z);
				playPauseVal = !playPauseVal;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}

	// ////////// DB SUPPORT ////////////////
	private String[] getLabelDescriptions() {
		return labelDescriptions;
	}

	// TODO - these static variables being passed around should at least be
	// apart of MainActivity.MDM!!!
	private long[] getLabelRows() {
		return labelRows;
	}

	public static void updateTitle(String newTitle) {
		title.setText(newTitle);
	}

	// /////////////////// XYPLOT GRAPHS //////////////////////

	private void resetGraphZoom() {
		minXY.x = xHSeries.getX(0).floatValue();
		maxXY.x = xHSeries.getX(xHSeries.size() - 1).floatValue();
		sensorHistoryPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
		// pre 0.5.1 users should use postRedraw() instead.
		sensorHistoryPlot.redraw();
	}

	// For real-time graph streaming
	// TODO - make sure whoever calls this function ensures that angles are b/w
	// -180 and 180
	public static void addDataPoint(float x, float y, float z) {

		if (playPauseVal) {
			// Update the graph:
			// update level data:
			xSeries.setModel(Arrays.asList(new Number[] { x }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			ySeries.setModel(Arrays.asList(new Number[] { y }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			zSeries.setModel(Arrays.asList(new Number[] { z }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

			// get rid the oldest sample in history:
			if (zHSeries.size() > HISTORY_SIZE) {
				zHSeries.removeFirst();
				yHSeries.removeFirst();
				xHSeries.removeFirst();
				// remove first from the linked-list data:
				xStreaming.removeFirst();
				yStreaming.removeFirst();
				zStreaming.removeFirst();
			}

			// add the latest history sample:
			xHSeries.addLast(null, x);
			yHSeries.addLast(null, y);
			zHSeries.addLast(null, z);
			// update variables to be used in analysis:
			xStreaming.add(x);
			yStreaming.add(y);
			zStreaming.add(z);
			streamLength++;

			new AnalyzeMotionTask().execute();
		}

	}

	private void initGraph() {
		xSeries = new SimpleXYSeries("X");
		ySeries = new SimpleXYSeries("Y");
		zSeries = new SimpleXYSeries("Z");

		xHSeries = new SimpleXYSeries("oX");
		xHSeries.useImplicitXVals();
		yHSeries = new SimpleXYSeries("oY");
		yHSeries.useImplicitXVals();
		zHSeries = new SimpleXYSeries("oZ");
		zHSeries.useImplicitXVals();

		sensorHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
		sensorHistoryPlot.setOnTouchListener(this);
		sensorHistoryPlot.addSeries(xHSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
		sensorHistoryPlot.addSeries(yHSeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
		sensorHistoryPlot.addSeries(zHSeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
		sensorHistoryPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
		sensorHistoryPlot.setDomainStepValue(HISTORY_SIZE / 10);
		sensorHistoryPlot.setTicksPerRangeLabel(3);
		sensorHistoryPlot.setDomainLabel("Index");
		sensorHistoryPlot.getDomainLabelWidget().pack();
		sensorHistoryPlot.setRangeLabel("Orientation");
		sensorHistoryPlot.getRangeLabelWidget().pack();

		sensorHistoryPlot.setRangeValueFormat(new DecimalFormat("#"));
		sensorHistoryPlot.setDomainValueFormat(new DecimalFormat("#"));

		int maxValue = 360;
		int minValue = maxValue * -1;
		sensorHistoryPlot.setRangeBoundaries(minValue, maxValue, BoundaryMode.FIXED);
		sensorHistoryPlot.calculateMinMaxVals();
		minXY = new PointF(sensorHistoryPlot.getCalculatedMinX().floatValue(), sensorHistoryPlot.getCalculatedMinY().floatValue());
		maxXY = new PointF(sensorHistoryPlot.getCalculatedMaxX().floatValue(), sensorHistoryPlot.getCalculatedMaxY().floatValue());

		redrawer = new Redrawer(Arrays.asList(new Plot[] { sensorHistoryPlot }), 100, false);

	}

	// //////// TESTING WITH INTERNAL SENSOR /////////////////
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (playPauseVal) {
			// if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// float[] values = event.values;
			// float x = values[0];
			// float y = values[1];
			// float z = values[2];
			// }
			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				float[] values = event.values;
				float x = values[0];
				float y = values[1];
				float z = values[2];

				if (sensorReady) {
					addDataPoint(x, y, z);
				}
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	// /////////////// ANDROID ACTIVITY OVERRIDES ////////////

	@Override
	public void onResume() {
		super.onResume();
		if (redrawer != null)
			redrawer.start();
		if (redrawer2 != null)
			redrawer2.start();
		// Toast.makeText(OnyxMotionActivity.this, "onResume",
		// Toast.LENGTH_LONG).show();
		loadReferenceData();
		setupReferenceSpinner();
		if (xTemplate == null) {
			referenceText.setText("Choose a reference motion below.");
		}
	}

	@Override
	public void onPause() {
		if (redrawer != null)
			redrawer.pause();
		if (redrawer2 != null)
			redrawer2.start();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (redrawer != null)
			redrawer.finish();
		if (redrawer2 != null)
			redrawer2.finish();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_CONNECT, Menu.NONE, "Connect Onyx");
		menu.add(Menu.NONE, MENU_DEBUG, Menu.NONE, "Debug with phone");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_CONNECT:
				// Toast.makeText(OnyxMotionActivity.this,
				// "Chose device connect", Toast.LENGTH_LONG).show();
				setupRazorSensor();
				return true;
			case MENU_DEBUG:
				setupSensor();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setupSensor() {
		// TESTING - set up the phone sensors
		// sensorManager.registerListener(this,
		// sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
		sensorReady = true;
		Toast.makeText(OnyxMotionActivity.this, "Now testing with internal sensors", Toast.LENGTH_LONG).show();
	}

	private void setupRazorSensor() {
		Intent i = new Intent(OnyxMotionActivity.this, RazorExample.class);
		startActivity(i);
	}

	// ////////////////// GET MOTION DATA INTO VARIABLES ////////////////////
	public static float[] getMotionX() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x) - 1;
		int len = rightLim - leftLim;
		if (xHSeries == null || len < 50)
			return null;
		float[] xVals = new float[len];
		for (int i = 0; i < len; i++) {
			xVals[i] = xHSeries.getY(i + leftLim).floatValue();
		}
		return xVals;
	}

	public static float[] getMotionY() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x) - 1;
		int len = rightLim - leftLim;
		if (yHSeries == null || len < 50)
			return null;
		float[] yVals = new float[len];
		for (int i = 0; i < len; i++) {
			yVals[i] = yHSeries.getY(i + leftLim).floatValue();
		}
		return yVals;
	}

	public static float[] getMotionZ() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x) - 1;
		int len = rightLim - leftLim;
		if (zHSeries == null || len < 50)
			return null;
		float[] zVals = new float[len];
		for (int i = 0; i < len; i++) {
			zVals[i] = zHSeries.getY(i + leftLim).floatValue();
		}
		return zVals;
	}

	// ///////////////////// TOUCHING THE GRAPH /////////////////
	static final int NONE = 0;
	static final int ONE_FINGER_DRAG = 1;
	static final int TWO_FINGERS_DRAG = 2;
	int mode = NONE;

	PointF firstFinger;
	float distBetweenFingers;
	boolean stopThread = false;

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: // Start gesture
				firstFinger = new PointF(event.getX(), event.getY());
				mode = ONE_FINGER_DRAG;
				stopThread = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_DOWN: // second finger
				distBetweenFingers = spacing(event);
				// the distance check is done to avoid false alarms
				if (distBetweenFingers > 5f) {
					mode = TWO_FINGERS_DRAG;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == ONE_FINGER_DRAG) {
					PointF oldFirstFinger = firstFinger;
					firstFinger = new PointF(event.getX(), event.getY());
					scroll(oldFirstFinger.x - firstFinger.x);
					sensorHistoryPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
					sensorHistoryPlot.redraw();

				} else if (mode == TWO_FINGERS_DRAG) {
					float oldDist = distBetweenFingers;
					distBetweenFingers = spacing(event);
					zoom(oldDist / distBetweenFingers);
					sensorHistoryPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
					sensorHistoryPlot.redraw();
				}
				break;
		}
		return true;
	}

	private void zoom(float scale) {
		float domainSpan = maxXY.x - minXY.x;
		float domainMidPoint = maxXY.x - domainSpan / 2.0f;
		float offset = domainSpan * scale / 2.0f;

		minXY.x = domainMidPoint - offset;
		maxXY.x = domainMidPoint + offset;

		minXY.x = Math.min(minXY.x, xHSeries.getX(xHSeries.size() - 3).floatValue());
		maxXY.x = Math.max(maxXY.x, xHSeries.getX(1).floatValue());
		clampToDomainBounds(domainSpan);
	}

	private void scroll(float pan) {
		float domainSpan = maxXY.x - minXY.x;
		float step = domainSpan / sensorHistoryPlot.getWidth();
		float offset = pan * step;
		minXY.x = minXY.x + offset;
		maxXY.x = maxXY.x + offset;
		clampToDomainBounds(domainSpan);
	}

	private void clampToDomainBounds(float domainSpan) {
		float leftBoundary = xHSeries.getX(0).floatValue();
		float rightBoundary = xHSeries.getX(xHSeries.size() - 1).floatValue();
		// enforce left scroll boundary:
		if (minXY.x < leftBoundary) {
			minXY.x = leftBoundary;
			maxXY.x = leftBoundary + domainSpan;
		} else if (maxXY.x > xHSeries.getX(xHSeries.size() - 1).floatValue()) {
			maxXY.x = rightBoundary;
			minXY.x = rightBoundary - domainSpan;
		}
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	static class AnalyzeMotionTask extends AsyncTask<Float, Float, float[]> {

		@Override
		protected float[] doInBackground(Float... params) {
			// REAL-TIME DATA ANALYSIS
			float[] res = null;
			synchronized (lock) {
				if ((streamLength > templateLength) && (streamLength % analysisEveryX) == 0) {
					res = MA.testAlgorithm1(xStreaming, yStreaming, zStreaming, xTemplate, yTemplate, zTemplate, templateLength, analysisEveryX);
					analysesCompleted++;
				}
			}
			return res;
		}

		@Override
		protected void onPostExecute(float[] res) {
			// TODO Auto-generated method stub
			super.onPostExecute(res);
			if (res != null)
				// scoreText.setText("Scores:::" + " x: " + res[0] + " y: " +
				// res[1] + " z: " + res[2] + " // Overall: " + res[3]);
				scoreText.setText("Scores:::" + "steamLength:" + streamLength + " / analysesCompleted:" + analysesCompleted + " / asdf: " + res[3]);
		}

	}

	// public static void showNewData(int len, ArrayList<Float> x,
	// ArrayList<Float> y, ArrayList<Float> z) {
	//
	// xSeries.setModel(Arrays.asList(new Number[] { x.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	// ySeries.setModel(Arrays.asList(new Number[] { y.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	// zSeries.setModel(Arrays.asList(new Number[] { z.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	//
	// clearGraphDataHistory();
	// for (int i = 0; i < len; i++) {
	// xHSeries.addLast(null, x.get(i));
	// yHSeries.addLast(null, y.get(i));
	// zHSeries.addLast(null, z.get(i));
	// }
	//
	// // the redrawing part
	// sensorHistoryPlot.setDomainBoundaries(0, len, BoundaryMode.FIXED);
	// redrawer.start();
	// }

	// private void showNewData2(int len, ArrayList<Float> x, ArrayList<Float>
	// y, ArrayList<Float> z) {
	//
	// xSeries2.setModel(Arrays.asList(new Number[] { x.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	// ySeries2.setModel(Arrays.asList(new Number[] { y.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	// zSeries2.setModel(Arrays.asList(new Number[] { z.get(0) }),
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	//
	// clearGraphDataHistory2();
	// for (int i = 0; i < len; i++) {
	// xHSeries2.addLast(null, x.get(i));
	// yHSeries2.addLast(null, y.get(i));
	// zHSeries2.addLast(null, z.get(i));
	// }
	//
	// // the redrawing part
	// sensorHistoryPlot2.setDomainBoundaries(0, len, BoundaryMode.FIXED);
	// redrawer2.start();
	// }

	// private static void clearGraphDataHistory() {
	// if (xHSeries != null && yHSeries != null && zHSeries != null) {
	// int len = xHSeries.size(); // precondition: all x/y/z history
	// // lengths always kept the same
	// for (int i = 0; i < len; i++) {
	// xHSeries.removeFirst();
	// yHSeries.removeFirst();
	// zHSeries.removeFirst();
	// }
	// }
	// }

	// private static void clearGraphDataHistory2() {
	// if (xHSeries2 != null && yHSeries2 != null && zHSeries2 != null) {
	// int len = xHSeries2.size(); // precondition: all x/y/z history
	// // lengths always kept the same
	// for (int i = 0; i < len; i++) {
	// xHSeries2.removeFirst();
	// yHSeries2.removeFirst();
	// zHSeries2.removeFirst();
	// }
	// }
	// }

	// private void initGraph2() {
	// xSeries2 = new SimpleXYSeries("X");
	// ySeries2 = new SimpleXYSeries("Y");
	// zSeries2 = new SimpleXYSeries("Z");
	//
	// xHSeries2 = new SimpleXYSeries("oX");
	// xHSeries2.useImplicitXVals();
	// yHSeries2 = new SimpleXYSeries("oY");
	// yHSeries2.useImplicitXVals();
	// zHSeries2 = new SimpleXYSeries("oZ");
	// zHSeries2.useImplicitXVals();
	//
	// sensorHistoryPlot2.setDomainBoundaries(0, HISTORY_SIZE,
	// BoundaryMode.FIXED);
	// sensorHistoryPlot2.addSeries(xHSeries2, new
	// LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
	// sensorHistoryPlot2.addSeries(yHSeries2, new
	// LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
	// sensorHistoryPlot2.addSeries(zHSeries2, new
	// LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
	// sensorHistoryPlot2.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
	// sensorHistoryPlot2.setDomainStepValue(HISTORY_SIZE / 10);
	// sensorHistoryPlot2.setTicksPerRangeLabel(3);
	// sensorHistoryPlot2.setDomainLabel("Index");
	// sensorHistoryPlot2.getDomainLabelWidget().pack();
	// sensorHistoryPlot2.setRangeLabel("Orientation");
	// sensorHistoryPlot2.getRangeLabelWidget().pack();
	//
	// sensorHistoryPlot2.setRangeValueFormat(new DecimalFormat("#"));
	// sensorHistoryPlot2.setDomainValueFormat(new DecimalFormat("#"));
	//
	// int maxValue = 360; // TODO - make this not be emperical... Based on
	// // device's accelerometer range.
	// int minValue = maxValue * -1;
	// sensorHistoryPlot2.setRangeBoundaries(minValue, maxValue,
	// BoundaryMode.FIXED);
	// sensorHistoryPlot2.calculateMinMaxVals();
	//
	// redrawer2 = new Redrawer(Arrays.asList(new Plot[] { sensorHistoryPlot2
	// }), 100, false);
	//
	// }
}
