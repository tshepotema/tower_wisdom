package com.example.tow;

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.tow.database.*;

public class Play extends Activity {

	TextView tvQuestion, tvTimer,  tvUser, tvScore, tvLevel;
	RadioGroup rgAnswers;
	RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
	ProgressBar pbProgress;
	
	String[][] qnaArray = new String[10][6];
	Integer groupID, playTimer, playTimerSeconds, currentQuestionID;
	CountDownTimer playTimerCounter;
	String correctAnswer, previousAnswer;
	String[] correctAcknowledge = {"Well Done!", "Correct!", "Yessss!", "Yupppp!", "Absolutely!"};
	Boolean timeUp = false;
	Boolean lifeline_time, lifeline_simplify;
		
	Integer score, scoreWeight, scorePenalty, playDifficulty;
	
	Integer userID;	
	
	private Handler progressHandler = new Handler();
	Menu playMenu;

	SharedPreferences sharedPref;
	Editor editor;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_layout);
		
		initializeLayout();
		
		sharedPref = getSharedPreferences(UserProfile.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		tvUser.setText(sharedPref.getString(UserProfile.nameKey, "ttema"));
		playDifficulty = sharedPref.getInt(UserProfile.diffKey, 2);

		setGameDefaults();
		
		//TODO: get current score if available
		score = 0;
		
		//TODO: get the correct User ID
		userID = 1;
		
		//TODO: get the correct Group ID
		groupID = 1;
		
		//TODO: initialize the time per question
		resetPlayTimer();		
		
		playTimer = playTimerSeconds * 1000;
		
		//initialize other variables
		previousAnswer = "just_for_debugging_ne";
		
		//load the QnAs from storage
		getAllQnAs(groupID);
				
		rgAnswers.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String userAnswer = "";
				Boolean rbChecked = false;
				switch (checkedId) {
				case R.id.rbAnswer1:
					userAnswer = (String) rbAnswer1.getText();
					if (rbAnswer1.isChecked()) {
						rbChecked = true;
					} else {
						if (userAnswer.equals(previousAnswer)) {
							rbChecked = false;
						} else {
							rbChecked = true;
						}
					}
					previousAnswer = (String) rbAnswer1.getText();
					break;
				case R.id.rbAnswer2:
					userAnswer = (String) rbAnswer2.getText();
					if (rbAnswer2.isChecked()) {
						rbChecked = true;
					} else {
						if (userAnswer.equals(previousAnswer)) {
							rbChecked = false;
						} else {
							rbChecked = true;
						}
					}
					previousAnswer = (String) rbAnswer2.getText();
					break;
				case R.id.rbAnswer3:
					userAnswer = (String) rbAnswer3.getText();
					if (rbAnswer3.isChecked()) {
						rbChecked = true;
					} else {
						if (userAnswer.equals(previousAnswer)) {
							rbChecked = false;
						} else {
							rbChecked = true;
						}
					}
					previousAnswer = (String) rbAnswer3.getText();
					break;
				case R.id.rbAnswer4:
					userAnswer = (String) rbAnswer4.getText();
					if (rbAnswer4.isChecked()) {
						rbChecked = true;
					} else {
						if (userAnswer.equals(previousAnswer)) {
							rbChecked = false;
						} else {
							rbChecked = true;
						}
					}
					previousAnswer = (String) rbAnswer4.getText();
					break;
				}
				
				if (rbChecked) {
					//disable the radio buttons after the click
					toogleRadioButtons(false);
					
					//validate the user's answer
					if (userAnswer.equals(correctAnswer)) {
						answeredCorrectly();
					} else {
						incorrectAnswer();
					}
				} 
			}
		});
		
		playTimerCounter = new CountDownTimer(playTimer, 1000) {
		     public void onTick(long millisUntilFinished) {
		    	 playTimerSeconds = (int) (millisUntilFinished / 1000);
		    	 if (playTimerSeconds <= 9) {
		    		 tvTimer.setText("0" + playTimerSeconds);
		    		 tvTimer.setTextColor(R.style.redfont);
		    	 } else {
		    		 tvTimer.setText("" + playTimerSeconds);
		    	 }
		    	 timeUp = false;
		     }
	
		     public void onFinish() {
		         tvTimer.setText("00");
		         timeUp = true;
		         incorrectAnswer();
		     }
		}.start();				
	}
	
	private void toogleRadioButtons(Boolean bStatus) {
		rbAnswer1.setEnabled(bStatus);
		rbAnswer2.setEnabled(bStatus);
		rbAnswer3.setEnabled(bStatus);
		rbAnswer4.setEnabled(bStatus);		
	}
	
	private void answeredCorrectly() {
		
		playTimerCounter.cancel();	//stop the timer
		
		//update the progressbar
		progressHandler.post(new Runnable() {			
			@Override
			public void run() {
				pbProgress.setProgress(currentQuestionID);				
			}
		});
		
		//update the score
		updateCurrentScore();
		
		if (currentQuestionID == 9) {
			//group completed
			currentQuestionID = 0;
						
			//next group
			groupID++;
			tvLevel.setText("Level: " + groupID);
			
			//save the game state
			saveGameState();
			
			//reset game defaults
			resetGameDefaults();
			
			//get next set of questions
			getAllQnAs(groupID);
			
		} else {
			//get the next question
			Random rand = new Random();
			int randomAcknowlege = rand.nextInt(5);
			Toast.makeText(Play.this, correctAcknowledge[randomAcknowlege], Toast.LENGTH_SHORT).show();
			currentQuestionID++;
						
		    Thread sleeper = new Thread() { 
		         public void run() { 
		        	 //sleep a little, well not a little for a whole 3 seconds
		        	 try {
						sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						runOnUiThread(new Runnable(){
							public void run() {
								rbAnswer1.setChecked(false);
								rbAnswer2.setChecked(false);
								rbAnswer3.setChecked(false);
								rbAnswer4.setChecked(false);								
								setQnA(currentQuestionID);
								playTimerCounter.start();
							}
						});
					}
		         } 
		    }; 		
			sleeper.start();						
		}
	}
	
	private void incorrectAnswer() {
		playTimerCounter.cancel();	//stop the timer
		//update the score
		score = score - scorePenalty;
		if (score < 0) {
			score = 0;
		}
		tvScore.setText("Score: " + score);
		currentQuestionID = 0;
		if (timeUp) {
			Toast.makeText(Play.this, "Time is up!", Toast.LENGTH_LONG).show();			
		} else {
			Toast.makeText(Play.this, "Incorrect Answer!", Toast.LENGTH_LONG).show();			
		}
		
	    Thread sleeper = new Thread() { 
	         public void run() { 
	        	 //sleep a little, well not a little for a whole 3 seconds
	        	 try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable(){
						public void run() {
							rbAnswer1.setChecked(false);
							rbAnswer2.setChecked(false);
							rbAnswer3.setChecked(false);
							rbAnswer4.setChecked(false);
							
							Intent openMainMenu = new Intent("com.example.tow.MAINMENU");
							startActivity(openMainMenu);
						}
					});
				}
	         } 
	    }; 		
		sleeper.start();
	}
	
	private void setGameDefaults() {		
		lifeline_time = true;
		lifeline_simplify = true;		
	}

	
	private void resetGameDefaults() {
		if (lifeline_simplify == false) {
			playMenu.findItem(R.id.action_simplify).setIcon(R.drawable.simplify_active);
		}
		if (lifeline_time == false) {
			playMenu.findItem(R.id.action_time_extender).setIcon(R.drawable.ic_action_time_active);
		}
		setGameDefaults();
	}
	
	
	private void resetPlayTimer() {
		switch (playDifficulty) {
		case 1:
			scoreWeight = 1;
			playTimerSeconds = 25;
			scorePenalty = 5;
			break;
		case 2:
			scoreWeight = 2;
			playTimerSeconds = 20;
			scorePenalty = 10;
			break;
		case 3:
			scoreWeight = 4;
			playTimerSeconds = 15;
			scorePenalty = 15;
			break;
		}		
	}
	
	private void updateCurrentScore() {
		Integer questionScore = scoreWeight * playTimerSeconds;
		score += questionScore;
		tvScore.setText("Score: " + score);
		tvLevel.setText("Level: " + groupID);
	}
		
	private void getAllQnAs(Integer groupID) {
		SQLiteDatabase db;
		db = openOrCreateDatabase(QuestionsTable.DATABASE_TOW, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		
		//see also - http://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder.html
		
		//Cursor cur = db.query(QuestionsTable.TABLE_QUESTIONS, null, null, null, null, null, null, "10");
		Cursor cur = db.query(QuestionsTable.TABLE_QUESTIONS, null, QuestionsTable.COLUMN_GROUP + " = " + groupID, null, null, null, null, "10");
		cur.moveToFirst();

		int iCount = 0;
		while (cur.isAfterLast() == false) {
			qnaArray[iCount][0] = cur.getString(1); 	// question
			qnaArray[iCount][1] = cur.getString(3); 	// option a
			qnaArray[iCount][2] = cur.getString(4); 	// option b
			qnaArray[iCount][3] = cur.getString(5); 	// option c
			qnaArray[iCount][4] = cur.getString(6); 	// option d
			qnaArray[iCount][5] = cur.getString(7); 	// option e
			
			iCount++;
			cur.moveToNext();
		}
		cur.close();				
		currentQuestionID = 0;
		setQnA(0); 	//set the first QnA
	}		
	
	private void setQnA(Integer question_id) {
		tvQuestion.setText(qnaArray[question_id][0]);
		rbAnswer1.setText(qnaArray[question_id][1]);
		rbAnswer2.setText(qnaArray[question_id][2]);
		rbAnswer3.setText(qnaArray[question_id][3]);
		rbAnswer4.setText(qnaArray[question_id][4]);
		correctAnswer = qnaArray[question_id][5];
		
		//enable the options buttons
		toogleRadioButtons(true);
	}
	
	private void initializeLayout() {
		tvUser = (TextView) findViewById(R.id.tvUser);
		tvScore = (TextView) findViewById(R.id.tvScore);
		tvLevel = (TextView) findViewById(R.id.tvLevel);
		
		tvQuestion = (TextView) findViewById(R.id.tvQuestion);
		tvTimer = (TextView) findViewById(R.id.tvTimer);
		rgAnswers = (RadioGroup) findViewById(R.id.rgAnswers);
		rbAnswer1 = (RadioButton) findViewById(R.id.rbAnswer1);
		rbAnswer2 = (RadioButton) findViewById(R.id.rbAnswer2);
		rbAnswer3 = (RadioButton) findViewById(R.id.rbAnswer3);
		rbAnswer4 = (RadioButton) findViewById(R.id.rbAnswer4);
		
		pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_menu, menu);
		playMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.main_menu) {			
			//then close the activity and open main menu
			Intent openMainMenu = new Intent("com.example.tow.MAINMENU");
			startActivity(openMainMenu);
			return true;
		}
		if (id == R.id.action_simplify) {
			if (lifeline_simplify == true) {
				//simplify the game
				
				playMenu.findItem(R.id.action_simplify).setIcon(R.drawable.simplify);
				
				lifeline_simplify = false;
			}			
		}
		if (id == R.id.action_time_extender) {
			if (lifeline_time == true) {
				//extend the time
				playTimerCounter.cancel();
				playTimer = playTimer * 3;
				playTimerCounter.start();
				playMenu.findItem(R.id.action_time_extender).setIcon(R.drawable.ic_action_time);
				lifeline_time = false;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void saveGameState() {		
    	//OPEN THE DATABASE
    	TowDatabaseHelper dbHelper = new TowDatabaseHelper(Play.this);
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
        //create content values
        values.put(ScoresTable.COLUMN_ID, userID);
        values.put(ScoresTable.COLUMN_SCORE, score);
        values.put(ScoresTable.COLUMN_GROUPID, groupID);
        
        //insert the data into the database using a prepared statement
        db.insertWithOnConflict(ScoresTable.TABLE_SCORES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        //CLOSE THE DATABASE
        db.close();
        dbHelper.close();				
	}

	@Override
	protected void onPause() {
		super.onPause();
		playTimerCounter.cancel();
		//-- save the game state
		saveGameState();
		finish();
	}		
}