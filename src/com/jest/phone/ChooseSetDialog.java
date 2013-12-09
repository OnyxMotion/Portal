package com.jest.phone;

import java.util.ArrayList;

import com.jest.jest.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ChooseSetDialog extends DialogFragment {

	private final String CHOOSE_SET_STRING = "Choose a set";
	private Spinner chooseSet = null;
	private ArrayList<String> spinnerMotionSetsArray = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.choose_set_dialog, container, false);

		chooseSet = (Spinner) v.findViewById(R.id.choose_set_spinner);

		ArrayList<String> temp = PhoneActivity.MDM.getSets();
		int len = temp.size();
		spinnerMotionSetsArray = new ArrayList<String>();
		spinnerMotionSetsArray.add(0, CHOOSE_SET_STRING);
		for (int i = 0; i < len; i++) {
			spinnerMotionSetsArray.add(i + 1, temp.get(i));
		}
		//Toast.makeText(getActivity(), "length: " + spinnerMotionSetsArray.size(), Toast.LENGTH_LONG).show();
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
				spinnerMotionSetsArray);
		chooseSet.setAdapter(spinnerArrayAdapter);
		chooseSet.setSelected(false);
		chooseSet.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String selection = spinnerMotionSetsArray.get(position);
				// Toast.makeText(getActivity(), "Spinner item selected: " +
				// selection + "/" + row, Toast.LENGTH_LONG).show();
				if (!selection.equals(CHOOSE_SET_STRING)) {
					Intent i = new Intent(getActivity(), BuildMotionSet.class);
					i.putExtra("set", selection);
					startActivity(i);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		return v;
	}

}
