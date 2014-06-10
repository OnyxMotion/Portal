package com.jest.onyx;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SkillSetList extends ListActivity {

	static final String[] MEASUREMENTS = new String[] {
		"Score", "Elbow Angle", "Release Speed", "Release Angle" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_1, MEASUREMENTS));
		getListView().setTextFilterEnabled(true);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		startActivity(new Intent(this, SkillListDetail.class).putExtra(
			"MEASUREMENT", position));
	}

}