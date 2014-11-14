package com.adamkis.mindCandy.view;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.adamkis.mindCandy.R;
import com.adamkis.mindCandy.communication.DataHandlerInterface;
import com.adamkis.mindCandy.communication.DataLoaderGenericGet;
import com.adamkis.mindCandy.model.Constants;
import com.adamkis.mindCandy.model.ObjectMindCandy;
import com.adamkis.mindCandy.utils.Utils;
import com.adamkis.mindCandy.view.adapters.ListAdapterSearch;



public class SearchResultActivity extends ActionBarActivity implements DataHandlerInterface {


    private GridView gridView;
	private SearchResultActivity searchResultActivity;
	 
//	private int resultCount;
//	private int currentPageIndex = 1;

	private String searchKeyWord = null;
	
//	private boolean appending = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_activity);
		

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Search Results");
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);

		searchResultActivity = this;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("searchKeyWord") ) {
		    searchKeyWord = extras.getString("searchKeyWord");
		}
		else{
			showError(true, "The search went wrong. Sorry about that");
			return;
		}

        gridView = (GridView)findViewById(R.id.searchResultList);

		StringBuilder url = new StringBuilder();
		url.append(Constants.FLICKR_SEARCH_PATH);
		url.append("&api_key=" + Constants.FLICKR_API_KEY);
		url.append("&format=json&nojsoncallback=1");

		try { 
		  url.append("&tags=" + URLEncoder.encode(searchKeyWord, "utf-8"));
		} catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
		  showError(true, "The search went wrong. Sorry about that");
		  return;
		}


		DataLoaderGenericGet dlgg = new DataLoaderGenericGet(searchResultActivity, url.toString(), "search");
		dlgg.execute();
		showProgress(true);

	      
	}
	      

	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}


	@Override
	public void showProgress(final boolean show) {

		final View loadingStatusView = (View)findViewById(R.id.loading_status);
		if( show ){
			loadingStatusView.setVisibility( View.VISIBLE );
		}
		else{
			loadingStatusView.setVisibility( View.GONE );
		}
		
	}

	
	public void showError(final boolean show, String message) {

		final TextView errorMessage = (TextView)findViewById(R.id.errorMessage);
		if( show ){
			errorMessage.setText(message);
			errorMessage.setVisibility( View.VISIBLE );
		}
		else{
			errorMessage.setVisibility( View.GONE );
		}
		
	}


	@Override
	public void callBackPost(String response, String mode) {
	}



	@Override
	public void callBackGet(String response, String mode) {

		if ( mode.equals("search") ){
		
			Log.i("Log", "Search response was:");
			Utils.longLog(response);
			
			if( response.equalsIgnoreCase("UnknownHostException") ){
				showError(true, "Oops... Looks like your device has no connection");
				return;
			}
			
			
	        try {		
	        	
	        	// Parsing the JSON
	        	JSONObject rawSearchResponseJSONObject;
				try {
					rawSearchResponseJSONObject = new JSONObject(response);
				} catch (JSONException e1) {
					showError(true, "Oops, something went wrong...");
					e1.printStackTrace();
					return;
				}


	        	ArrayList<ObjectMindCandy> resultList = new ArrayList<ObjectMindCandy>();

				JSONArray dataJSONArray = new JSONArray();
				try {
					dataJSONArray = rawSearchResponseJSONObject
							.optJSONObject("photos")
							.optJSONArray("photo");
				} catch (Exception e) {
					e.printStackTrace();
					showError(true, "No matching results found...");
					showProgress(false);
					return;
				}
				
				if( dataJSONArray.length() < 1 ){
					showError(true, "No matching results found...");
					showProgress(false);
					return;
				}
				else{
				
					for ( int i = 0; i < dataJSONArray.length(); i++  ){
						try {
							resultList.add( new ObjectMindCandy( dataJSONArray.optJSONObject(i) ) );
						} catch (Exception e) {
							e.printStackTrace(); 
							Log.w("Toovia", "When parsing FLICKR object results: book cannot be parsed. Index: " + i);
						}
					} 
		
					if( resultList.size() == 0 ){
						showError(true, "No matching results found...");
						showProgress(false);
						return;
					}
				
				}

	
				
				// Showing the results
				ListAdapterSearch adapter = new ListAdapterSearch(resultList, searchResultActivity);

                gridView.setAdapter(adapter);
				adapter.notifyDataSetChanged();

                gridView.setOnItemClickListener(new OnItemClickListener() {
				
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					
						if ( ((ObjectMindCandy)arg0.getItemAtPosition(arg2)).getTitle() != null ){
						
							Intent detailsPageIntent = new Intent(searchResultActivity, ProfilePageActivity.class);
							detailsPageIntent.putExtra("FLICKRObject",
									((Serializable)(((ObjectMindCandy)arg0.getItemAtPosition(arg2)))));
							searchResultActivity.startActivity( detailsPageIntent );
							return;
						}
						else{
							
						}
					
					}
				});

				showProgress(false);
				
				
				
					
			} catch (Throwable e) {
				e.printStackTrace();
			}
        
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
    	default:
    		return super.onOptionsItemSelected(item);
		}

	}
	
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }


}
