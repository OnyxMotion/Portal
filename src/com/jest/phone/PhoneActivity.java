/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jest.phone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.jest.database.MotionDatabaseManager;
import com.jest.jest.R;

// Monitor the phone's orientation sensor and plot the resulting azimuth pitch and roll values.
// See: http://developer.android.com/reference/android/hardware/SensorEvent.html
public class PhoneActivity extends FragmentActivity implements OnTouchListener {

	private static final int HISTORY_SIZE = 500; // number of points to plot in
	private SensorManager sensorMgr = null;
	private Sensor orSensor = null;
	private XYPlot sensorHistoryPlot = null;
	private CheckBox hwAcceleratedCb;
	private CheckBox showFpsCb;
	private SimpleXYSeries xSeries;
	private SimpleXYSeries ySeries;
	private SimpleXYSeries zSeries;
	private static SimpleXYSeries xHistorySeries = null;
	private static SimpleXYSeries yHistorySeries = null;
	private static SimpleXYSeries zHistorySeries = null;
	private Redrawer redrawer;

	// ////////////// Additional UI & Debug //////////////
	private Button playPause;
	private Button saveDataButton;
	private Button analyzeDataButton;
	private Button viewDataButton;
	private Button resetButton;
	private Button trainMotionSet;
	private boolean play = true;
	private static PointF minXY; // used for touch screen actions
	private static PointF maxXY;
	// //////////////////////////////////////////////////////

	// /////////////// NETWORKING & RELATED UI ///////////////////
	private Button connectButton;
	private EditText hostText;
	private EditText portText;
	private String PORT_NAME = null;
	private String HOST_NAME = null;
	private Socket echoSocket = null;
	private static PrintWriter out = null;
	private int PORT = -1;
	public BufferedReader in;
	public ServerSocket serverSocket;
	private static Context context;
	public static boolean isConnected = false;
	// ////////////////////////////////////////////////////////////

	// //////////////////// Database Related ///////////////////////
	public static MotionDatabaseManager MDM = null;

