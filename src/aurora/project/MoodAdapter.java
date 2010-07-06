package aurora.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

public class MoodAdapter extends BaseAdapter {
    private Context mContext;
    private Bitmap[] images;
    private Integer[] imageIds;
    private final int numImages = 36;
    //TODO
	@SuppressWarnings("unchecked")
	ArrayList<AsyncTask> tasks;

    @SuppressWarnings("unchecked")
	public MoodAdapter(Context c) {
        mContext = c;
        imageIds = new Integer[numImages];
        images = new Bitmap[numImages];
        //TODO
        tasks = new ArrayList<AsyncTask>();
    }

    public int getCount() {
        return numImages;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) { 	
    	ImageView imageView = null;
    	try{
        
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
            
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(images[position]);
    	} catch (Exception e) {
    		Log.e("log_tag", "ERROR IN GET VIIIIIEWWWWWWWWWWWWWWWWWWWWW");
    	}
        
        return imageView;
    }

    
    //TODO
    public void populate() {
    	killTasks();
    	images = new Bitmap[numImages];
    	notifyDataSetChanged();
    	tasks.add(new PopulateMoodPaths().execute());
    }
    
   //TODO
	@SuppressWarnings("unchecked")
	public void killTasks() {
    	for(AsyncTask task : tasks) {
    		task.cancel(true);
    	}
    	tasks.clear();
    }
    
    public int getImageId(int position) {
    	return imageIds[position];
    }
    
    private class ImageHelper {
    	Bitmap bitmap;
    	int position;
    	public ImageHelper(int position, Bitmap bitmap) {
    		this.position = position;
    		this.bitmap = bitmap;
    	}
    }
    
    //TODO
    private class PopulateMoodPaths extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			String[] imagePaths = new String[numImages];
			InputStream is = null;
			
			//http get
			try{
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpGet httpget = new HttpGet("http://auroralabs.cornellhci.org/android/moodSelect.php");
			        HttpResponse response = httpclient.execute(httpget);
			        
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();
			}catch(Exception e){
			        Log.e("log_tag", "Error in http connection "+e.toString());
			}
			
			//convert \n separated response to Array
			try{
				
			        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        String line = null;
			        int i = 0;
			        while ((line = reader.readLine()) != null) {
			        	int beginPath = line.indexOf('e');
			        	imageIds[i] = Integer.parseInt(line.substring(0,beginPath));
			        	imagePaths[i] = line.substring(beginPath);
			        	i++;
			                //Log.e("log_tag", line);
			        }
			        is.close();
			}catch(Exception e){
			        Log.e("log_tag", "Error converting result "+e.toString());
			}

			return imagePaths;
		}
		@Override
		protected void onPostExecute(String[] imagePaths){		
			for(int i=0; i<imagePaths.length; i++) {
				try{
					tasks.add(new DownloadMoodImages().execute("http://auroralabs.cornellhci.org/img/" + imagePaths[i], i));
				} catch(Exception e) {
					Log.e("log_tag", "Error in downloading image "+e.toString());
					Log.e("log_tag", "http://auroralabs.cornellhci.org/img/" + imagePaths[i]);
				}
			}
		}
    }
    
    //TODO
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
            		if(bitmap == null)
            			Log.e("ERROR", url);
            	}catch(Exception e){
            		Log.e("log_tag", "Error in http connection "+e.toString());
            	}
            }

    		return (new ImageHelper(position, bitmap));
        }
    	
    	@Override
        protected void onPostExecute(ImageHelper helper){
    		images[helper.position] = helper.bitmap;
    		notifyDataSetChanged();
        }
    }

}
