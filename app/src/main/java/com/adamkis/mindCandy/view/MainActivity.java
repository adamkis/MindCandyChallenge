package com.adamkis.mindCandy.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.adamkis.mindCandy.R;
import com.adamkis.mindCandy.communication.SavedSearchesCache;
import com.adamkis.mindCandy.utils.Utils;
import com.adamkis.mindCandy.view.adapters.ListAdapterSavedSearch;

public class MainActivity extends ActionBarActivity {

	private Activity mainActivity = this;
	private ListView listView = null;
	private EditText book_search_keyword_edittext  = null;
	
	@SuppressLint("InflateParams") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getSupportActionBar().hide(); 
		
		setContentView(R.layout.activity_main);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	    
	    
	    
	    
	    listView = (ListView)this.findViewById(R.id.saved_searches_listview);
    	
		View header =  getLayoutInflater().inflate(R.layout.header_main, null);
		listView.addHeaderView( header, null, false);
	    

		View footer =  getLayoutInflater().inflate(R.layout.footer_main, null);
		listView.addFooterView( footer );

		book_search_keyword_edittext = (EditText)findViewById(R.id.book_search_keyword_edittext); 
		
		
		final Button searchButton = (Button)findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	            performSearch( book_search_keyword_edittext.getText().toString() );
			}
		});
		
		book_search_keyword_edittext.setOnEditorActionListener(
				new TextView.OnEditorActionListener() {
					
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            performSearch( book_search_keyword_edittext.getText().toString() );
		            return true;
		        }
		        return false;
		    }

			
		});

		
		 
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    	
		if( listView == null )
			listView = (ListView)this.findViewById(R.id.saved_searches_listview);
		
		if( book_search_keyword_edittext == null )
			book_search_keyword_edittext = (EditText)findViewById(R.id.book_search_keyword_edittext);
		
		ArrayList<String> saved_searches = SavedSearchesCache.loadSearches(mainActivity);
		
		ListAdapterSavedSearch adapter = new ListAdapterSavedSearch(saved_searches, mainActivity);
		listView.setAdapter(adapter);	
		adapter.notifyDataSetChanged();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
				if ( (arg0.getItemAtPosition(arg2)) != null ){
				
//					Toast.makeText(mainActivity, (String)(arg0.getItemAtPosition(arg2)), Toast.LENGTH_SHORT).show();
					performSearch((String)(arg0.getItemAtPosition(arg2)));
					
				}
				else{
					
				}
			
			}
		});
		
	}
	
	private synchronized void performSearch( String text ){
		if( text != null && text.length() > 0 ){
			Intent searchIntent = new Intent(mainActivity, SearchResultActivity.class);
			searchIntent.putExtra("searchKeyWord", text);
			startActivity(searchIntent);
			
			if( SavedSearchesCache.
					loadSearches(mainActivity) == null ||
				!SavedSearchesCache.
					loadSearches(mainActivity).
					contains(text )
					){
				SavedSearchesCache.addSearch(text, mainActivity);
			}
			
		}
		else{
			Utils.showCrouton("Please add keywords", mainActivity);
		}
	}
	
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }
}
