package com.jest.phone;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jest.database.MotionDatabaseManager.DataArrays;
import com.jest.database.MotionDatabaseManager.MotionLabel;
import com.jest.jest.R;


public class ViewMotionDataListFragment extends ListFragment {

	private static String[] labelDescriptions;
	private static long[] labelRows;
	private static String[] labelSets;
	private static int[] labelScores;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO - string array for descriptions
		ArrayList<MotionLabel> labelDescs = PhoneActivity.MDM.getMotionLabels();
		labelDescriptions = toLabelDescriptions(labelDescs);
		labelRows = toLabelRows(labelDescs);
		labelSets = toLabelSets(labelDescs);
		labelScores = toLabelScores(labelDescs);
		
		ViewMotionDataListAdapter myListAdapter = new ViewMotionDataListAdapter(getActivity(), labelDescriptions, labelRows, labelSets, labelScores);
		//ListAdapter myListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, labelDescriptions);
		setListAdapter(myListAdapter);

	}

	// The fragment can use these instead of requerying.
	public static String[] getLabelDescriptions() {
		return labelDescriptions;
	}

	// TODO - these static variables being passed around should at least be
	// apart of MainActivity.MDM!!!
	public static long[] getLabelRows() {
		return labelRows;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.view_motion_data_listview, container, false);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		String title = getListView().getItemAtPosition(position).toString();
		//Toast.makeText(getActivity(), title + " / row: " + labelRows[position], Toast.LENGTH_LONG).show();

		ViewMotionDataFragment.updateTitle(title);
		DataArrays data = PhoneActivity.MDM.getMotionData(labelRows[position]);
		int len = data.dataX.size();
		ArrayList<Float> x = data.dataX;
		ArrayList<Float> y = data.dataY;
		ArrayList<Float> z = data.dataZ;
		ViewMotionDataFragment.showNewData(len, x, y, z);
	}

	public String[] toLabelDescriptions(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		String[] labelDescs = new String[len];
		for (int i = 0; i < len; i++) {
			labelDescs[i] = labels.get(i).description;
		}
		return labelDescs;
	}
	
	public String[] toLabelSets(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		String[] labelSets = new String[len];
		for (int i = 0; i < len; i++) {
			labelSets[i] = labels.get(i).set;
		}
		return labelSets;
	}
	
	public int[] toLabelScores(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		int[] labelScores = new int[len];
		for (int i = 0; i < len; i++) {
			labelScores[i] = labels.get(i).score;
		}
		return labelScores;
	}

	public long[] toLabelRows(ArrayList<MotionLabel> labels) {
		int len = labels.size();
		long[] labelRs = new long[len];
		for (int i = 0; i < len; i++) {
			labelRs[i] = labels.get(i).row;
		}
		return labelRs;
	}

}
