package com.jest.razor;


import java.io.IOException;

/**
 * Interface definition for callbacks to be invoked when Razor AHRS events occur.
 * 
 * @author Peter Bartz
 */
public interface RazorListener {

	/**
	 * Invoked when updated yaw/pitch/roll angles are available.
	 * @param yaw
	 * @param pitch
	 * @param roll
	 */
	void onAnglesUpdate(float yaw, float pitch, float roll);

	/**
	 * Invoked when updated accelerometer/magnetometer/gyroscope data is available.
	 * @param accX
	 * @param accY
	 * @param accZ
	 * @param magX
	 * @param magY
	 * @param magZ
	 * @param gyrX
	 * @param gyrY
	 * @param gyrZ
	 */
	void onSensorsUpdate(float accX, float accY, float accZ, float magX, float magY, float magZ,
			float gyrX, float gyrY, float gyrZ);

	/**
	 * Invoked when an IOException occurred.
	 * RazorAHRS will be disconnected already.
	 *
	 * @param e The IOException that was thrown
	 */
	void onIOExceptionAndDisconnect(IOException e);

	/**
	 * Invoked when making an attempt to connect.Because connecting via Bluetooth often fails,
	 * multiple attempts can be made.
	 * See {@link RazorAHRS#RazorAHRS(android.bluetooth.BluetoothDevice, RazorListener, int)}.
	 *
	 * @param attempt Current attempt
	 * @param maxAttempts Maximum number of attempts to be made
	 */
	void onConnectAttempt(int attempt, int maxAttempts);

	/**
	 * Invoked when connecting completed successfully.
	 */
	void onConnectOk();

	/**
	 * Invoked when connecting failed.
	 *
	 * @param e	Exception that was thrown
	 */
	void onConnectFail(Exception e);
}
