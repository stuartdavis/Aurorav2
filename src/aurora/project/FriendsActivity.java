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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity{
	
	private RelativeLayout postsviewscreen;
	private GridView gridview;
	private RecentMoodsAdapter recentMoodsAdapter;
	private Button viewfriends;
	private RelativeLayout friendslistscreen;
	private ListView friendslist;
	private Button viewrecentposts;
	private RelativeLayout friendspostsscreen;
	private GridView friendsgridview;
	private RecentMoodsAdapter friendsadapter;
	private Button backtoallfriends;
	private RelativeLayout postscreen;
	private ImageView postimage;
	private TextView post;
	private TextView poster;
	private TextView time;
	private Button backbutton;
	private Button commentbutton;
	private RelativeLayout commentscreen;
	private EditText comment;
	private Button sendcomment;
	private Button cancel;
	
	private Boolean inlistview;
	
	//TODO
	private ArrayAdapter<String> listadapter;
	private ArrayList<Integer> friendIds;
	private int selectedPosition;
	private ProgressDialog dialog;
	
	public void onCreate(Bundle savedInstanceState) {
		Log.e("log_tag", "STARTING FRIENDSACTIVITY ONCREATE");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.friends_activity_layout);
	    
	    postsviewscreen = (RelativeLayout) findViewById(R.id.postsviewscreen);
	    gridview = (GridView) findViewById(R.id.gridview);
	    viewfriends = (Button) findViewById(R.id.viewfriends);
	    friendslistscreen = (RelativeLayout) findViewById(R.id.friendslistscreen);
	    friendslist = (ListView) findViewById(R.id.friendslist);
	    viewrecentposts = (Button) findViewById(R.id.viewrecentposts);
	    friendspostsscreen = (RelativeLayout) findViewById(R.id.friendspostsscreen);
	    friendsgridview = (GridView) findViewById(R.id.friendsgridview);
	    backtoallfriends = (Button) findViewById(R.id.backtoallfriends);
	    postscreen = (RelativeLayout) findViewById(R.id.postscreen);
	    postimage = (ImageView) findViewById(R.id.postimage);
	    post = (TextView) findViewById(R.id.post);
	    poster = (TextView) findViewById(R.id.poster);
	    time = (TextView) findViewById(R.id.time);
	    backbutton = (Button) findViewById(R.id.backbutton);
	    commentbutton = (Button) findViewById(R.id.commentbutton);
	    commentscreen = (RelativeLayout) findViewById(R.id.commentscreen);
	    comment = (EditText) findViewById(R.id.comment);
	    sendcomment = (Button) findViewById(R.id.sendcomment);
	    cancel = (Button) findViewById(R.id.cancel);
	    
	    inlistview = false;
	    
	    recentMoodsAdapter = new RecentMoodsAdapter(this);
	    gridview.setAdapter(recentMoodsAdapter);
	    
	    friendsadapter = new RecentMoodsAdapter(this);
	    friendsgridview.setAdapter(friendsadapter);
	    
	    //TODO
	    //friendNames = new ArrayList<String>();
	    friendIds = new ArrayList<Integer>();
	    listadapter = new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<String>());
	    
	    friendslist.setAdapter(listadapter);
	    friendslist.setTextFilterEnabled(true);

	    postscreen.setVisibility(4);
	    commentscreen.setVisibility(4);
	    friendslistscreen.setVisibility(4);
	    friendspostsscreen.setVisibility(4);
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	ImageView clicked = (ImageView) v;
	        	if (clicked.getDrawable()!=null)
	        	postimage.setImageDrawable(clicked.getDrawable());
	        	//TODO
	        	post.setText(recentMoodsAdapter.getStatusNote(position));
	        	poster.setText(recentMoodsAdapter.getStatusUsername(position));
	        	time.setText(recentMoodsAdapter.getStatusTime(position));
	        	selectedPosition = position;
	        	postsviewscreen.setVisibility(4);
	        	postscreen.setVisibility(0);
	        }
	    });
	    
	    viewfriends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	postsviewscreen.setVisibility(4);
            	friendslistscreen.setVisibility(0);
            	inlistview = true;
            	//TODO
            	if(listadapter.isEmpty()) {
            		listadapter.clear();
            		Aurora.addTask(new PopulateFriendsList().execute());
            	}
            }
        });
	    
	    friendslist.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        		friendspostsscreen.setVisibility(0);
	        		friendslistscreen.setVisibility(4);
	        		friendsadapter.populate(friendIds.get(position));

	        		//TODO
	        }
	      });
	    
	    viewrecentposts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	postsviewscreen.setVisibility(0);
            	friendslistscreen.setVisibility(4);
            	inlistview = false;
            }
        });
	    
	    friendsgridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	ImageView clicked = (ImageView) v;
	        	postimage.setImageDrawable(clicked.getDrawable());
	        	//TODO
	        	post.setText(friendsadapter.getStatusNote(position));
	        	poster.setText(friendsadapter.getStatusUsername(position));
	        	time.setText(friendsadapter.getStatusTime(position));
        		selectedPosition = position;
        		
	        	friendspostsscreen.setVisibility(4);
	        	postscreen.setVisibility(0);
	        }
	    });

    	backtoallfriends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	friendspostsscreen.setVisibility(4);
            	friendslistscreen.setVisibility(0);
            }
        });
	    
    	backbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (inlistview)
            		friendspostsscreen.setVisibility(0);
            	else
            		postsviewscreen.setVisibility(0);
            	postscreen.setVisibility(4);            	
            }
        });
	    
    	commentbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	postscreen.setVisibility(4);
            	commentscreen.setVisibility(0);
            }
        });
    	
    	//TODO
    	sendcomment.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			if(!comment.getText().toString().trim().equals("")) { //a valid comment
    				dialog = ProgressDialog.show(FriendsActivity.this, "", "Sending. Please wait...", true);
    				if (inlistview) {	
    					new PostStatusComment().execute(friendsadapter.getStatusId(selectedPosition),
    							friendsadapter.getStatusFriendId(selectedPosition));
    				} else {
    					new PostStatusComment().execute(recentMoodsAdapter.getStatusId(selectedPosition),
    							recentMoodsAdapter.getStatusFriendId(selectedPosition));
    				}
    			} else { //not a valid comment
    				Toast.makeText(FriendsActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
    			}
    		}
    	});
    	
    	cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	commentscreen.setVisibility(4);
            	postscreen.setVisibility(0);
            	comment.setText("");
            }
        });
    	
	}
	
	public void myOnResume(){
		postscreen.setVisibility(4);
	    commentscreen.setVisibility(4);
	    friendslistscreen.setVisibility(4);
	    friendspostsscreen.setVisibility(4);
	    postsviewscreen.setVisibility(0);
	    
	    recentMoodsAdapter.populate(0);
	}
	
	//TODO
	@Override
	public void onPause() {
		super.onPause();
		Aurora.killTasks();
	}
	
	//TODO
	private class PopulateFriendsList extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			String result = "";
			friendIds.clear();
					
			//the user's id
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			
			InputStream is = null;
			JSONArray jArray = null;
			
			//http post
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/getUsersInGroup.php");
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpclient.execute(httppost);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("FRIENDS ACTIVITY", "Error in http connection "+e.toString());
			}
			
			//convert jason encoded response to LinkedList
			try{
				
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;
			        
			        while ((line = reader.readLine()) != null) {
			        	result += line;		        	
			        }
			        is.close();
			}catch(Exception e){
			        Log.e("FRIENDS ACTIVITY", "Error converting result "+e.toString());
			}
			
			try {
				jArray = new JSONArray(result);
			} catch(Exception e){
				Log.e("FRIENDS ACTIVITY", "Error parsing data "+e.toString());
			}
			
			return jArray;
		}
		
		@Override
    	protected void onPostExecute(JSONArray jArray){		
    		
    		try{	
    			 for(int i=0;i<jArray.length();i++){
 	                JSONObject json_data = jArray.getJSONObject(i);
 	                friendIds.add(json_data.getInt("id"));
 	                listadapter.add(json_data.getString("username"));
    			 }
            } catch(Exception e) {
                Log.e("FRIENDS ACTIVITY", "Error in downloading image "+e.toString());
                
            }
    		
    	}
    }
	
	//TODO
	private class PostStatusComment extends AsyncTask<Object, Void, String> {
		@Override
		protected String doInBackground(Object... params) {
			String result = "";		
			int statusId = (Integer) params[0];
			int friendId = (Integer) params[1];
			
			//query information
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			nameValuePairs.add(new BasicNameValuePair("status_id", String.valueOf(statusId)));
			nameValuePairs.add(new BasicNameValuePair("friend_id", String.valueOf(friendId)));		
			nameValuePairs.add(new BasicNameValuePair("message", comment.getText().toString()));
			
			InputStream is = null;
			
			//http post
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/postStatusComment.php");
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpclient.execute(httppost);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("FRIENDS ACTIVITY", "Error in http connection "+e.toString());
			}
			
			//convert jason encoded response to Arrays
			try{
				
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;
			        
			        while ((line = reader.readLine()) != null) {
			        	result += line;	        	
			        }
			        is.close();
			}catch(Exception e){
			        Log.e("FRIENDS ACTIVITY", "Error converting result "+e.toString());
			}
	        
			return result;
		}
		
		@Override
		public void onPostExecute(String result) {
			dialog.dismiss();
			commentscreen.setVisibility(4);
			if(inlistview)
				friendspostsscreen.setVisibility(0);
			else
				postsviewscreen.setVisibility(0);
			comment.setText("");
        	if(result.equals("OKOK"))
        		Toast.makeText(FriendsActivity.this, "Comment posted successfully", Toast.LENGTH_SHORT).show();
        	else if(result.equals("self"))
        		Toast.makeText(FriendsActivity.this, "No self comments!", Toast.LENGTH_SHORT).show();
        	else
        		Toast.makeText(FriendsActivity.this, "Error posting comment", Toast.LENGTH_SHORT).show();
        			
		}
    }
}
