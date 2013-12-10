package com.jest.analysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

import com.androidplot.xy.SimpleXYSeries;

public class MotionAnalyzer {

	private float[] xStream;
	private float[] yStream;
	private float[] zStream;
	private int len1;

	public MotionAnalyzer(int maxSize) {
		maxSize++;
		xStream = new float[maxSize];
		yStream = new float[maxSize];
		zStream = new float[maxSize];
		for (int i = 0; i < maxSize; i++) {
			xStream[i] = 0;
			yStream[i] = 0;
			zStream[i] = 0;
		}
	}

	public float[] getX1() {
		return xStream;
	}

	public float[] getY1() {
		return yStream;
	}

	public float[] getZ1() {
		return zStream;
	}

	// Smoothing and DTW; returns scores in array
	public float[] testAlgorithm1(LinkedList<Float> xS, LinkedList<Float> yS, LinkedList<Float> zS, float[] xT, float[] yT, float[] zT, int templateLength,
			int analysisEveryX) {
		float[] result = new float[4]; // return 3 metrics, for matches in x, y,
										// z

		// Preconditioning: remove angle discontinuities, low-pass filter, and
		// take first derivative
		removeDiscontinuitiesAndConvert(this.xStream, xS, templateLength);
		removeDiscontinuitiesAndConvert(this.yStream, yS, templateLength);
		removeDiscontinuitiesAndConvert(this.zStream, zS, templateLength);
		removeDiscontinuities(zT, templateLength);
		removeDiscontinuities(yT, templateLength);
		removeDiscontinuities(zT, templateLength);
		HanningFilter(this.xStream, templateLength);
		HanningFilter(this.yStream, templateLength);
		HanningFilter(this.zStream, templateLength);
		HanningFilter(xT, templateLength);
		HanningFilter(yT, templateLength);
		HanningFilter(zT, templateLength);
		this.xStream = firstDerivative(this.xStream, templateLength);
		this.yStream = firstDerivative(this.yStream, templateLength);
		this.zStream = firstDerivative(this.zStream, templateLength);
		xT = firstDerivative(xT, templateLength);
		yT = firstDerivative(yT, templateLength);
		zT = firstDerivative(zT, templateLength);
		//
		// // Find likey start locations:
		// int xIndexAligned = 0;//alignSequenceStart(this.xStream,
		// this.xTemplate, analysisEveryX);
		// int yIndexAligned = 0;//alignSequenceStart(this.yStream,
		// this.yTemplate, analysisEveryX);
		// int zIndexAligned = 0;//alignSequenceStart(this.zStream,
		// this.zTemplate, analysisEveryX);
		//
		// // DTW(arr1, arr2, lengthToAnalyze, offset1, offset2)
		// // Analysis from offset1 to offset1+lengthToAnalyze of arr1, offset2
		// to
		// // offset2+lengthToAnalyze of arr2
		 result[0] = DTW(this.xStream, xT, templateLength, 0, 0);
		 result[1] = DTW(this.yStream, yT, templateLength, 0, 0);
		 result[2] = DTW(this.zStream, zT, templateLength, 0, 0);

		// // scale DTW scores by power
		// // assume signal1 is the reference
		// // because we don't care as much if a signal that doesn't change much
		// // has a worse score
		// result[3] = finalScoreModel(result[0], result[1], result[2]);

		result[3] = (float) Math.sqrt(result[0]*result[0] + result[1]*result[1] + result[2]*result[2]);
		return result;
	}

	public float finalScoreModel(float x, float y, float z) {
		float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

		float finalScore = (float) (108.72 * Math.exp(magnitude * -0.004));
		if (finalScore > 100)
			finalScore = 100;

		return round(finalScore, 2);

	}

	public void setDataSet1(SimpleXYSeries sx1, SimpleXYSeries sy1, SimpleXYSeries sz1) {
		int len = sx1.size();
		len1 = len;
		xStream = new float[len];
		yStream = new float[len];
		zStream = new float[len];
		for (int i = 0; i < len; i++) {
			xStream[i] = sx1.getY(i).floatValue();
			yStream[i] = sy1.getY(i).floatValue();
			zStream[i] = sz1.getY(i).floatValue();
		}
	}

	public ArrayList<Float> float2ArrayList(float[] data) {
		int len = data.length;
		ArrayList<Float> dataArrayList = new ArrayList<Float>();
		for (int i = 0; i < len; i++) {
			dataArrayList.add(data[i]);
		}
		return dataArrayList;
	}

