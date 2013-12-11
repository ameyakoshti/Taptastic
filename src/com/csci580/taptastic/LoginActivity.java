package com.csci580.taptastic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csci580.taptastic.adapter.LoginDataBaseAdapter;

public class LoginActivity extends Activity {
	Button btnSignIn, btnSignUp;
	LoginDataBaseAdapter loginDataBaseAdapter;
	String loggedIn;
	public static String appMode="Student";
	// SharedPreferences prefs;
	SharedPreferences prefsPreviousLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// create a instance of SQLite Database
		loginDataBaseAdapter = new LoginDataBaseAdapter(this);
		loginDataBaseAdapter = loginDataBaseAdapter.open();

		// Get The Reference Of Buttons
		btnSignIn = (Button) findViewById(R.id.buttonSignIN);
		btnSignUp = (Button) findViewById(R.id.buttonSignUP);

		//editTextUserName = (EditText) dialog.findViewById(R.id.editTextUserNameToLogin);
		//editTextPassword = (EditText) dialog.findViewById(R.id.editTextPasswordToLogin);
		
		// Check if the user has already logged in previously
		prefsPreviousLogin = getSharedPreferences("Login", 0);
		if (prefsPreviousLogin.getString("password", null) != null) {
			btnSignIn.performClick();			
		}

		// Set OnClick Listener on SignUp button
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Create Intent for SignUpActivity and Start The Activity
				Intent intentSignUP = new Intent(getApplicationContext(), SignUPActivity.class);
				startActivity(intentSignUP);
			}
		});
	}

	// Methods to handleClick Event of Sign In Button
	public void signIn(View V) {
		final Dialog dialog = new Dialog(LoginActivity.this);
		dialog.setContentView(R.layout.login);
		dialog.setTitle("Login");

		// get the References of views
		final EditText editTextUserName = (EditText) dialog.findViewById(R.id.editTextUserNameToLogin);
		final EditText editTextPassword = (EditText) dialog.findViewById(R.id.editTextPasswordToLogin);
		
		Button btnSignIn = (Button) dialog.findViewById(R.id.buttonSignIn);

		// Set On ClickListener
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// get The User name and Password
				String userName = editTextUserName.getText().toString();
				String password = editTextPassword.getText().toString();

				// Check in database if the credentials are valid
				// fetch the Password form database for respective user name
				String storedPassword = loginDataBaseAdapter.getStoredPassword(userName);

				String storedUSCID = loginDataBaseAdapter.getUSCID(userName);
				// check if the Stored password matches with Password entered by
				// user
				if (password.equals(storedPassword)) {
					Toast.makeText(LoginActivity.this, "Database :Congrats: Login Successfull", Toast.LENGTH_SHORT).show();
					dialog.dismiss();

					if (prefsPreviousLogin.getString("password", null) == null) {
						SharedPreferences sp = getSharedPreferences("Login", 0);
						SharedPreferences.Editor Ed = sp.edit();
						Ed.putString("userName", userName);
						Ed.putString("USCID", storedUSCID);
						Ed.putString("password", password);
						Ed.commit();
					}

					Intent homePage = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(homePage);
					overridePendingTransition(R.anim.incoming, R.anim.outgoing);
				} else {
					Toast.makeText(LoginActivity.this, "Database :User Name or Password does not match", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Check if the user has already logged in previously
		if (prefsPreviousLogin.getString("password", null) != null) {
			editTextUserName.setText(prefsPreviousLogin.getString("userName", null));
			editTextPassword.setText(prefsPreviousLogin.getString("password", null));			
			btnSignIn.performClick();
		}
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Close The Database
		loginDataBaseAdapter.close();
	}
}
