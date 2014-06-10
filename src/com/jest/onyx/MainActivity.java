package com.jest.onyx;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jest.getdata.DataHelper;
import com.jest.jest.R;

public class MainActivity extends Activity {
	
	private EditText  username;
	private EditText  password;
	private TextView attempts;
	private Button login;
	private int counter = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	    username = (EditText) findViewById(R.id.username);
	    password = (EditText) findViewById(R.id.password);
	    login = (Button) findViewById(R.id.loginButton);
		attempts = (TextView)findViewById(R.id.attempts);
	    attempts.setText("Login Attempts: " + Integer.toString(counter));
	    
	    DataHelper.testFileIO(getBaseContext());
	}

	public void login(View view){
		if (DataHelper.setUser(username.getText().toString(), 
			password.getText().toString())) {
			startActivity(new Intent(this, MainDashboard.class));
		} else {
		    Toast.makeText(getApplicationContext(), "Wrong Credentials",
		    	Toast.LENGTH_SHORT).show();	
		    attempts.setText("Login Attempts: " + Integer.toString(--counter));
		    if (counter == 0) login.setEnabled(false);
		}
	}
}
