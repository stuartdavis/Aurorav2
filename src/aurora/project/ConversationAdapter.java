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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter {
    private Context context; 
    private ArrayList<Conversation> conversations;

    private class Conversation {
    	int id;
    	int friendId;
    	String username;
    	Bitmap image;
    	String note;
    	//ArrayList<String> messages;
    	
    	public Conversation(int id, int friendId, String username, Bitmap image, String note) {
    		this.id = id;
    		this.friendId = friendId;
    		this.username = username;
    		this.image = image;
    		this.note = note;
    		//messages = new ArrayList<String>();
    	}
    	
    	/*public void addMessage(String message) {
    		messages.add(message);
    	}
    	
    	public void clearMessages() {
    		messages.clear();
    	}*/
    }
    
    public ConversationAdapter(Context c) {
        context = c;
        conversations = new ArrayList<Conversation>();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {  	 
    	View row;
        if (convertView == null) {
        	LayoutInflater inflater= LayoutInflater.from(context);
        	row = inflater.inflate(R.layout.conversation_item, null);
        } else {
            row = convertView;
        }
        if(conversations.get(position) != null) {
        	TextView label=(TextView)row.findViewById(R.id.label);
            label.setText("With " + conversations.get(position).username);
            
            ImageView icon=(ImageView)row.findViewById(R.id.icon); 
            icon.setImageBitmap(conversations.get(position).image);
        }
        
        return row;
    }
    
    public void populate() {
    	conversations.clear();
    	notifyDataSetChanged();
    	new PopulateConversations().execute();
    }
    
    private class ImageHelper {
    	Bitmap bitmap;
    	int position;
    	public ImageHelper(int position, Bitmap bitmap) {
    		this.position = position;
    		this.bitmap = bitmap;
    	}
    }
    
    public String getConversationNote(int position) {
    	return conversations.get(position).note;
    }
    public int getConversationId(int position) {
    	return conversations.get(position).id;
    }
    public int getConversationFriendId(int position) {
    	return conversations.get(position).friendId;
    }
    
    private class PopulateConversations extends AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			String result = "";
			ArrayList<String> imagePaths = new ArrayList<String>();
			InputStream is = null;
			
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(Aurora.USER_ID)));
			
			//http post
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response;
				HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/getConversationsForUser.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}catch(Exception e){
				Log.e("POPULATE CONVERSATIONS", "Error in http connection "+e.toString());
			}
			
			//convert jason encoded response
			try{			
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;
			        
			        while ((line = reader.readLine()) != null) {
			        	result += line;	        	
			        }
			        is.close();
			}catch(Exception e){
			        Log.e("POPULATE CONVERSATIONS", "Error converting result "+e.toString());
			}
			
			try {
			JSONArray jArray = new JSONArray(result);
	        for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                conversations.add(new Conversation(json_data.getInt("id"), json_data.getInt("friend_id"),
	                		json_data.getString("username"), null, json_data.getString("notes")));
	                imagePaths.add(json_data.getString("path").replace("\\", ""));
	        }
	        conversations.trimToSize();
			} catch(JSONException e){
    		        Log.e("POPULATE CONVERSATIONS", "Error parsing data "+e.toString());
    		}
	        
			return imagePaths;
		}
		
		@Override
    	protected void onPostExecute(ArrayList<String> imagePaths){		
    		
    		try{
    			for(int i=0; i<imagePaths.size() && imagePaths.get(i) != null; i++) {
                new DownloadMoodImages().execute("http://auroralabs.cornellhci.org/img/" + imagePaths.get(i), i);
    			}
            } catch(Exception e) {
                Log.e("log_tag", "Error in downloading image "+e.toString());
                
            }
    		
    	}
    }
  
    private class DownloadMoodImages extends AsyncTask<Object, Void, ImageHelper> {
    	@Override
        protected ImageHelper doInBackground(Object... params) {
    		String url = (String) params[0];
    		int position = (Integer) params[1];
            InputStream is = null;
  
    		//http get
        	try{
        		HttpClient httpclient = new DefaultHttpClient();
        		HttpGet httpget = new HttpGet(url);
        		HttpResponse response = httpclient.execute(httpget);
    		        
        		HttpEntity entity = response.getEntity();
        		is = entity.getContent();
    		}catch(Exception e){
    			Log.e("DOWNLOAD MOOD IMAGES - CONVADAPTER", "Error in http connection "+e.toString());
    		}
    		
    		return (new ImageHelper(position, BitmapFactory.decodeStream(is)));
        }
    	
    	@Override
        protected void onPostExecute(ImageHelper helper){
    		try {
    			conversations.get(helper.position).image = helper.bitmap;
    			notifyDataSetChanged();
    		} catch (Exception e) {
    			Log.e("DOWNLOAD MOOD IMAGES - CONVADAPTER", "Error setting image "+e.toString());
    		}
        }
    }

	@Override
	public int getCount() {
		conversations.trimToSize();
		return conversations.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
