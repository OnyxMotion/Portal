package com.jest.phone;

import com.jest.jest.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveDataDialog extends DialogFragment {

	private EditText mEditTextDesc;
	private EditText mEditTextSet;
	private EditText mEditTextScore;
	private Button saveButton;
	private Button cancelButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.save_data_activity, container, false);

		mEditTextDesc = (EditText) v.findViewById(R.id.new_data_description);
		mEditTextSet = (EditText) v.findViewById(R.id.new_data_set);
		mEditTextScore = (EditText) v.findViewById(R.id.new_data_score);
		saveButton = (Button) v.findViewById(R.id.save_dialog_save);
		cancelButton = (Button) v.findViewById(R.id.save_dialog_cancel);

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String desc = mEditTextDesc.getText().toString();
				final String set = mEditTextSet.getText().toString();
				final int score = Integer.parseInt(mEditTextScore.getText().toString());

				final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Please wait...");
				new Thread() {
					public void run() {
						try {
							
							float[] x = PhoneActivity.getMotionX();
							float[] y = PhoneActivity.getMotionY();
							float[] z = PhoneActivity.getMotionZ();
							if (x == null || y == null || z == null) {
								//Toast.makeText(getActivity(), "Failed: not enough motion data.", Toast.LENGTH_LONG).show();
								progressDialog.dismiss();
								return;
							}
							long row = PhoneActivity.MDM.createMotionLabel(desc, set, score);
							boolean i = PhoneActivity.MDM.addMotionData(row, x, y, z);
							if (!PhoneActivity.MDM.doesSetExist(set)) {
								long newSet = PhoneActivity.MDM.createMotionSet(set, 0, 0);
								Toast.makeText(getActivity(), "netSet: " + newSet, Toast.LENGTH_LONG).show();
							}
//							Toast.makeText(getActivity(), "Row: " + row, Toast.LENGTH_LONG).show();
//							if (i == true)
//								Toast.makeText(getActivity(), "SUCCESS!", Toast.LENGTH_LONG).show();
//							else 
//								Toast.makeText(getActivity(), "FAILED TO INSERT MOTION DATA", Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							Log.e("tag", e.toString());
						}
						// dismiss the progress dialog
						progressDialog.dismiss();
					}
				}.start();
				return;

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});

		return v;
	}

}