	public float[] arrayList2Float(ArrayList<Float> data) {
		int len = data.size();
		float[] res = new float[len];
		for (int i = 0; i < len; i++) {
			res[i] = data.get(i);
		}
		return res;
	}

	// e.g. HanningFilter(this.x1); arrays are mutable
	public void HanningFilter(float[] array, int len) {
		int i;
		for (i = 2; i < len - 3; i += 3) {
			array[i] = 0.25f * (array[i] + 2 * array[i - 1] + array[i - 2]);
			array[i + 1] = 0.25f * (array[i + 1] + 2 * array[i] + array[i - 1]);
			array[i + 2] = 0.25f * (array[i + 2] + 2 * array[i + 1] + array[i]);
		}
		// Clean-up at the end
		for (int j = i; j < len; j++) {
			array[i] = 0.25f * (array[i] + 2 * array[i - 1] + array[i - 2]);
		}
	}

	public float[] firstDerivative(float[] data, int len) {
		// (signal(i + 1) - signal(i - 1))/2
		float[] ddx = new float[len];
		for (int i = 1; i < len - 1; i++) {
			ddx[i] = (data[i] - data[i - 1]) + (data[i + 1] - data[i - 1]) / 2;
			// ddx[i] = 5 * (data[i + 1] - data[i - 1]) / 2;
		}
		return ddx;
	}

	// Removes angle discontinuities, i.e. jumps of 180 degrees.
	// Preconditions: requires that all possible data series range from (-180,
	// 180)
	// rather than, say, 0 to 360.
	public float[] removeDiscontinuities(float[] data) {
		int len = data.length;
		float[] fixed = new float[len];
		int multipleToAdd = 0;
		float prevVal = data[0]; // initialize

		for (int i = 0; i < len; i++) {
			float curVal = data[i];

			if ((prevVal - curVal) > 178) {
				multipleToAdd++;
			}
			if ((curVal - prevVal) > 178) {
				multipleToAdd--;
			}

			prevVal = curVal;
			fixed[i] = curVal + 360 * multipleToAdd;
		}

		return fixed;
	}

	// Removes angle discontinuities, i.e. jumps of 180 degrees.
	// Preconditions: requires that all possible data series range from (-180,
	// 180)
	// rather than, say, 0 to 360.
	// Also converts data format to float[]
	public void removeDiscontinuitiesAndConvert(float[] result, LinkedList<Float> data, int templateLength) {
		int multipleToAdd = 0;
		float prevVal = data.get(0); // initialize

		for (int i = 0; i < templateLength; i++) {
			float curVal = data.get(i);

			if ((prevVal - curVal) > 178) {
				multipleToAdd++;
			}
			if ((curVal - prevVal) > 178) {
				multipleToAdd--;
			}

			prevVal = curVal;
			try {
				result[i] = curVal + 360 * multipleToAdd;
			} catch (Exception e) {
				Log.d("removeDiscontinuities", "result.length:" + result.length + " / i:" + i + " / templateLength:" + templateLength);
			}
		}

	}

	public void removeDiscontinuities(float[] data, int templateLength) {
		int multipleToAdd = 0;
		float prevVal = data[0]; // initialize

		for (int i = 0; i < templateLength; i++) {
			float curVal = data[0];

			if ((prevVal - curVal) > 178) {
				multipleToAdd++;
			}
			if ((curVal - prevVal) > 178) {
				multipleToAdd--;
			}

			prevVal = curVal;
			// try {
			data[i] = curVal + 360 * multipleToAdd;
			// } catch (Exception e) {
			// Log.d("removeDiscontinuities",
			// "result.length:"+result.length+" / i:"+i+" / templateLength:"+templateLength);
			// }
		}

	}

	// Precondition: startLength << array.length
	// Find which index gives the optimal alignment between the beginning of
	// template[]
	// as it's slid over series[]
	public int alignSequenceStart(float[] series, float[] template, int startLength) {
		int index = 0;
		float distance = 999999999; // distance is big because we're re-using
									// DTW to find
		float test;
		// an optimal start alignment
		// TODO - make sure we're selecting series[i: i+startLength]
		for (int i = 0; i < startLength; i++) {

			// float[] thisSeries = null;
			// System.arraycopy(series, i, thisSeries, 0, startLength);

			test = DTW(series, template, startLength, i, 0);

			if (test < distance) {
				index = i; // current winner
			}
		}
		return index;
	}

