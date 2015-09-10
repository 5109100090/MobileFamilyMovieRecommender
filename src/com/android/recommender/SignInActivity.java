package com.android.recommender;

import com.example.recommender.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SignInActivity extends ActionBarActivity {

	private SQLiteDatabase myDB;
	private EditText userName, userPassword;
	private boolean authenticated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		userName = (EditText) findViewById(R.id.userName);
		userPassword = (EditText) findViewById(R.id.userPassword);
		findViewById(R.id.buttonLogin).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (userName.getText().toString().equals("")
								|| userPassword.getText().toString()
								.equals("")) {
							new MyAlertDialog(SignInActivity.this,
									"Required Data",
									"Input your username and password.");
						} else {
							doLogin(userName.getText().toString(),
									userPassword.getText().toString());
						}
					}
				});

		findViewById(R.id.linkToRegister).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(SignInActivity.this,
								SignUpActivity.class);
						startActivity(intent);
					}
				});

		initDatabase();
	}

	private void doLogin(String username, String password)
	{
		Double latitude = 0.0, longitude = 0.0;
		try {
			myDB = this.openOrCreateDatabase("mydbs.db", MODE_PRIVATE, null);
			/*retrieve data from database */
			Cursor c = myDB.rawQuery("SELECT * FROM user" , null);
			int Column1 = c.getColumnIndex("username");
			int Column2 = c.getColumnIndex("password");
			int colLatitude = c.getColumnIndex("latitude");
			int colLongitude = c.getColumnIndex("longitude");
			// Check if our result was valid.
			c.moveToFirst();
			if (c != null) {
				// Loop through all Results
				do {
					if(c.getString(Column1).equals(username) && c.getString(Column2).equals(new MD5().crypt(password)))
					{
						authenticated = true;
						latitude = c.getDouble(colLatitude);
						longitude = c.getDouble(colLongitude);
						break;
					}
				}while(c.moveToNext());
			}
		}
		catch(Exception e) {
			Log.e("Error", "Error reading DB", e);
		} finally {
			if (myDB != null)
				myDB.close();
		}
		
		if(authenticated)
		{
			userName.setText("");
			userPassword.setText("");
			Intent intent = new Intent(SignInActivity.this,
					MainActivity.class);
			intent.putExtra("userName", username);
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			startActivity(intent);
		}
		else
		{
			new MyAlertDialog(SignInActivity.this,
					"Error Authentication",
					"Invalid username & password.");
		}
	}

	protected void initDatabase()
	{
		myDB= null;
		try{
			myDB = this.openOrCreateDatabase("mydbs.db", MODE_PRIVATE, null);

			createUserTable();
			createHistoryTable();
			emptyTable();
		}
		catch(Exception e) {
			Log.e("Error", "Error initialize DB", e);
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	protected void createUserTable()
	{
		myDB.execSQL("CREATE TABLE IF NOT EXISTS user (username VARCHAR, password VARCHAR, registration_date DATE, latitude DOUBLE, longitude DOUBLE);");		
	}

	protected void createHistoryTable()
	{
		myDB.execSQL("CREATE TABLE IF NOT EXISTS history (username VARCHAR, cinema VARCHAR, movie VARCHAR, session_time TIME, session_date DATE );");
	}
	
	protected void emptyTable()
	{
		//myDB.delete("user",null,null);
		myDB.delete("history",null,null);
	}
}
