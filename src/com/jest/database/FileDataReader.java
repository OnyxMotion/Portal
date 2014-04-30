package com.jest.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class FileDataReader {

	//FORMAT: values come one after another
	public static float[] readVectorData(Context context, int rawResourceID) {
		
		
		InputStream inputStream = context.getResources().openRawResource(rawResourceID);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		ArrayList<Float> temp = new ArrayList<Float>();
		String line; 
		try {
			while ((line = reader.readLine()) != null) {
				temp.add(Float.parseFloat(line));
			}
		} catch (Exception e) {
			Toast.makeText(context, "readCSVdata failed: "+e.toString(), Toast.LENGTH_LONG).show();
		}
		
		int len = temp.size();
		float[] ret = new float[len];
		for (int i = 0; i < len; i++) {
			ret[i] = temp.get(i);
		}
		temp = null;
		
		return ret;
		
	}

}
