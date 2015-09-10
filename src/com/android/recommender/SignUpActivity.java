package com.android.recommender;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.recommender.R;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends ActionBarActivity {

	private SQLiteDatabase myDB;
	private String errorMessage = null;
	private EditText userName, userPassword, userPasswordConfirmation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		userName = (EditText) findViewById(R.id.userName);
		userPassword = (EditText) findViewById(R.id.userPassword);
		userPasswordConfirmation = (EditText) findViewById(R.id.userPassword2);
		findViewById(R.id.buttonRegister).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (validateInput(userName.getText().toString(), userPassword.getText().toString(), userPasswordConfirmation.getText().toString())) {
							doRegister(userName.getText().toString(), userPassword.getText().toString());
						} else {
							new MyAlertDialog(SignUpActivity.this,
									"Input validation",
									errorMessage);
						}
					}
				});
	}
	
	private boolean validateInput(String username, String password, String password2)
	{
		if(username.equals("") || password.equals("") || password2.equals(""))
		{
			errorMessage = "All fields are required";
			return false;
		}
		else if(username.length() <= 5 || password.length() <= 5)
		{
			errorMessage = "Username and password must be more than 5 characters";
			return false;
		}
		else if(!password.equals(password2))
		{
			errorMessage = "Double check your password";
			return false;
		}
		else
		{
			try {
				myDB = this.openOrCreateDatabase("mydbs.db", MODE_PRIVATE, null);
				Cursor c = myDB.rawQuery("SELECT * FROM user WHERE username = '" + username + "'"  , null);
				if(c.getCount() != 0)
				{
					errorMessage = "Username already exist";
					return false;
				}
			}
			catch(Exception e) {
				Log.e("Error", "Error reading DB", e);
			} finally {
				if (myDB != null)
					myDB.close();
			}
		}
		
		return true;
	}
	
	private void doRegister(String username, String password)
	{
		try {
			myDB = this.openOrCreateDatabase("mydbs.db", MODE_PRIVATE, null);
			ContentValues values = new ContentValues();
			values.put("username", username);
			values.put("password", new MD5().crypt(password));
			values.put("registration_date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			values.put("latitude", "-37.811567");
			values.put("longitude", "144.964968");
			myDB.insert("user", null, values);
			Toast toast = Toast.makeText(getApplicationContext(), "Your registration is successfull", Toast.LENGTH_SHORT);
			toast.show();
			finish();
		}
		catch(Exception e) {
			Log.e("Error", "Error reading DB", e);
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}
}
