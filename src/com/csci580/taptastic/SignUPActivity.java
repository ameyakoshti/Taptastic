package com.csci580.taptastic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csci580.taptastic.adapter.LoginDataBaseAdapter;

public class SignUPActivity extends Activity {
	EditText editTextUserName, editTextPassword, editTextConfirmPassword, editTextUSCID;
	Button btnCreateAccount;

	LoginDataBaseAdapter loginDataBaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		// get Instance of Database Adapter
		loginDataBaseAdapter = new LoginDataBaseAdapter(this);
		loginDataBaseAdapter = loginDataBaseAdapter.open();

		// Get References of Views
		editTextUserName = (EditText) findViewById(R.id.editTextUserName);
		editTextUSCID = (EditText) findViewById(R.id.editTextUSCID);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

		btnCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
		btnCreateAccount.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String userName = editTextUserName.getText().toString();
				String uscID = editTextUSCID.getText().toString();
				String password = editTextPassword.getText().toString();
				String confirmPassword = editTextConfirmPassword.getText().toString();

				// check if any of the fields are vacant
				if (userName.equals("") || password.equals("") || confirmPassword.equals("")) {
					Toast.makeText(getApplicationContext(), "All fields are mandatory", Toast.LENGTH_LONG).show();
					return;
				}
				// check if both password matches
				if (!password.equals(confirmPassword)) {
					Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
					return;
				} else {
					// Save the Data in Database
					loginDataBaseAdapter.insertEntry(userName, uscID, password);

					// Save the Data in Servlet
					ServletCalls createAccount = new ServletCalls();
					createAccount.execute("1", userName, uscID, password);

					Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();

					Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(loginPage);
					// overridePendingTransition(R.anim.left_to_right,
					// R.anim.right_to_left);
					overridePendingTransition(R.anim.incoming, R.anim.outgoing);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loginDataBaseAdapter.close();
	}
}