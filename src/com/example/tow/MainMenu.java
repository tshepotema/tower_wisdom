package com.example.tow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.tow.ImageAdapter;

public class MainMenu extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.grid_menu);

	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						Intent playGame = new Intent("com.example.tow.PLAY");
						startActivity(playGame);
						break;
					case 1:
						Intent showLeaderboard = new Intent("com.example.tow.LEADERBOARD");
						startActivity(showLeaderboard);
						break;
					case 2:
						Intent showUserDetails = new Intent("com.example.tow.PLAYERDETAILS");
						startActivity(showUserDetails);
						break;
					case 3:
						Intent showSettings = new Intent("com.example.tow.USERPROFILE");
						startActivity(showSettings);
						break;
						
				}
			}	        	        
	    });
	    
	}
	
}
