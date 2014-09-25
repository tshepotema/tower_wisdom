package com.example.tow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

import com.example.tow.database.ScoresTable;
import com.example.tow.database.TowDatabaseHelper;
import com.example.tow.database.QuestionsTable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class InstallAppService {
	private Context installServiceContext;

	public InstallAppService(Context installServiceContext) {
		this.installServiceContext = installServiceContext;
	}
	
	public void installAppDB() {
        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask(this.installServiceContext).execute("http://dealfinder.zetail.co.za/webservices/tow.php");
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
            
            jsonRequest.put("action", "installApp");
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

        private Context syncContext;
        
        public HttpAsyncTask(Context context) {
        	syncContext = context;
        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute process the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
            	
            	//OPEN THE DATABASE
            	TowDatabaseHelper dbHelper = new TowDatabaseHelper(this.syncContext);
            	SQLiteDatabase db = dbHelper.getWritableDatabase();
            	
            	//dbHelper.onCreate(db);	//TT. after dbg
            	
            	ContentValues values = new ContentValues();
            	            	
                JSONObject resultObj = new JSONObject(result);
                JSONArray response = resultObj.getJSONArray("response");
                //Log.d("postExecute", "trying PostExecute on [" + response.length() + "] items");
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dealObj = response.getJSONObject(i);

                    Integer questionID = dealObj.getInt("question_id");
                    String question = dealObj.getString("question");
                    Integer groupID = dealObj.getInt("group_id");
                    String optionA = dealObj.getString("a");
                    String optionB = dealObj.getString("b");
                    String optionC = dealObj.getString("c");
                    String optionD = dealObj.getString("d");
                    String optionE = dealObj.getString("e");

                    //create content values
                    values.put(QuestionsTable.COLUMN_ID, questionID);
                    values.put(QuestionsTable.COLUMN_QUESTION, question);
                    values.put(QuestionsTable.COLUMN_GROUP, groupID);
                    values.put(QuestionsTable.COLUMN_A, optionA);
                    values.put(QuestionsTable.COLUMN_B, optionB);
                    values.put(QuestionsTable.COLUMN_C, optionC);
                    values.put(QuestionsTable.COLUMN_D, optionD);
                    values.put(QuestionsTable.COLUMN_E, optionE);
                    
                    //insert the data into the database using a prepared statement
                    db.insertWithOnConflict(QuestionsTable.TABLE_QUESTIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    
                    //Log.d("postExecute", "postExecute --> question =  [" + question + "] option E = " + optionE);                                        
                }
                
                
            	ContentValues values_userdetails = new ContentValues();
            	
                JSONObject resultObjUserDetails = new JSONObject(result);
                JSONArray responseUserDetails = resultObjUserDetails.getJSONArray("user");
                //Log.d("postExecute", "userDetails PostExecute on [" + responseUserDetails.length() + "] items");
                for (int i = 0; i < responseUserDetails.length(); i++) {
                    JSONObject userObj = response.getJSONObject(i);

                    Integer userID = userObj.getInt("user_id");

                    //create content values
                    values_userdetails.put(ScoresTable.COLUMN_ID, userID);
                    values_userdetails.put(ScoresTable.COLUMN_SCORE, 0);
                    values_userdetails.put(ScoresTable.COLUMN_GROUPID, 1);
                    
                    //insert the data into the database using a prepared statement
                    db.insertWithOnConflict(ScoresTable.TABLE_SCORES, null, values_userdetails, SQLiteDatabase.CONFLICT_REPLACE);
                    
                }                
                
                //CLOSE THE DATABASE
                db.close();
                dbHelper.close();
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
			Intent playIntent = new Intent("com.example.tow.PLAY");
			this.syncContext.startActivity(playIntent);					
                        
        }
    }		

}
