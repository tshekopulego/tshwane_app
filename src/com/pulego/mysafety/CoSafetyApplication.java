package com.pulego.mysafety;

import android.app.Application;
import android.content.Context;

public class CoSafetyApplication extends Application {

	private String markerType;

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String type) {
		this.markerType = type;
	}

	private String regId;

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	private static Context context;

	public void onCreate() {
		super.onCreate();
		CoSafetyApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return CoSafetyApplication.context;
	}

	public static String serverURL() {
		return "http://test.tshwanesafety.co.za";

	}
}