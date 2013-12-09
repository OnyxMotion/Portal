package com.jest.database;

import java.util.ArrayList;

import com.jest.database.MotionDatabaseManager.MotionLabel;

public class DBhelper {

	public static String[] toLabelDescriptions(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		String[] labelDescs = new String[len];
		for (int i = 0; i < len; i++) {
			labelDescs[i] = labels.get(i).description;
		}
		return labelDescs;
	}

	public static String[] toLabelSets(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		String[] labelSets = new String[len];
		for (int i = 0; i < len; i++) {
			labelSets[i] = labels.get(i).set;
		}
		return labelSets;
	}

	public static int[] toLabelScores(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		int[] labelScores = new int[len];
		for (int i = 0; i < len; i++) {
			labelScores[i] = labels.get(i).score;
		}
		return labelScores;
	}

	public static long[] toLabelRows(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		long[] labelRs = new long[len];
		for (int i = 0; i < len; i++) {
			labelRs[i] = labels.get(i).row;
		}
		return labelRs;
	}

}
