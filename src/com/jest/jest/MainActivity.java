package com.jest.jest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jest.onyx.OnyxMotionActivity;
import com.jest.phone.PhoneActivity;
import com.jest.razor.RazorExample;

public class MainActivity extends Activity {

	private Button chooseRazorSensor;
	private Button choosePhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		chooseRazorSensor = (Button) findViewById(R.id.choose_razor);
		choosePhone = (Button) findViewById(R.id.choose_phone);

		chooseRazorSensor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, RazorExample.class);
				startActivity(i);
			}
		});

		choosePhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, OnyxMotionActivity.class);
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
