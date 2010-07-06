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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class RecentMoodsAdapter extends BaseAdapter {
    private Context mContext;
    private final int numStats = 16;  
    private ArrayList<Status> recentStats;
   //TODO
    @SuppressWarnings("unchecked")
	ArrayList<AsyncTask> tasks;

    public RecentMoodsAdapter(Context c) {
        mContext = c;
        recentStats = new ArrayList<Status>();
      //TODO
        tasks = new ArrayList<AsyncTask>();
    }

    public int getCount() {
    	recentStats.trimToSize();
    	return recentStats.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) { 	
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
            
        } else {
            imageView = (ImageView) convertView;
        }
        //imageView.setImageResource(images[position]);
        if(recentStats.get(position) != null)
        	imageView.setImageBitmap(recentStats.get(position).image);
        return imageView;
    }

    
    //TODO
    public void populate(int id) {
    	killTasks();
    	recentStats.clear();
    	notifyDataSetChanged();
    	tasks.add(new PopulateRecentMoods().execute(id));
    }
    
    //TODO
    @SuppressWarnings("unchecked")
	public void killTasks() {
    	for(AsyncTask task : tasks) {
    		task.cancel(true);
    	}
    	tasks.clear();
    }
    
    private class ImageHelper {
    	Bitmap bitmap;
    	int position;
    	public ImageHelper(int position, Bitmap bitmap) {
    		this.position = position;
    		this.bitmap = bitmap;
    	}
    }
    
    public String getStatusUsername(int position) {
    	return recentStats.get(position).username;
    }
    
    public int getStatusFriendId(int position) {
    	return recentStats.get(position).friendId;
    }
    
    public int getStatusId(int position) {
    	return recentStats.get(position).id;
    }
    
    public String getStatusNote(int position) {
    	return recentStats.get(position).note;
    }
    
    public String getStatusTime(int position) {
    	return recentStats.get(position).time;
    }
    
    
    private class PopulateRecentMoods extends AsyncTask<Object, Void, String[]> {
		@Override
		protected String[] doInBackground(Object... params) {
			int friend_id = (Integer) params[0];
			String result = "";
			String[] imagePaths = new String[numStats];
			//int[] resultIds = new int[numImages];
			InputStream is = null;
			
			//http get
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpResponse response;
			        if (friend_id > 0) {
			        	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("friend_id", String.valueOf(friend_id)));
						HttpPost httppost = new HttpPost("http://auroralabs.cornellhci.org/android/getRecentMoodsForUser.php");
					    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					    response = httpclient.execute(httppost);
			        } else {
			        	HttpGet httpget = new HttpGet("http://auroralabs.cornellhci.org/android/getRecentMoods.php");
			        	response = httpclient.execute(httpget);
			        }
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("POPULATE RECENT MOODS", "Error in http connection "+e.toString());
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
			        Log.e("POPULATE RECENT MOODS", "Error converting result "+e.toString());
			}
			
			try {
			JSONArray jArray = new JSONArray(result);
	        for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                recentStats.add(new aurora.project.Status(json_data.getInt("id"),
	                		json_data.getString("notes"), null, json_data.getInt("user_id"),
	                		json_data.getString("username"), json_data.getString("created")));
	                imagePaths[i] = json_data.getString("path").replace("\\", "");
	        }
	        recentStats.trimToSize();
			} catch(JSONException e){
    		        Log.e("POPULATE RECENT MOODS", "Error parsing data "+e.toString());
    		}
	        
			return imagePaths;
		}
		
		@Override
    	protected void onPostExecute(String[] imagePaths){		
    		
    		try{
    			for(int i=0; i<imagePaths.length && imagePaths[i] != null; i++) {
                tasks.add(new DownloadMoodImages().execute("http://auroralabs.cornellhci.org/img/" + imagePaths[i], i));
    			}
            } catch(Exception e) {
                Log.e("POPULATE RECENT MOODS", "Error in downloading image "+e.toString());            
            }
    		
    	}
    }
  
    
    private class DownloadMoodImages extends AsyncTask<Object, Void, ImageHelper> {
    	@Override
        protected ImageHelper doInBackground(Object... params) {
       		String url = (String) params[0];
       		int position = (Integer) params[1];
            InputStream is = null;

            Bitmap bitmap = null;
            while(bitmap == null) {
            	//http get
            	try{
            		HttpClient httpclient = new DefaultHttpClient();
            		HttpGet httpget = new HttpGet(url);
            		HttpResponse response = httpclient.execute(httpget);

            		HttpEntity entity = response.getEntity();
            		is = entity.getContent();

            		bitmap = BitmapFactory.decodeStream(is);
            	}catch(Exception e){
            		Log.e("DOWNLOAD MOODS IMAGES", "Error in http connection "+e.toString());
            	}

    		if(bitmap == null)
    			Log.e("ERROR", url);
            }
    		return (new ImageHelper(position, bitmap));
        }
    	
    	@Override
        protected void onPostExecute(ImageHelper helper){
    		try {
    			recentStats.get(helper.position).image = helper.bitmap;
    			notifyDataSetChanged();
    		} catch (Exception e) {
    			Log.e("DOWNLOAD MOODS IMAGES", "Error setting image "+e.toString());
    		}		
        }
    }

}
