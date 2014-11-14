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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.adamkis.mindCandy.R;
import com.adamkis.mindCandy.communication.DataHandlerInterface;
import com.adamkis.mindCandy.communication.DataLoaderGenericGet;
import com.adamkis.mindCandy.model.Constants;
import com.adamkis.mindCandy.model.ObjectMindCandy;
import com.adamkis.mindCandy.utils.Utils;
import com.adamkis.mindCandy.view.adapters.ListAdapterSearch;

import android.support.v4.widget.SwipeRefreshLayout;


public class SearchResultActivity extends ActionBarActivity implements DataHandlerInterface {


    private GridView gridView;
	private SearchResultActivity searchResultActivity;


	private String searchKeyWord = null;
	
	private boolean appending = false;

    public boolean dataLoadDone = true;
    public int searchResultCountTotal = 1;

//    private DataLoaderGenericGet dlgg = null;

    private int page = 1;

    private ArrayList<ObjectMindCandy> imageResultList = null;

    private ListAdapterSearch adapter = null;

    private View loadingStatusView = null;
    private Animation fade_in;
    private Animation fade_out;
    private Animation.AnimationListener animationListener;


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

        // Make our first call to the server
        makeCallToServer(page);
		showProgress(true);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                makeCallToServer(1);
                showProgress(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

	      
	}


    private void makeCallToServer(int page){

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

        url.append("&page=" + page);

        String mode = ( page == 1 ) ? "search" : "append";

        DataLoaderGenericGet dlgg = new DataLoaderGenericGet(searchResultActivity, url.toString(), mode);
        dlgg.execute();
    }

	@Override
	public void onBackPressed(){
		super.onBackPressed();
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

//        Log.i("Log", "Search response was:");
//        Utils.longLog(response);

        if( response != null && response.equalsIgnoreCase("UnknownHostException") ){
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


            imageResultList = new ArrayList<ObjectMindCandy>();

            JSONArray dataJSONArray = new JSONArray();
            try {
                dataJSONArray = rawSearchResponseJSONObject
                        .getJSONObject("photos")
                        .getJSONArray("photo");
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
                        imageResultList.add(new ObjectMindCandy(dataJSONArray.getJSONObject(i)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("Toovia", "When parsing FLICKR object results: book cannot be parsed. Index: " + i);
                    }
                }

                if( imageResultList.size() == 0 ){
                    showError(true, "No matching results found...");
                    showProgress(false);
                    return;
                }

            }

            /////////////////////////////////////////////////////////////////////////////////////
            // Showing search results with the first results
            /////////////////////////////////////////////////////////////////////////////////////
            if ( mode.equals("search") ){

                try {
                    searchResultCountTotal =
                            Integer.parseInt(
                            rawSearchResponseJSONObject
                                .getJSONObject("photos")
                                .getString("total"));
                } catch (Exception e) {
                    e.printStackTrace();
                    showError(true, "No matching results found...");
                    showProgress(false);
                    return;
                }

                // Showing the results
                adapter = new ListAdapterSearch(imageResultList, searchResultActivity);
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


                gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {}

                    @Override
                    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {


                        boolean loadMore = ( firstVisible + visibleCount >= totalCount ) && totalCount!=0 && dataLoadDone && totalCount < searchResultCountTotal;
//                            Log.i("Log", "\nFirst visible: " + Integer.toString(firstVisible));
//                            Log.i("Log", "Visible count: " + Integer.toString(visibleCount));
//                            Log.i("Log", "Total count: " + Integer.toString(totalCount));
//                            Log.i("Log", "DataLoadDone: " + dataLoadDone);
//                            Log.i("Log", "SearchResultCountTotal: " + searchResultCountTotal);

                        if(loadMore) {
                            dataLoadDone = false;
                            makeCallToServer(++page);
                        }
                    }
                });


                showProgress(false);


            }

            /////////////////////////////////////////////////////////////////////////////////////
            // Appending new Results
            /////////////////////////////////////////////////////////////////////////////////////
            if ( mode.equals("append") ){

                dataLoadDone = true;
                adapter.appendAdapterData(imageResultList);
                adapter.notifyDataSetChanged();

            }


        } catch (Throwable e) {
            e.printStackTrace();
        }

		
	}


    @Override
    public void showProgress(boolean show) {

        // init if not inited
        if( loadingStatusView == null )
            loadingStatusView = (View)findViewById(R.id.loading_status);
        if( fade_in == null )
            fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        if( fade_out == null )
            fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        if( animationListener == null ){
            animationListener = new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) {
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @Override
                public void onAnimationEnd(Animation arg0) {
                    loadingStatusView.setVisibility(View.GONE);
                }
            };
        }

        // handling visibility
        if( show ){
            loadingStatusView.setVisibility( View.VISIBLE );
            loadingStatusView.startAnimation(fade_in);
        }
        else{
            fade_out.setAnimationListener(animationListener);
            loadingStatusView.startAnimation( fade_out );
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
