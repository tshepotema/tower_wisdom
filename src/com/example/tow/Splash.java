package com.example.tow;

import android.app.Activity;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		//setup the database
		Thread appStatus = new Thread() {
			public void run() {
				try {
					//Call the rankings
					//RankingService rankings = new RankingService(Splash.this);
					//rankings.getUpdatedRankings();
					
					//Call the db installer service
					InstallAppService installApp = new InstallAppService(Splash.this);
					installApp.installAppDB();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		appStatus.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}	

}