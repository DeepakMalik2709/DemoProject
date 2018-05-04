package com.notes.nicefact.to;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jitender
 * 
 *         POJO to hold a single property for google drive files
 * 
 */
public class GoogleFileProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String key;

	String visibility;

	String value;



	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("key", key);
			jsonObject.put("value", value);
			jsonObject.put("visibility", visibility);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}




	public GoogleFileProperty(JSONObject jsonObject) {
		try {
			if (jsonObject.has("key")) {
				key = jsonObject.getString("key");
			}
			if (jsonObject.has("value")) {
				value = jsonObject.getString("value");
			}
			if (jsonObject.has("visibility")) {
				visibility = jsonObject.getString("visibility");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public GoogleFileProperty() {
		super();
		// TODO Auto-generated constructor stub
	}




	public String getKey() {
		return key;
	}




	public void setKey(String key) {
		this.key = key;
	}




	public String getVisibility() {
		return visibility;
	}




	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}




	public String getValue() {
		return value;
	}




	public void setValue(String value) {
		this.value = value;
	}


}
