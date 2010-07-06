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
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TalkActivity extends Activity {

	private TextView talkdescription;
	private Button conversationbutton;
	private Button auroraimbutton;
	private ListView conversationslist;
	private ImageView conversationpic;
	private TextView conversationstarter;
	private ListView conversation;
	private Button backbutton;
	private Button replybutton;
	private RelativeLayout replyscreen;
	private Button sendcomment;
	private Button cancelcomment;
	private EditText commenteditor;
	private ListView auroraimlist;
	private Button postbutton;
	private Boolean onauroraim;
	
	ArrayAdapter<Spannable> imlistadapter;
	ConversationAdapter conversationAdapter;
	ArrayAdapter<Spannable> conversationListAdapter;
	private int selectedConversation;
	
	//TODO
	ProgressDialog dialog;
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.talk_activity_layout);
    	
    	talkdescription = (TextView) findViewById(R.id.talkdescription);
    	conversationbutton = (Button) findViewById(R.id.conversationsbutton);
    	auroraimbutton = (Button) findViewById(R.id.auroraimbutton);
    	conversationslist = (ListView) findViewById(R.id.conversationslist);
    	conversationpic = (ImageView) findViewById(R.id.conversationpic);
    	conversationstarter = (TextView) findViewById(R.id.conversationstarter);
    	conversation = (ListView) findViewById(R.id.conversation);
    	backbutton = (Button) findViewById(R.id.backbutton);
    	replybutton = (Button) findViewById(R.id.replybutton);
    	replyscreen = (RelativeLayout) findViewById(R.id.replyscreen);
    	cancelcomment = (Button) findViewById(R.id.cancelcomment);
    	sendcomment = (Button) findViewById(R.id.sendcomment);
    	commenteditor = (EditText) findViewById(R.id.commenteditor);
    	auroraimlist = (ListView) findViewById(R.id.auroraimlist);
    	postbutton = (Button) findViewById(R.id.postbutton);
    	
    	//TODO ims
    	imlistadapter = new ArrayAdapter<Spannable>(this, R.layout.list_item, new ArrayList<Spannable>());
	    auroraimlist.setAdapter(imlistadapter);
	    auroraimlist.setTextFilterEnabled(false);

	    //TODO conversations
	    conversationAdapter = new ConversationAdapter(this);
	    conversationslist.setAdapter(conversationAdapter);
	    conversationslist.setTextFilterEnabled(true);
	    
	    conversationListAdapter = new ArrayAdapter<Spannable>(TalkActivity.this, R.layout.list_item,new ArrayList<Spannable>());
    	conversation.setAdapter(conversationListAdapter);
    	
    	talkdescription.setText("There are two ways to talk with your group members in Aurora:" +
    			"\n(1) Have a conversation with them about a status update, or " +
    			"\n(2) Chat with everyone in Aurora IM");
	    
	    conversationslist.setVisibility(4);
	    conversation.setVisibility(4);
	    backbutton.setVisibility(4);
	    replybutton.setVisibility(4);
	    replyscreen.setVisibility(4);
	    auroraimlist.setVisibility(4);
	    postbutton.setVisibility(4);
	    conversationpic.setVisibility(4);
    	conversationstarter.setVisibility(4);
    	
    	conversationbutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		onauroraim = false;
            	talkdescription.setVisibility(4);
            	auroraimlist.setVisibility(4);
            	conversationbutton.setVisibility(4);
            	auroraimbutton.setVisibility(0);
            	conversationslist.setVisibility(0);
            	postbutton.setVisibility(4);
            	//TODO populate conversations
            	conversationAdapter.populate();
        	}
        });
    	
    	
    	auroraimbutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		onauroraim = true;
            	talkdescription.setVisibility(4);
            	conversationslist.setVisibility(4);
            	auroraimbutton.setVisibility(4);
            	conversationbutton.setVisibility(0);
            	auroraimlist.setVisibility(0);
            	postbutton.setVisibility(0);
            	
            	//TODO populate ims
            	imlistadapter.clear();
            	new PopulateIMs().execute();
        	}
        });
    	
    	conversationslist.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	conversationslist.setVisibility(4);
	        	conversationbutton.setVisibility(4);
	        	auroraimbutton.setVisibility(4);
	        	conversation.setVisibility(0);
	        	backbutton.setVisibility(0);
	        	replybutton.setVisibility(0);
	        	conversationpic.setVisibility(0);
	        	conversationstarter.setVisibility(0);

	        	//TODO view specific conversation
	        	selectedConversation = position;
	        	ImageView v = (ImageView) view.findViewById(R.id.icon);
	        	conversationpic.setImageDrawable(v.getDrawable());
	        	conversationstarter.setText(conversationAdapter.getConversationNote(position));
	        	conversationListAdapter.clear();
	        	new PopulateConversationMessages().execute(conversationAdapter.getConversationId(position));
	        }
	      });
    	
    	backbutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		conversationslist.setVisibility(0);
	        	auroraimbutton.setVisibility(0);
	        	conversationpic.setVisibility(4);
	        	conversationstarter.setVisibility(4);
	        	conversation.setVisibility(4);
	        	backbutton.setVisibility(4);
	        	replybutton.setVisibility(4);
        	}
        });
    	
    	replybutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		conversation.setVisibility(4);
	        	backbutton.setVisibility(4);
	        	replybutton.setVisibility(4);
	        	conversationpic.setVisibility(4);
	        	conversationstarter.setVisibility(4);
	        	replyscreen.setVisibility(0);
        	}
        });
    	
    	postbutton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		auroraimbutton.setVisibility(4);
	        	postbutton.setVisibility(4);
	        	conversationbutton.setVisibility(4);
	        	auroraimlist.setVisibility(4);
	        	replyscreen.setVisibility(0);
        	}
        });
    	
    	//TODO
    	sendcomment.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			if(!commenteditor.getText().toString().trim().equals("")) { //a valid comment
    				dialog = ProgressDialog.show(TalkActivity.this, "", "Sending. Please wait...", true);
    				if (onauroraim) {	
    					new PostIM().execute(commenteditor.getText().toString());
    				}
    				else {
    					new PostConversationMessage().execute(
    							conversationAdapter.getConversationId(selectedConversation),
    							conversationAdapter.getConversationFriendId(selectedConversation),
    							commenteditor.getText().toString());
    				}
    			} else { //not a valid comment
    				Toast.makeText(TalkActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
    			}
    			
    		}
    	});
    	
       	cancelcomment.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		if (onauroraim) {
    	        	postbutton.setVisibility(0);
    	        	conversationbutton.setVisibility(0);
    	        	auroraimlist.setVisibility(0);
    	        	replyscreen.setVisibility(4);
    	        	auroraimbutton.performClick();
        		}
        		else {
        			conversation.setVisibility(0);
	        		backbutton.setVisibility(0);
	        		conversationpic.setVisibility(0);
		        	conversationstarter.setVisibility(0);
	        		replybutton.setVisibility(0);
	        		replyscreen.setVisibility(4);
        		}
        		commenteditor.setText("");
        	}
        }); 	
    }
    
    public void myOnResume(){
    	conversationslist.setVisibility(4);
	    conversation.setVisibility(4);
	    backbutton.setVisibility(4);
	    replybutton.setVisibility(4);
	    replyscreen.setVisibility(4);
	    auroraimlist.setVisibility(4);
	    postbutton.setVisibility(4);
	    conversationpic.setVisibility(4);
    	conversationstarter.setVisibility(4);
    	talkdescription.setVisibility(0);
    	conversationbutton.setVisibility(0);
    	auroraimbutton.setVisibility(0);
    }
    
    private class PopulateIMs extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			String result = "";
			JSONArray jArray = null;
			InputStream is = null;	
			
			//the user's id
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			
			//http post
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/getIMsFromGroup.php");
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpclient.execute(httppost);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("POPULATE IMS", "Error in http connection "+e.toString());
			}
			
			//get jason encoded response
			try{
				
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;
			        
			        while ((line = reader.readLine()) != null) {
			        	result += line;		        	
			        }
			        is.close();
			        jArray = new JSONArray(result);
			}catch(Exception e){
			        Log.e("POPULATE IMS", "Error converting result "+e.toString());
			}
			
			return jArray;
		}
		
		@Override
    	protected void onPostExecute(JSONArray jArray){ 		
    		try{	
    			for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                TextView tv = new TextView(TalkActivity.this);
	                String usnm = json_data.getString("username");
	                tv.setText(usnm+": " + json_data.getString("message"), TextView.BufferType.SPANNABLE);
	                Spannable str = (Spannable) tv.getText();
	                if (Aurora.USERNAME == usnm)
	                	str.setSpan(new ForegroundColorSpan(0xFF0000FF), 0, usnm.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                else	
	                	str.setSpan(new ForegroundColorSpan(0xFFFFFF00), 0, usnm.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                imlistadapter.add(str);
    			}
            } catch(Exception e) {
                Log.e("POPULATE IMS", "Error adding ims "+e.toString());              
            }  		
    	}
    }
    
    
    //TODO
    private class PostIM extends AsyncTask<Object, Void, String> {
		@Override
		protected String doInBackground(Object... params) {
			String message = (String) params[0];
			String result = "";
			InputStream is = null;
				
			//the IM to post
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			nameValuePairs.add(new BasicNameValuePair("message", message));
			
			//http post
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/postIM.php");
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpclient.execute(httppost);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("POST IMS", "Error in http connection "+e.toString());
			}
			
			//check response
			try{	
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;        
			        while ((line = reader.readLine()) != null) {
			        	result += line;		        	
			        }
			        is.close();
			}catch(Exception e){
			        Log.e("POST IMS", "Error converting result "+e.toString());
			}
			
			return result;
		}
	
		@Override
		public void onPostExecute(String result) {
			dialog.dismiss();
			auroraimbutton.setVisibility(0);
			postbutton.setVisibility(0);
			conversationbutton.setVisibility(0);
			auroraimlist.setVisibility(0);
			replyscreen.setVisibility(4);
			commenteditor.setText("");
			auroraimbutton.performClick();
			
        	if(result.equals("OK"))
        		Toast.makeText(TalkActivity.this, "IM sent successfully", Toast.LENGTH_SHORT).show();
        	else
        		Toast.makeText(TalkActivity.this, "Error sending IM", Toast.LENGTH_SHORT).show();
        			
		}
    }
    
    private class PopulateConversationMessages extends AsyncTask<Object, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Object... params) {
			int conversationId = (Integer) params[0];
			String result = "";
			InputStream is = null;
			JSONArray jArray = null;

			//the IM to post
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("conversation_id", String.valueOf(conversationId)));

			//http post
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/getMessagesForConversation.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch(Exception e){
				Log.e("POPULATE CONVERSATION MESSAGES", "Error in http connection "+e.toString());
			}

			//convert jason encoded response
			try{

				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				String line = null;

				while ((line = reader.readLine()) != null) {
					result += line;		        	
				}
				is.close();
				jArray = new JSONArray(result);
			}catch(Exception e){
				Log.e("POPULATE IMS", "Error converting result "+e.toString());
			}

			return jArray;
		}
		
		@Override
    	protected void onPostExecute(JSONArray jArray){ 		
    		try{	
    			for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                TextView tv = new TextView(TalkActivity.this);
	                String usnm = json_data.getString("username");
	                String crtd = json_data.getString("created");
	                tv.setText(crtd + " " + usnm + ": " + json_data.getString("message"), TextView.BufferType.SPANNABLE);
	                Spannable str = (Spannable) tv.getText();
	                if (Aurora.USERNAME == usnm)
		                str.setSpan(new ForegroundColorSpan(0xFF0000FF), crtd.length(), crtd.length()+usnm.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                else
	                	str.setSpan(new ForegroundColorSpan(0xFFFFFF00), crtd.length(), crtd.length()+usnm.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                conversationListAdapter.add(str);
    			}
            } catch(Exception e) {
                Log.e("POPULATE IMS", "Error adding ims "+e.toString());              
            }  		
    	}
    }
    
    //TODO
    private class PostConversationMessage extends AsyncTask<Object, Void, String> {
		@Override
		protected String doInBackground(Object... params) {
			int conversationId = (Integer) params[0];
			int friendId = (Integer) params[1];
			String message = (String) params[2];
			String result = "";
			InputStream is = null;
				
			//the message to post
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("conversation_id", String.valueOf(conversationId)));
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			nameValuePairs.add(new BasicNameValuePair("friend_id", String.valueOf(friendId)));
			nameValuePairs.add(new BasicNameValuePair("message", message));
			
			//http post
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/postMessageToConversation.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch(Exception e){
				Log.e("POST CONVERSATION MESSAGE", "Error in http connection "+e.toString());
			}
			
			//check response
			try{	
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				String line = null;        
				while ((line = reader.readLine()) != null) {
					result += line;		        	
				}
				is.close();
			}catch(Exception e){
				Log.e("POST CONVERSATION MESSAGE", "Error converting result "+e.toString());
			}
			
			return result;
		}
		
		@Override
		public void onPostExecute(String result) {
			dialog.dismiss();
			conversation.setVisibility(0);
			conversationstarter.setVisibility(0);
			conversationpic.setVisibility(0);
			backbutton.setVisibility(0);
			replybutton.setVisibility(0);
			replyscreen.setVisibility(4);
			commenteditor.setText("");
			
			//	repopulate messages
			conversationListAdapter.clear();
			new PopulateConversationMessages().execute(conversationAdapter.getConversationId(selectedConversation));
			
        	if(result.equals("OKOK"))
        		Toast.makeText(TalkActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
        	else
        		Toast.makeText(TalkActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
        			
		}
    }
}