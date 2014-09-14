package com.example.tow;

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.tow.database.*;

public class Play extends Activity {

	TextView tvQuestion, tvTimer;
	RadioGroup rgAnswers;
	RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
	
	String[][] qnaArray = new String[10][6];
	Integer groupID, playTimer, currentQuestionID;
	CountDownTimer playTimerCounter;
	String correctAnswer, previousAnswer;
	String[] correctAcknowledge = {"Well Done!", "Correct!", "Yessss!", "Yupppp!", "Absolutely!"};
	Boolean timeUp = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_layout);
		
		initializeLayout();
		
		//initialize the time per question
		playTimer = 15 * 1000;
		
		//initialize other variables
		previousAnswer = "put_application_hash_key_here";
		
		//TODO: get the correct Group ID
		groupID = 1;
		
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
		    	 Integer timeLeft = (int) (millisUntilFinished / 1000);
		    	 if (timeLeft <= 9) {
		    		 tvTimer.setText("0" + timeLeft);
		    		 tvTimer.setTextColor(R.style.redfont);
		    	 } else {
		    		 tvTimer.setText("" + timeLeft);
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
		
		if (currentQuestionID == 9) {
			//group completed
			currentQuestionID = 0;
						
			//next group
			groupID++;
						
			getAllQnAs(groupID);
			
		} else {
			//get the next question
			Random rand = new Random();
			int randomAcknowlege = rand.nextInt(5);
			Toast.makeText(Play.this, currentQuestionID + ")" + correctAcknowledge[randomAcknowlege], Toast.LENGTH_SHORT).show();
			currentQuestionID++;
						
		    Thread sleeper = new Thread() { 
		         public void run() { 
		        	 //sleep a little, well not a little for a whole 3 seconds
		        	 try {
						sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
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
		tvQuestion = (TextView) findViewById(R.id.tvQuestion);
		tvTimer = (TextView) findViewById(R.id.tvTimer);
		rgAnswers = (RadioGroup) findViewById(R.id.rgAnswers);
		rbAnswer1 = (RadioButton) findViewById(R.id.rbAnswer1);
		rbAnswer2 = (RadioButton) findViewById(R.id.rbAnswer2);
		rbAnswer3 = (RadioButton) findViewById(R.id.rbAnswer3);
		rbAnswer4 = (RadioButton) findViewById(R.id.rbAnswer4);
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
			
			//TODO: quit game and update stats
			
			//then close the activity and open main menu
			Intent openMainMenu = new Intent("com.example.tow.MAINMENU");
			startActivity(openMainMenu);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}