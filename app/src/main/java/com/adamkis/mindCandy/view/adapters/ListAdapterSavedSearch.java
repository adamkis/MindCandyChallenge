package com.adamkis.mindCandy.view.adapters;

import java.util.ArrayList;

import com.adamkis.mindCandy.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListAdapterSavedSearch extends BaseAdapter {

    private LayoutInflater rowInflater = null;
    private ArrayList<String> data = new ArrayList<String>();


    public ListAdapterSavedSearch(ArrayList<String> data, Activity activity){
    	
    	rowInflater = LayoutInflater.from(activity);
    	this.data = data;

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
		TextView search_text;
	}
    
    public View getView(int pos, View convertView, ViewGroup p) {

    	// For the static environments

    	SearchViewHolder searchViewHolder;

    	if( convertView == null ){
    		
    		convertView = rowInflater.inflate(R.layout.pod_saved_search, p, false);
    		
    		searchViewHolder = new SearchViewHolder();
    		searchViewHolder.search_text = (TextView)convertView.findViewById(R.id.search_text);

    		convertView.setTag(searchViewHolder);
    	}
    	else{
    		searchViewHolder = (SearchViewHolder)convertView.getTag();
    	}

    	try{
    		searchViewHolder.search_text.setText(data.get(pos));



    	}
    	catch(Exception e){
    		Log.d("Toovia", "Faulty element in the ListAdapter");
    		e.printStackTrace();
    	}

        return convertView;
    }
    

    
    
    public void setAdapterData(ArrayList<String> data) {
    	
    	this.data = data;

    }
    
    public void appendAdapterData(ArrayList<String> data) {
    	

    	for (int i=0; i<data.size(); i++){
    		this.data.add(data.get(i));
        }
    	

    }
    

    
    
}
