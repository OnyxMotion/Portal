package com.jest.onyx;

import java.io.IOException;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.jest.razor.RazorAHRS;
import com.jest.razor.RazorListener;

public class DeviceService extends Service {

	private BluetoothDevice razorDevice;
	private Context context;
	private RazorAHRS razor;
	public static final String INTENT_RAZOR_KEY = "razor";
	
	public static float rollValue = 0;
	public static float pitchValue = 0;
	public static float yawValue = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		context = getApplicationContext();
		razorDevice = intent.getParcelableExtra("razor");
		initRazor();

		return Service.START_NOT_STICKY;
	}

	private void initRazor() {
		// Create new razor instance and set listener
		razor = new RazorAHRS(razorDevice, new RazorListener() {
			@Override
			public void onConnectAttempt(int attempt, int maxAttempts) {
				Toast.makeText(context, "Connect attempt " + attempt + " of " + maxAttempts + "...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onConnectOk() {
				Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show();
			}

			public void onConnectFail(Exception e) {
				Toast.makeText(context, "Connecting failed: " + e.getMessage() + ".", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAnglesUpdate(float yaw, float pitch, float roll) {
				Log.d("onAnglesUpdate", "does this happen?");
				// PAYLOAD - update the UI and data structures
				//OnyxMotionActivity.addDataPoint(yaw, pitch, roll);
			}

			@Override
			public void onIOExceptionAndDisconnect(IOException e) {
				Toast.makeText(context, "Disconnected, an error occured: " + e.getMessage() + ".", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSensorsUpdate(float accX, float accY, float accZ, float oriX, float oriY, float oriZ, float gyrX, float gyrY, float gyrZ) {
				Log.d("onSensorsUpdate", "got new data: " + accX);
				OnyxMotionActivity.getNewData(accX,accY,accZ,oriX,oriY,oriZ,gyrX,gyrZ,gyrZ);

			}
		});

		// Connect asynchronously
		razor.asyncConnect(5); // 5 connect attempts
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
}
