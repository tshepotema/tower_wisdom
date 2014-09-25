package com.example.tow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tow.InstallAppService.HttpAsyncTask;
import com.example.tow.database.QuestionsTable;
import com.example.tow.database.ScoresTable;
import com.example.tow.database.TowDatabaseHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Leaderboard extends Activity {
	ListView list;

	ArrayList<String> userName = new ArrayList<String>();
	ArrayList<String> userScore = new ArrayList<String>();
	ArrayList<String> userRanking = new ArrayList<String>();

	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leaderboard);

		// get the players on the leader board
		new HttpAsyncTask().execute("http://dealfinder.zetail.co.za/webservices/tow.php");

	}	
	    	
    public static String GET(String url){
        InputStream inputStream;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpPost post = new HttpPost(url);

            JSONObject jsonRequest = new JSONObject();
            JSONObject requestData = new JSONObject();
            requestData.put("db_ver", "1");
            JSONArray requestDataArray = new JSONArray();
            requestDataArray.put(requestData);
            
            jsonRequest.put("action", "getLeaderboard");
            jsonRequest.put("data", requestDataArray);
            
            StringEntity reqEntity = new StringEntity(jsonRequest.toString());
            reqEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(reqEntity);
            
            // make POST request to the url
            HttpResponse httpResponse = httpclient.execute(post);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Tshepo - Error connecting to remote resource";

        } catch (Exception e) {
        	e.printStackTrace();
            //Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        	inputStream.close();
        return result;

    }
    	
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(Leaderboard.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Tower of Wisdom Leaderboard");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute process the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("postExecute", "leaderboard starting to PostExecute");
            try {
            	            	
                Integer j = 0;
                JSONObject resultObj = new JSONObject(result);
                JSONArray response = resultObj.getJSONArray("response");
                //Log.d("postExecute", "leaderboard trying PostExecute on [" + response.length() + "] items");
                for (int i = 0; i < response.length(); i++) {
                    JSONObject leaderboardObj = response.getJSONObject(i);

                    //Integer userID = leaderboardObj.getInt("user_id");
                    String user = leaderboardObj.getString("username");
                    String score = leaderboardObj.getString("score");
                    j = i + 1;
                    String ranking = user + " is ranked # " + j;
                               
                    userName.add(user);
                    userScore.add(score);
                    userRanking.add(ranking);
                    
                    //Log.d("postExecute", "leaderboard postExecute --> user =  [" + userName + "] score = " + score);                                        
                } 
                
        		CustomList adapter = new CustomList(Leaderboard.this, userName, userScore, userRanking);
        		list = (ListView) findViewById(R.id.lvLeaderboard);
        		list.setAdapter(adapter);
        		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        			@Override
        			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        				Toast.makeText(Leaderboard.this, "User profile view not available", Toast.LENGTH_SHORT).show();
        			}
        		});
                
        		//close the progress dialog
        		mProgressDialog.dismiss();
                                    
            } catch (JSONException e) {
                e.printStackTrace();
            }                            
        }

    }
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
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
			return super.onOptionsItemSelected(item);
		case R.id.action_preferences:
			Intent showSettings = new Intent("com.example.tow.USERPROFILE");
			startActivity(showSettings);
			return true;
		case R.id.action_profile:
			Intent showUserDetails = new Intent("com.example.tow.PLAYERDETAILS");
			startActivity(showUserDetails);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
}