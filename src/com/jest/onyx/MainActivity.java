package com.jest.onyx;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jest.jest.R;

public class MainActivity extends Activity {
	
	
	private EditText  username=null;
	   private EditText  password=null;
	   private TextView attempts=null;
	   private Button login;
	   int counter = 4;
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.login);
	      username = (EditText)findViewById(R.id.username);
	      password = (EditText)findViewById(R.id.password);

	      login = (Button)findViewById(R.id.loginButton);
	   }

	   public void login(View view){
	      if(username.getText().toString().equals("admin") && 
	      password.getText().toString().equals("admin")){
	      Toast.makeText(getApplicationContext(), "Redirecting...", 
	      Toast.LENGTH_SHORT).show();
	      startActivity(new Intent(this, MainDashboard.class));
	   }	
	   else{
		      attempts = (TextView)findViewById(R.id.attempts);
		      attempts.setText("Login Attempts:" + Integer.toString(counter));
	      Toast.makeText(getApplicationContext(), "Wrong Credentials",
	      Toast.LENGTH_SHORT).show();	
	      counter--;
	      attempts.setText(Integer.toString(counter));
	      if(counter==0){
	         login.setEnabled(false);
	      }

	   }

	}

	
}
