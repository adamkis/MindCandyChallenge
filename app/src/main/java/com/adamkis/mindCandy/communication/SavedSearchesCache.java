package com.adamkis.mindCandy.communication;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class SavedSearchesCache {

	private final static String fileName = "saved_searches";

	public static void saveSearches( ArrayList<String> objects, Context context ){
		// Write user to the INTERNAL STORAGE
		FileOutputStream fos = null;
		try {

			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(objects);
	    	os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> loadSearches( Context context ){
		
		ArrayList<String> objects = null;
		
		ObjectInputStream objectInputStream = null;
		try {
			
			FileInputStream fis = context.openFileInput(fileName);
			objectInputStream = new ObjectInputStream(fis);
			
			// Only way to determine the end is that it'll throw an exception
			while( true ){
				objects = (ArrayList<String>)objectInputStream.readObject();
			}

			
		} catch (EOFException exc) {
			try {
				objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			Log.w("Log", "Data for saved searches is empty");
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

		return objects;
	}
	
	public static void addSearch( String searchText, Context context ){
		
		ArrayList<String> savedSearches = loadSearches(context);
		if( savedSearches == null ){
			savedSearches = new ArrayList<String>();
		}
		savedSearches.add(searchText);
		saveSearches(savedSearches, context);

	}
	
	
	
	
}