	// TODO - can probably try a band constraint in the cost matrix DTW
	// Note: only analyzing over templateLength # of points in both
	// DTW(arr1, arr2, lengthToAnalyze, offset1, offset2)
	// Analysis from offset1 to offset1+lengthToAnalyze of arr1, offset2 to
	// offset2+lengthToAnalyze of arr2
	public float DTW(float[] array1, float[] array2, int templateLength, int array1IndexOffset, int array2IndexOffset) {
		float[][] DTW;
		int len1 = templateLength;// + array1IndexOffset; // array1.length;
		int len2 = templateLength;// + array2IndexOffset; // array2.length;
		DTW = new float[len1][len2];

		// Initialization
		for (int i = 0; i < len2; i++) {
			DTW[0][i] = 9999999999.0f; // really big number
		}
		for (int i = 0; i < len1; i++) {
			DTW[i][0] = 9999999999.0f; // really big number
		}
		DTW[0][0] = 0;

		// Switching to windowed version:
		// j goes from i-R to i+R where R is window width; say length of
		// template1 / 2 (though we're assuming they're the same here)

		// Main loop
		int iStart = 1;// 1 + array1IndexOffset;
		int jStart = 1;// 1 + array2IndexOffset;
		for (int i = iStart; i < len1; i++) {
			for (int j = jStart; j < len2; j++) {
				float cost = euclidDistance(array1[i], array2[j]);
				DTW[i][j] = cost + returnMin(DTW[i - 1][j], DTW[i][j - 1], DTW[i - 1][j - 1]);
			}
		}

		// Normalization attempt?
		float max1 = returnAbsMax(array1);
		float max2 = returnAbsMax(array1);
		float max = returnAbsMax(new float[] { max1, max2 });

		return DTW[len1 - 1][len2 - 1] / max;

	}

	public float[] FFTmagnitude(float[] data) {
		int originalLen = data.length;
		int newLen = 2;
		while (newLen <= originalLen) {
			newLen = newLen * newLen; // keep going up in powers of 2 until we
										// bigger
		}
		Complex[] complexData = new Complex[newLen];
		int i = 0;
		for (i = 0; i < originalLen; i++) {
			complexData[i] = new Complex(data[i], 0);
		}
		for (; i < newLen; i++) {
			complexData[i] = new Complex(0, 0); // zero pad the rest
		}

		Complex[] FFTresult = FFT.fft(complexData);
		float[] result = FFT.magnitude(FFTresult);

		return result;
	}

	public float euclidDistance(float x, float y) {
		return (float) Math.pow((x - y), 2.0);
	}

	public float vectorEuclidDistance(float[] x, float[] y, int len) {
		float sum = 0;
		for (int i = 0; i < len; i++) {
			sum += euclidDistance(x[i], y[i]);
		}
		return sum;
	}

	public float signalAbsAvg(float[] data) {
		int len = data.length;
		float total = 0;
		for (int i = 0; i < len; i++) {
			total += Math.abs(data[i]);
		}
		return total / len; // some normalizing
	}

	public LinkedList<Float> toLinkedList(ArrayList<Float> arrayList) {
		int size = arrayList.size();
		LinkedList<Float> data = new LinkedList<Float>();
		for (int i = 0; i < size; i++) {
			data.add(arrayList.get(i));
		}
		return data;
	}

	private float[] toFloatArray(LinkedList<Float> list, int size) {
		if (list.size() < size)
			return null;
		float[] newData = new float[size];
		for (int i = 0; i < size; i++) {
			newData[i] = list.get(i);
		}
		return newData;
	}

	public static float[] toFloatArray(ArrayList<Float> list, int size) {
		if (list.size() < size)
			return null;
		float[] newData = new float[size];
		for (int i = 0; i < size; i++) {
			newData[i] = list.get(i);
		}
		return newData;
	}

	public float returnMin(float a, float b) {
		if (a <= b)
			return a;
		else
			return b;
	}

	public float returnMin(float a, float b, float c) {
		float y;
		if ((a <= b) && (a <= c)) {
			y = a;
		} else if ((b <= a) && (b <= c)) {
			y = b;
		} else {
			y = c;
		}
		return y;
	}

	public int returnAbsMax(float[] data) {
		int len = data.length;
		int max = -999999999;
		for (int i = 0; i < len; i++) {
			if (data[i] > max) {
				max = Math.round(Math.abs(data[i]));
			}
		}
		return max;
	}

	public int returnMin(float[] data) {
		int len = data.length;
		int min = 999999999;
		for (int i = 0; i < len; i++) {
			if (data[i] < min) {
				min = Math.round(data[i]);
			}
		}
		return min;
	}

	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}