	// /////////////////////////////////////////////////////////////

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_phone);

		// ////// DATABASE ////////////
		MDM = new MotionDatabaseManager(PhoneActivity.this);

		// ////////////////////////////

		playPause = (Button) findViewById(R.id.pause);
		resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				minXY.x = xHistorySeries.getX(0).floatValue();
				maxXY.x = xHistorySeries.getX(xHistorySeries.size() - 1).floatValue();
				sensorHistoryPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);

				// pre 0.5.1 users should use postRedraw() instead.
				sensorHistoryPlot.redraw();
			}
		});
		saveDataButton = (Button) findViewById(R.id.save_data_unused);
		saveDataButton.setOnClickListener(new OnClickListener() {
			// The purpose of DEBUG is to grab and display data for debugging
			// and data analysis
			@Override
			public void onClick(View v) {
				// TEST: current boundaries of what we're looking at
				Toast.makeText(getApplicationContext(), "Min: " + minXY.x + " | Max: " + maxXY.x, Toast.LENGTH_LONG).show();
				SaveDataDialog sdd = new SaveDataDialog();
				FragmentManager fm = getSupportFragmentManager();
				sdd.show(fm, "SaveDataDialog");

			}
		});
		analyzeDataButton = (Button) findViewById(R.id.analyze_data);
		analyzeDataButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		viewDataButton = (Button) findViewById(R.id.view_data);
		viewDataButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(PhoneActivity.this, ViewMotionDataActivity.class);
				startActivity(i);

			}
		});
		trainMotionSet = (Button) findViewById(R.id.train_motion_set);
		trainMotionSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ChooseSetDialog csd = new ChooseSetDialog();
				FragmentManager fm = getSupportFragmentManager();
				csd.show(fm, "ChooseSetDialog");

			}
		});

		xSeries = new SimpleXYSeries("X");
		ySeries = new SimpleXYSeries("Y");
		zSeries = new SimpleXYSeries("Z");

		// setup the APR History plot:
		sensorHistoryPlot = (XYPlot) findViewById(R.id.sensorHistoryPlot);
		sensorHistoryPlot.setOnTouchListener(this);

		xHistorySeries = new SimpleXYSeries("aX");
		xHistorySeries.useImplicitXVals();
		yHistorySeries = new SimpleXYSeries("aY");
		yHistorySeries.useImplicitXVals();
		zHistorySeries = new SimpleXYSeries("aZ");
		zHistorySeries.useImplicitXVals();

		sensorHistoryPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
		sensorHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
		sensorHistoryPlot.addSeries(xHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
		sensorHistoryPlot.addSeries(yHistorySeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
		sensorHistoryPlot.addSeries(zHistorySeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
		sensorHistoryPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
		sensorHistoryPlot.setDomainStepValue(HISTORY_SIZE / 10);
		sensorHistoryPlot.setTicksPerRangeLabel(3);
		sensorHistoryPlot.setDomainLabel("Sample Index");
		sensorHistoryPlot.getDomainLabelWidget().pack();
		sensorHistoryPlot.setRangeLabel("Acceleration (M/s2)");
		sensorHistoryPlot.getRangeLabelWidget().pack();

		sensorHistoryPlot.setRangeValueFormat(new DecimalFormat("#"));
		sensorHistoryPlot.setDomainValueFormat(new DecimalFormat("#"));

		int maxValue = 360; // TODO - make this not be empirical... Based on
							// device's accelerometer range.
		int minValue = maxValue * -1;
		sensorHistoryPlot.setRangeBoundaries(minValue, maxValue, BoundaryMode.FIXED);
		sensorHistoryPlot.calculateMinMaxVals();
		minXY = new PointF(sensorHistoryPlot.getCalculatedMinX().floatValue(), sensorHistoryPlot.getCalculatedMinY().floatValue());
		maxXY = new PointF(sensorHistoryPlot.getCalculatedMaxX().floatValue(), sensorHistoryPlot.getCalculatedMaxY().floatValue());

		redrawer = new Redrawer(Arrays.asList(new Plot[] { sensorHistoryPlot }), 100, false); // ,
																								// aprLevelsPlot
		playPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play = !play;
			}
		});

		// //////// NETWORKING // //////////////////////////////////////////////

		// hostText = (EditText) findViewById(R.id.host_text);
		portText = (EditText) findViewById(R.id.port_text);
		connectButton = (Button) findViewById(R.id.connect_button);

		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// connectButton.setClickable(false);

				// HOST_NAME = hostText.getText().toString();
				PORT_NAME = portText.getText().toString();
				if (PORT_NAME == null) {
					Toast.makeText(PhoneActivity.this, "Host and/or port not set!", Toast.LENGTH_LONG).show();
					return;
				}
				PORT = Integer.parseInt(PORT_NAME);
				new initNetworkTask().execute();
			}
		});

		connectButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				hostText.setText("192.168.43.249");
				portText.setText("4444");
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		redrawer.start();
	}

	@Override
	public void onPause() {
		redrawer.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		redrawer.finish();
		MDM.close();
		super.onDestroy();
	}

	private void cleanup() {
		finish();
	}

	public static Context getAppContext() {
		return PhoneActivity.context;
	}

	// //////////////////////// NETWORKING SUBCLASSES /////////////////////////
	class receiveDataTask extends AsyncTask<Void, Float, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String motionDataJSON;
				Log.d("receiveDataTask", "Started receiveDataTask!");

				while (in.readLine() != null) {
					// Log.d("receiveDataTask", "Received data!"); //yep
					motionDataJSON = in.readLine();

					// {"z":4.78944,"y":-0.07604187,"x":11.841576}
					JSONObject json = new JSONObject(motionDataJSON);
					float x = (float) json.getDouble("x");
					float y = (float) json.getDouble("y");
					float z = (float) json.getDouble("z");

					if (play == true) {
						publishProgress(x, y, z);
					}
				}
			} catch (Exception e) {
				Log.e("receiveDataTask", e.toString());
			}
			return null;
		}

		// Do UI stuff here with the argument of publishProgress()
		@Override
		protected void onProgressUpdate(Float... values) {
			// Update the graph:
			// update level data:
			xSeries.setModel(Arrays.asList(new Number[] { values[0] }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			ySeries.setModel(Arrays.asList(new Number[] { values[1] }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			zSeries.setModel(Arrays.asList(new Number[] { values[2] }), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

			// get rid the oldest sample in history:
			if (zHistorySeries.size() > HISTORY_SIZE) {
				zHistorySeries.removeFirst();
				yHistorySeries.removeFirst();
				xHistorySeries.removeFirst();
			}

			// add the latest history sample:
			xHistorySeries.addLast(null, values[0]);
			yHistorySeries.addLast(null, values[1]);
			zHistorySeries.addLast(null, values[2]);
			super.onProgressUpdate(values);
		}

	}
	
	public static float[] getMotionX() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x)-1;
		int len = rightLim - leftLim;
		if (xHistorySeries == null || len < 50) 
			return null;
		float[] xVals = new float[len];
		for (int i = 0; i < len; i++) {
			xVals[i] = xHistorySeries.getY(i+leftLim).floatValue();
		}
		return xVals;
	}
	public static float[] getMotionY() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x)-1;
		int len = rightLim - leftLim;
		if (yHistorySeries == null || len < 50) 
			return null;
		float[] yVals = new float[len];
		for (int i = 0; i < len; i++) {
			yVals[i] = yHistorySeries.getY(i+leftLim).floatValue();
		}
		return yVals;
	}
	public static float[] getMotionZ() {
		int leftLim = Math.round(minXY.x);
		int rightLim = Math.round(maxXY.x)-1;
		int len = rightLim - leftLim;
		if (zHistorySeries == null || len < 50) 
			return null;
		float[] zVals = new float[len];
		for (int i = 0; i < len; i++) {
			zVals[i] = zHistorySeries.getY(i+leftLim).floatValue();
		}
		return zVals;
	}
	

	// in case socket stuff can't be done in onStartCommand()
	class initNetworkTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				serverSocket = new ServerSocket(PORT);
				// echoSocket = new Socket(HOST_NAME, PORT);
				while (echoSocket == null) {
					echoSocket = serverSocket.accept();
					in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
					isConnected = true;
				}

			} catch (Exception e) {
				Log.e("initNetworkTask", e.toString());
				isConnected = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (echoSocket != null) {
				Toast.makeText(getApplicationContext(), "Socket created!", Toast.LENGTH_LONG).show();
				// COMMUNICATION ESTABLISHED - START RECEIVING DATA
				new receiveDataTask().execute();
			} else {
				Toast.makeText(getApplicationContext(), "Failed to create socket.", Toast.LENGTH_LONG).show();
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////

	// //////////////////////////// Scroll, zoom, etc /////////////////////////
	// Definition of the touch states
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

		minXY.x = Math.min(minXY.x, xHistorySeries.getX(xHistorySeries.size() - 3).floatValue());
		maxXY.x = Math.max(maxXY.x, xHistorySeries.getX(1).floatValue());
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
		float leftBoundary = xHistorySeries.getX(0).floatValue();
		float rightBoundary = xHistorySeries.getX(xHistorySeries.size() - 1).floatValue();
		// enforce left scroll boundary:
		if (minXY.x < leftBoundary) {
			minXY.x = leftBoundary;
			maxXY.x = leftBoundary + domainSpan;
		} else if (maxXY.x > xHistorySeries.getX(xHistorySeries.size() - 1).floatValue()) {
			maxXY.x = rightBoundary;
			minXY.x = rightBoundary - domainSpan;
		}
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
}