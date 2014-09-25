package com.example.tow;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	
	private final Activity context;
	
	private final ArrayList<String> userName;
	private final ArrayList<String> userScore;
	private final ArrayList<String> userRanking;
		
	public CustomList(Activity context, ArrayList<String> userName2, 
		ArrayList<String> userScore2, ArrayList<String> userRanking2) {
		super(context, R.layout.leaderboard_list_row, userName2);
		this.context = context;
		this.userName = userName2;
		this.userScore = userScore2;
		this.userRanking = userRanking2;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.leaderboard_list_row, null, true);
		
		TextView txtUsername = (TextView) rowView.findViewById(R.id.tvLeaderUsername);
		TextView txtScore = (TextView) rowView.findViewById(R.id.tvLeaderScore);
		TextView txtRanking = (TextView) rowView.findViewById(R.id.tvLeaderRanking);
		
		txtUsername.setText(userName.get(position));
		txtScore.setText(userScore.get(position));
		txtRanking.setText(userRanking.get(position));
				
		return rowView;
	}
				
}