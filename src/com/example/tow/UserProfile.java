package com.example.tow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class UserProfile extends Activity {
	
	EditText etUsername;
	RadioGroup rgDifficulty;
	RadioButton rbEasy, rbIntermediate, rbDifficult;
	Button btUpdate;
	Integer selectedDifficulty;

	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String nameKey = "name_key"; 
	public static final String diffKey = "diff_key"; 
	
	SharedPreferences sharedPref;
	Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		initializeLayout();
		
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		etUsername.setText(sharedPref.getString(nameKey, "Guest"));
		selectedDifficulty = sharedPref.getInt(diffKey, 2);
	      	      
		switch (selectedDifficulty) {
			case 1:
				rbEasy.setChecked(true);
				break;
			case 2:
				rbIntermediate.setChecked(true);
				break;
			case 3:
				rbDifficult.setChecked(true);
				break;
		}		
		
		btUpdate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// update the shared preferences
				String username = etUsername.getText().toString();
				switch (rgDifficulty.getCheckedRadioButtonId()) {
				case R.id.rbEasy:
						selectedDifficulty = 1;
					break;
				case R.id.rbIntermediate:
						selectedDifficulty = 2;
					break;
				case R.id.rbDifficult:
						selectedDifficulty = 3;
					break;
				}
				
				editor.putString(nameKey, username);
				editor.putInt(diffKey, selectedDifficulty);
				
				//save changes in SharedPreferences
				editor.commit();
				
				onPause();
			}
		});								
	}
	
	private void initializeLayout() {
		etUsername = (EditText) findViewById(R.id.etUsername);
		rgDifficulty = (RadioGroup) findViewById(R.id.rgDifficulty);
		rbEasy = (RadioButton) findViewById(R.id.rbEasy);
		rbIntermediate = (RadioButton) findViewById(R.id.rbIntermediate);
		rbDifficult = (RadioButton) findViewById(R.id.rbDifficult);
		btUpdate = (Button) findViewById(R.id.btUpdate);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_menu) {
			Intent openMainMenu = new Intent("com.example.tow.MAINMENU");
			startActivity(openMainMenu);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
			
}