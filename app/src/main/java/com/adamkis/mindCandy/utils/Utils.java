package com.adamkis.mindCandy.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import com.adamkis.mindCandy.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.TextView;


public class Utils {

	@SuppressWarnings("deprecation")
	public static int getScreenWidthInPixes(Activity activity){
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}
	
	@SuppressWarnings("deprecation")
	public static int getScreenHeightInPixes(Activity activity){
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	
	
	public static void showCrouton(String message, Activity activity){
		
		View crouton_view = activity.getLayoutInflater().inflate(R.drawable.crouton_layout, null);
		((TextView)crouton_view.
				findViewById(R.id.crouton_text)).setText(message);
		Crouton.show(activity, crouton_view);
	
		
	}
	
	public static boolean isEmailValid(String email){
		if( email == null || email.length()<1 )
			return false;
		else
			return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
	}

	@SuppressLint("ClickableViewAccessibility") 
	public static void setTouchColor(final View view, final String defaultColor, final String pressedColor){
		
		final String pressedColorToSet;
		if( pressedColor == null )
			pressedColorToSet = "#9E9E9E";
		else
			pressedColorToSet = pressedColor;
		
		final String defaultColorToSet;
		if( defaultColor == null )
			defaultColorToSet = "#FFFFFF";
		else
			defaultColorToSet = defaultColor;

		if( view != null )
			view.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
	                if(event.getAction() == MotionEvent.ACTION_DOWN){
	                	view.setBackgroundColor(Color.parseColor( pressedColorToSet ));
	                }
	                if(event.getAction() == MotionEvent.ACTION_CANCEL){
	                	view.setBackgroundColor(Color.parseColor( defaultColorToSet ));
//	                	view.invalidate();
	                	view.clearFocus();
	                	view.clearAnimation();
	                }
	                if(event.getAction() == MotionEvent.ACTION_MOVE){
	                	view.clearFocus();
	                	view.clearAnimation();
	                }
	                if(event.getAction() == MotionEvent.ACTION_UP){
	                	view.setBackgroundColor(Color.parseColor( defaultColorToSet ));
	                }
					return false;
				}
			});
	}
	
	public static void longLog(String str) {
		if( str != null ){
		    if(str.length() > 4000) {
		        Log.d("Log", str.substring(0, 4000));
		        longLog(str.substring(4000));
		    } else
		        Log.d("Log", str);
		}
	}
	
	

	public HttpClient sslClient(HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() { 
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
	    }
	}
	
	
	public class MySSLSocketFactory extends SSLSocketFactory {
	     SSLContext sslContext = SSLContext.getInstance("TLS");

	     public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	         super(truststore);

	         TrustManager tm = new X509TrustManager() {
	             public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	             }

	             public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	             }

	             public X509Certificate[] getAcceptedIssuers() {
	                 return null;
	             }

	         };

	         sslContext.init(null, new TrustManager[] { tm }, null);
	     }

	     public MySSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
	        super(null);
	        sslContext = context;
	     }

	     @Override
	     public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	         return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	     }

	     @Override
	     public Socket createSocket() throws IOException {
	         return sslContext.getSocketFactory().createSocket();
	     }
	}
	


	/**
	 * Calculates the distance in miles between two lat/long points
	 * using the haversine formula
	 */
	public static double haversine(double lat1, double lng1, double lat2, double lng2) {
	    int r = 6371; // average radius of the earth in km
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
	      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = r * c;
	    // Originally in Kilometers, transforming to miles
	    return d * 0.621371;
	}
	
	public static String textShortenerWords( String inputText, int maxWords ){
		
    	String[] inputTextArray = inputText.split(" ");
    	if( inputTextArray.length > maxWords ){
    		String rerponseString = "";
    		int i = 0;
    		for( String word : inputTextArray ){
    			rerponseString += word + " "; 
    			if( i == maxWords )
    				break;
    			i++;
    		}
    		rerponseString += "...";
    		return rerponseString;
    	}
    	else{
    		return inputText;
    	}
    	
	}
	
	public static String textShortener( String inputText, int maxCharacters ){
		if( inputText.length() > maxCharacters )
			return inputText.substring(0, maxCharacters) + " ...";
		else
			return inputText;

	}
	
	public static String capitalizeString(String string, Context context) {
		  char[] chars = string.toLowerCase(context.getResources().getConfiguration().locale).toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
		      found = false;
		    }
		  }
		  return String.valueOf(chars);
	}
	
	public static int dpToPx(int dp, Context context) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	

	
	
	
	public static class CreditCardValidator {
		
		public static boolean validate( long creditcard ) {
			//long creditcard = 4388576018402626L; //Invalid
//			long creditcard = 4388576018410707L; //Valid
			int[] digits = getDigits(creditcard);
			int sumEvenPlaces = sumEvenPlaces(digits);
			int sumOddPlaces = sumOddPlaces(digits);
			int totalSum = sumEvenPlaces + sumOddPlaces;
			
			//Check if valid
			if (totalSum % 10 == 0){
				System.out.println("The credit card number is valid");
				return true;
			}
			else{
				System.out.println("The credit card number is invalid");
				return false;
			}
		}
		
		private static int[] getDigits(long creditcard) {
			int[] digits = new int[16];
			
			for (int index=15; index>=0; index--) {
				int digit = (int) (creditcard % 10);
				digits[index] = digit;
				creditcard /= 10;
			}
			
			return digits;
		}
		
		//sum even places from right to left
		//doubling every 2nd place from right
		//save sum of doubled numbers
		private static int sumEvenPlaces(int[] digits) {
			int sum = 0;
			for (int i=0; i<digits.length; i+=2) {
				int doubledValue = digits[i] << 1;
				
				if (doubledValue >= 10) //sum the two digits together if the doubled number is two digits
					sum += 1 + doubledValue - 10;
				else
					sum += doubledValue;
			}
	
			return sum;
		}
		
		private static int sumOddPlaces(int[] digits) {
			int sum = 0;
			for (int i=1; i<digits.length; i+=2) 
				sum += digits[i];
			
			return sum;
		}
	}



	

	
}
