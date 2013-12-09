package com.jest.phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jest.jest.R;

public class ViewMotionDataListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final long[] labelRows;
	private final String[] labelDescriptions;
	private final String[] labelSets;
	private final int[] labelScores;

	public ViewMotionDataListAdapter(Context context, String[] labelDescriptions, long[] labelRows, String[] labelSets, int[] labelScores) {
		super(context, R.layout.view_motion_data_list_row, labelDescriptions);
		this.context = context;
		this.labelDescriptions = labelDescriptions;
		this.labelRows = labelRows;
		this.labelSets = labelSets;
		this.labelScores = labelScores;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.view_motion_data_list_row, parent, false);

		TextView desc = (TextView) v.findViewById(R.id.list_row_description);
		TextView set = (TextView) v.findViewById(R.id.list_row_set);
		TextView score = (TextView) v.findViewById(R.id.list_row_score);

		desc.setText(this.labelDescriptions[position]);
		set.setText("Set: " + this.labelSets[position]+"  /  ");
		score.setText("Score: " + this.labelScores[position]);

		return v;
	}

}
