package com.adamkis.mindCandy.model;

import java.io.Serializable;

import org.json.JSONObject;

public class ObjectMindCandy implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String title = null;	
	private String farm_id = null;	
	private String server_id = null;
	private String photo_id = null;	
	private String secret = null;

    private String photoUrl = null;

    public ObjectMindCandy( JSONObject jsonObject ){
		setTitle(jsonObject.optString("title"));
		setFarm_id(jsonObject.optString("farm"));
		setServer_id(jsonObject.optString("server"));
		setPhoto_id(jsonObject.optString("id"));
		setSecret(jsonObject.optString("secret"));

        StringBuilder photoUrlBuilder = new StringBuilder();
        photoUrlBuilder.append("https://farm");
        photoUrlBuilder.append(getFarm_id());
        photoUrlBuilder.append(".staticflickr.com/");
        photoUrlBuilder.append(getServer_id());
        photoUrlBuilder.append("/");
        photoUrlBuilder.append(getPhoto_id());
        photoUrlBuilder.append("_");
        photoUrlBuilder.append(getSecret());
        photoUrlBuilder.append(".jpg");

        setPhotoUrl( photoUrlBuilder.toString() );

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFarm_id() {
		return farm_id;
	}

	public void setFarm_id(String farm_id) {
		this.farm_id = farm_id;
	}

	public String getServer_id() {
		return server_id;
	}

	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) { this.secret = secret; }


    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

}
