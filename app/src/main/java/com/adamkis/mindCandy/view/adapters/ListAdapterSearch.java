package com.adamkis.mindCandy.view.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamkis.mindCandy.R;
import com.adamkis.mindCandy.model.ObjectMindCandy;
import com.squareup.picasso.Picasso;

public class ListAdapterSearch extends BaseAdapter {

    private LayoutInflater rowInflater = null;
    private ArrayList<ObjectMindCandy> data = new ArrayList<ObjectMindCandy>();
    private Activity activity;

    private int lastPosition = -1;


    public ListAdapterSearch(ArrayList<ObjectMindCandy> data, Activity activity){
    	
    	rowInflater = LayoutInflater.from(activity);
    	this.data = data;
    	this.activity = activity;

    }
    
    public int getCount() { 
    	if( data == null )
    		return 0;
    	return data.size(); 
    }
    
    public Object getItem(int pos) {
    	return data.get(pos); 
    }
    public long getItemId(int pos) { return pos; }

	static class SearchViewHolder{
		TextView titleTextView;
		ImageView image;
	}
    
    public View getView(int pos, View convertView, ViewGroup p) {

    	// For the static environments

    	SearchViewHolder searchViewHolder;

    	if( convertView == null ){
    		
    		convertView = rowInflater.inflate(R.layout.pod_search_result, p, false);
    		
    		searchViewHolder = new SearchViewHolder();
    		searchViewHolder.titleTextView = (TextView)convertView.findViewById(R.id.title);
    		searchViewHolder.image = (ImageView)convertView.findViewById(R.id.image);

    		convertView.setTag(searchViewHolder);
    	}
    	else{
    		searchViewHolder = (SearchViewHolder)convertView.getTag();
    	}

    	try{
    		searchViewHolder.titleTextView.setText(data.get(pos).getTitle());
    		
			StringBuilder photoUrl = new StringBuilder();
			photoUrl.append("https://farm");
			photoUrl.append(data.get(pos).getFarm_id());
			photoUrl.append(".staticflickr.com/");
			photoUrl.append(data.get(pos).getServer_id());
			photoUrl.append("/");
			photoUrl.append(data.get(pos).getPhoto_id());
			photoUrl.append("_");
			photoUrl.append(data.get(pos).getSecret());
			photoUrl.append(".jpg");
			
    		try{
	        	Picasso
	        	.with(activity)
	      	  	.load(photoUrl.toString())
	      	  	.placeholder(R.drawable.grey_rectangle_600_2_spot)
	      	  	.noFade()
	      	  	.into(searchViewHolder.image);

    		}catch(Exception e){ e.printStackTrace(); }


//        Log.i("Log", "LastPos>>" + lastPosition + " Position>>" + pos);

//        Animation animation = AnimationUtils.loadAnimation(activity, (pos > lastPosition) ? R.anim.slide_up : R.anim.slide_down);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_up);

    	if( /*pos > lastPosition &&  pos == lastPosition+1 && */ animation != null){
	        convertView.startAnimation(animation);
    	}
        lastPosition = pos;


    	}
    	catch(Exception e){
    		Log.d("Toovia", "Faulty element in the ListAdapter");
    		e.printStackTrace();
    	}

        return convertView;
    }
    

    
    
    public void setAdapterData(ArrayList<ObjectMindCandy> data) {
    	
    	this.data = data;

    }
    
    public void appendAdapterData(ArrayList<ObjectMindCandy> data) {
    	

    	for (int i=0; i<data.size(); i++){
    		this.data.add(data.get(i));
        }
    	

    }
    

    
    
}
