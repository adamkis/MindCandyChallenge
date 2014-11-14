package com.adamkis.mindCandy.communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.adamkis.mindCandy.utils.Utils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class DataLoaderGenericGet extends AsyncTask<String, Void, String > {

	private CookieStore cookieStore;
	private String mode = null;
	private String url = null;
	private DataHandlerInterface dataHandlerInterface = null;

	
	public DataLoaderGenericGet( DataHandlerInterface dataHandlerInterface, String url, String mode ){
		this.dataHandlerInterface = dataHandlerInterface;
		this.mode = mode;
		this.url = url;
	}

	@Override
	protected void onPreExecute(){}
	
    @Override
    protected void onPostExecute(String response) { 

    	if( dataHandlerInterface != null ){
    		dataHandlerInterface.callBackGet(response, mode);
    	}
    	else{
    		Log.e("Log", "the caller of the data load asynch task became null");
    	}

    }

	
	
	@Override
	protected String doInBackground(String... params) {

        StringBuffer response = new StringBuffer();

    	try {

	    	HttpClient httpclient = new DefaultHttpClient();
	    	
	    	Utils tu = new Utils();
	    	httpclient = tu.sslClient(httpclient);
	    	
	        HttpContext localContext = new BasicHttpContext();
	        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

	        HttpGet httpGet = new HttpGet( url ); 
			Log.i("Toovia", "Mode: " + mode + " The URL called for Generic GET call: " + url );

			HttpResponse httpResponse = httpclient.execute(httpGet, localContext);
			HttpEntity responseEntity = httpResponse.getEntity();

			InputStream inputStream = AndroidHttpClient.getUngzippedContent( responseEntity );
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String s = null;
        	while((s = bufferedReader.readLine()) != null){
        		response.append(s); 
        	}
			
//			response.append( bufferedReader.readLine() );

			// Try to close
			try {
				responseEntity.consumeContent();
			} catch (Exception e) {e.printStackTrace();}		


		} 
    	catch (UnknownHostException e) {
			e.printStackTrace();
			response.delete(0, response.length());
			response.append("UnknownHostException");
		}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	catch (Throwable e) {
			e.printStackTrace();
			System.gc();
		}
		
    	return response.toString();


	}

}
