package com.pulego.mysafety;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pulego.mysafety.R;
import com.pulego.mysafety.utils.AlertDialogManager;
import com.pulego.mysafety.utils.ConnectionDetector;
import com.pulego.mysafety.utils.ServerUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The Class SplashScreen will launched at the start of the application. It will
 * be displayed for 3 seconds and than finished automatically and it will also
 * start the next activity of app.
 */
public class phoneNumberActivity extends Activity {
	GoogleCloudMessaging gcm;
	String regid;
	String mobileNo;
	String PROJECT_NUMBER = "600629729532";

	Editor editor;

	// Connection detector
	ConnectionDetector cd;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// give your server registration url here
	static final String SERVER_URL = "http://test.tshwanesafety.co.za/dashboard/registerdevice.php";

	static final String DISPLAY_MESSAGE_ACTION = "com.pulego.mysafety.DISPLAY_MESSAGE";

	private static final String SENDER_ID = "600629729532";

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	static final String TAG = "MySafety";

	Context ctx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonenumber);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();

		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by
			return;

		}
		
		editor = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).edit();

		ctx = this;

		runThread(ctx);

		returnMobileNumber();

		final Button captureBtn = (Button) findViewById(R.id.btnOk);
		captureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText phoneNumber = (EditText) findViewById(R.id.phoneText);
				mobileNo = phoneNumber.getText().toString();
				
				if (mobileNo.isEmpty()) {
					// Internet Connection is not present
					alert.showAlertDialog(v.getContext(), "Phone Number",
							"Please enter your phone number", false);
					// stop executing code by
					return;

				}
				
				if (mobileNo.length()<10) {
					// Internet Connection is not present
					alert.showAlertDialog(v.getContext(), "Phone Number",
							"Invalid phone number, Please try again", false);
					// stop executing code by
					return;

				}
					
				
				new RegisterDeviceBackgroundTask(ctx).execute();

			}
		});

	}

	private void returnMobileNumber() {

		mobileNo = getMyPhoneNumber();

		EditText phoneNumber = (EditText) findViewById(R.id.phoneText);

		if (!mobileNo.isEmpty())
			phoneNumber.setText(getMyPhoneNumber());
	}

	private void returnDeviceId() {

		((TextView) findViewById(R.id.deviceId)).setText("Device succesfully registered.");
		
		((TextView) findViewById(R.id.btnOk)).setEnabled(true);

	}

	private String getMyPhoneNumber() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}

	private void runThread(final Context ctx) {

		new Thread() {
			public void run() {

				try {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(ctx);
							String regid = prefs.getString("regId", "");

							// Check device for Play Services APK. If check
							// succeeds,
							// proceed with GCM registration.
							if (checkPlayServices()) {
								gcm = GoogleCloudMessaging.getInstance(ctx);
								regid = getRegistrationId(ctx);

								if (regid.isEmpty()) {
									registerInBackground(ctx);
								}
							} else
								Log.i(TAG,
										"No valid Google Play Services APK found.");
						}
					});
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context ctx) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String registrationId = prefs.getString("regId", "");

		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground(final Context context) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					//sendRegistrationIdToBackend(regid, mobileNo);

					// Persist the regID - no need to register again.
					//storeRegistrationId(regid,mobileNo);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				returnDeviceId();
			}
		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 */
	private void storeRegistrationId(String regId, String phoneNumber) {
		editor.putString("regId", regid);
		editor.putString("phoneNumber", phoneNumber);
		editor.commit();
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(String regId, String phoneNumber) {

		Map<String, String> newparams = new HashMap<String, String>();
		newparams.put("regId", regId);
		newparams.put("phoneNumber", phoneNumber);

		try {
			ServerUtilities.post(SERVER_URL, newparams);
		} catch (IOException e) {
			Log.i(TAG, e.toString() + "\n");
		}
	}
	
	private class RegisterDeviceBackgroundTask extends AsyncTask <Void, Void, Void> {
	    private ProgressDialog dialog;
	     
	    public RegisterDeviceBackgroundTask(Context ctx) {
	        dialog = new ProgressDialog(ctx);
	    }
	 
	    @Override
	    protected void onPreExecute() {
	        dialog.setMessage("Please wait.");
	        dialog.show();
	    }
	     
	    @Override
	    protected void onPostExecute(Void result) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	        
	        Intent i = new Intent(phoneNumberActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
	    }
	     
	    @Override
	    protected Void doInBackground(Void... params) {
	    	
	    	sendRegistrationIdToBackend(regid, mobileNo);
			
			storeRegistrationId(regid, mobileNo);
			
			return null;
	    }
	     
	}

}