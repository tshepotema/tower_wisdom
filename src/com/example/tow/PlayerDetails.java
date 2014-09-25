package com.example.tow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PlayerDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_details);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu:
			Intent openMainMenu = new Intent("com.example.tow.MAINMENU");
			startActivity(openMainMenu);
			return true;
		case R.id.action_play:
			Intent playGame = new Intent("com.example.tow.PLAY");
			startActivity(playGame);
			return true;
		case R.id.action_leaderboard:
			Intent showLeaderboard = new Intent("com.example.tow.LEADERBOARD");
			startActivity(showLeaderboard);
			return true;
		case R.id.action_preferences:
			Intent showSettings = new Intent("com.example.tow.USERPROFILE");
			startActivity(showSettings);
			return true;
		case R.id.action_profile:
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}