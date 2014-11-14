package com.adamkis.mindCandy.view;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamkis.mindCandy.R;
import com.adamkis.mindCandy.model.ObjectMindCandy;
import com.squareup.picasso.Picasso;


public class ProfilePageActivity extends ActionBarActivity {

	private ObjectMindCandy flickrObject = null;
	private ProfilePageActivity profilePageActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_page_activity);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
		
		
		
		
		Bundle extras = getIntent().getExtras(); 
		if ( extras != null )
			flickrObject = (ObjectMindCandy)extras.get("FLICKRObject");
		
		
		if( flickrObject != null ){
			
			getSupportActionBar().setTitle(flickrObject.getTitle());
			
			TextView titleTextView = (TextView)findViewById(R.id.title);
			ImageView image = (ImageView)findViewById(R.id.image);
	
			titleTextView.setText(flickrObject.getTitle());
			
			
			// Displaying the image
			StringBuilder photoUrl = new StringBuilder();
			photoUrl.append("https://farm");
			photoUrl.append(flickrObject.getFarm_id());
			photoUrl.append(".staticflickr.com/");
			photoUrl.append(flickrObject.getServer_id());
			photoUrl.append("/");
			photoUrl.append(flickrObject.getPhoto_id());
			photoUrl.append("_");
			photoUrl.append(flickrObject.getSecret());
			photoUrl.append(".jpg");
			
    		try{
	        	Picasso
	        	.with(profilePageActivity)
	      	  	.load(photoUrl.toString())
	      	  	.placeholder(R.drawable.grey_rectangle_600_2_spot)
	      	  	.into(image);

    		}catch(Exception e){ e.printStackTrace(); }

			
		}
		else{
			showError(true);
		}


	}

	public void showError(final boolean show) {

		final TextView error_message = (TextView)findViewById(R.id.error_message);
		if( show ){
			error_message.setVisibility( View.VISIBLE );
		}
		else{
			error_message.setVisibility( View.GONE );
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

