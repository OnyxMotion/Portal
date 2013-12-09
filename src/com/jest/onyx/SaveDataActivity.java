package com.jest.onyx;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jest.jest.R;

public class SaveDataActivity extends Activity {

	private TextView instructionText;
	private EditText mEditTextDesc;
	private EditText mEditTextSet;
	private EditText mEditTextScore;
	private Button saveButton;
	private Button cancelButton;
	private boolean wasSuccessful = false;

	private String instructionMessage = "Enter a description (name) for your motion. Categorize it into a "
			+ "set (e.g. free-throws). Choose a numeric score label. Please make sure valid "
			+ "data is selected from the motion graph on the previous page.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_data_activity);

		mEditTextDesc = (EditText) findViewById(R.id.new_data_description);
		mEditTextSet = (EditText) findViewById(R.id.new_data_set);
		mEditTextScore = (EditText) findViewById(R.id.new_data_score);
		saveButton = (Button) findViewById(R.id.save_dialog_save);
		cancelButton = (Button) findViewById(R.id.save_dialog_cancel);

		instructionText = (TextView) findViewById(R.id.instruction_text);
		instructionText.setText(instructionMessage);

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String desc = mEditTextDesc.getText().toString();
				final String set = mEditTextSet.getText().toString();

				final int score;
				try {
					score = Integer.parseInt(mEditTextScore.getText().toString());
				} catch (Exception e) {
					Toast.makeText(SaveDataActivity.this, "Please enter all fields.", Toast.LENGTH_LONG).show();
					return;
				}

				Toast.makeText(SaveDataActivity.this, "Saving your motion, please wait...", Toast.LENGTH_LONG).show();

				Thread t = new Thread() {
					public void run() {
						try {

							float[] x = OnyxMotionActivity.getMotionX();
							float[] y = OnyxMotionActivity.getMotionY();
							float[] z = OnyxMotionActivity.getMotionZ();
							if (x == null || y == null || z == null) {
								// Toast.makeText(getActivity(),
								// "Failed: not enough motion data.",
								// Toast.LENGTH_LONG).show();
								return;
							}
							long row = OnyxMotionActivity.MDM.createMotionLabel(desc, set, score);
							boolean i = OnyxMotionActivity.MDM.addMotionData(row, x, y, z);
							if (!OnyxMotionActivity.MDM.doesSetExist(set)) {
								long newSet = OnyxMotionActivity.MDM.createMotionSet(set, 0, 0);

							}
							wasSuccessful = true;
						} catch (Exception e) {
							wasSuccessful = false;
							Log.e("SaveDataActivity", e.toString());
						}
						// dismiss the progress dialog
					}
				};

				t.start();

				try {
					t.join();
					if (wasSuccessful) {
						Toast.makeText(SaveDataActivity.this, "Motion saved successfully.", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(SaveDataActivity.this, "Motion save failed. Have you entered all fields correctly?", Toast.LENGTH_LONG).show();
					}
				} catch (InterruptedException e) {
					Toast.makeText(SaveDataActivity.this, "Threading error", Toast.LENGTH_LONG).show();
				}

				finishActivity(0);
				//return;

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});

	}

}
