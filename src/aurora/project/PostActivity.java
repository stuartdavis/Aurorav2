package aurora.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PostActivity extends Activity{
	
	private RelativeLayout typescreen;
	private String posttext;
	private EditText entry;
	private Button poster;
	private Button cancel;
	private Button morebutton;
	private RelativeLayout picselectscreen;
	private Button firstpost;
	private GridView gridview;
	private ImageView selectedpic;
	private RelativeLayout confirmscreen;
	private TextView confirmedtext;
	private ImageView confirmedpic;
	private Button finalcancel;
	private Button confirm;
	private MoodAdapter moodAdapter;
	private int selectedPosition;
	private ProgressDialog dialog;
	
	public void onCreate(Bundle savedInstanceState){
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity_layout);
		Log.e("log_tag", "STARTING POSTACTIVITY ONCREATE");
		typescreen = (RelativeLayout) findViewById(R.id.typescreen);
		picselectscreen = (RelativeLayout) findViewById(R.id.picselectscreen);
		confirmscreen = (RelativeLayout) findViewById(R.id.confirmscreen);
		morebutton = (Button) findViewById(R.id.morebutton);
		cancel = (Button) findViewById(R.id.cancel);
		entry = (EditText) findViewById(R.id.entry);
        poster = (Button) findViewById(R.id.next);
        firstpost = (Button) findViewById(R.id.firstpost);
        gridview = (GridView) findViewById(R.id.gridview02);
        selectedpic = (ImageView) findViewById(R.id.selectedpic);
        confirmedtext = (TextView) findViewById(R.id.confirmedtext);
        confirmedpic = (ImageView) findViewById(R.id.confirmedpic);
        finalcancel = (Button) findViewById(R.id.finalcancel);
        confirm = (Button) findViewById(R.id.confirm);
        
		typescreen.setVisibility(8);
		confirmscreen.setVisibility(8);
        //TODO
        moodAdapter = new MoodAdapter(this);
		gridview.setAdapter(moodAdapter);
		
        //TODO
		morebutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		moodAdapter.populate();
        		selectedpic.setImageResource(-1);
        	}
        });
        
    	firstpost.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		if(selectedpic.getDrawable()!=null) {
	        		confirmedpic.setImageDrawable(selectedpic.getDrawable());
	        		selectedpic.setImageResource(-1);
	        		typescreen.setVisibility(0);
	        		picselectscreen.setVisibility(4);
	        		entry.setText("");
        		}
        		else {
        			Toast.makeText(PostActivity.this, "A picture must be selected in order to post.", Toast.LENGTH_LONG).show();
        		}
        	}
        });
    	
    	//TODO
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	selectedpic.setImageDrawable( ( (ImageView) v).getDrawable() );
	        	selectedPosition = position;
	        }
	    });

        poster.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	posttext = entry.getText().toString();
            	confirmedtext.setText(posttext);
            	entry.setText("");
            	typescreen.setVisibility(4);
        		confirmscreen.setVisibility(0);
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                entry.setText("");
                selectedpic.setImageResource(-1);
                typescreen.setVisibility(4);
                picselectscreen.setVisibility(0);
            }
        });
    
    	finalcancel.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		picselectscreen.setVisibility(0);
        		confirmscreen.setVisibility(4);
            	selectedpic.setImageResource(-1);
        	}
        });
    	
    	
    	//TODO
    	confirm.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dialog = ProgressDialog.show(PostActivity.this, "", "Posting. Please wait...", true);
        		Aurora.addTask(new PostMoodStatusTask().execute());    
        			         	
        	}
        });
		} catch (Exception e) {
			Log.e("log_tag", "POSTACTIVITY ERROR: " + e.toString());
		}
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	//TODO
	private class PostMoodStatusTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String result="";
			//the mood status to add
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			nameValuePairs.add(new BasicNameValuePair("photo_id", String.valueOf(moodAdapter.getImageId(selectedPosition))));
			nameValuePairs.add(new BasicNameValuePair("notes", confirmedtext.getText().toString()));
			InputStream is = null;
			
			//http post
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/postMoodStatus.php");
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpclient.execute(httppost);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("log_tag", "Error in http connection "+e.toString());
			}
			
			//convert response to string
			try{
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        StringBuilder sb = new StringBuilder();
			        String line = null;
			        while ((line = reader.readLine()) != null) {
			                sb.append(line);
			        }
			        is.close();
			 
			        result=sb.toString();
			}catch(Exception e){
			        Log.e("log_tag", "Error converting result "+e.toString());
			}
			
			return result;
		}
		
		@Override
		public void onPostExecute(String result) {
			dialog.dismiss();
			picselectscreen.setVisibility(0);
    		confirmscreen.setVisibility(4);
        	selectedpic.setImageResource(-1);
        	moodAdapter.populate();
        	
        	if(result.equals("OK"))
        		Toast.makeText(PostActivity.this, "Mood posted successfully", Toast.LENGTH_SHORT).show();
        	else
        		Toast.makeText(PostActivity.this, "Error posting mood", Toast.LENGTH_SHORT).show();
        			
		}
    }
	
	public void myOnResume(){
		typescreen.setVisibility(4);
		confirmscreen.setVisibility(4);
		picselectscreen.setVisibility(0);
		selectedpic.setImageResource(-1);
		moodAdapter.populate();
	}
	
	//TODO
	public void onPause() {
		super.onPause();
		Aurora.killTasks();
	}
}
