package com.pulego.mysafety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.utils.AlertDialogManager;
import com.pulego.mysafety.utils.ConnectionDetector;
import com.pulego.mysafety.utils.GPSTracker;
import com.pulego.mysafety.utils.GooglePlaces;
import com.pulego.mysafety.utils.PlaceDetails;
import com.pulego.mysafety.R;

/**
 * The Class PropertyDetail is the Activity class that is launched when the user
 * clicks on an item in Feed list or in Search results list. It simply shows
 * dummy details of a property with dummy image of property and also includes a
 * Google Map view. You need to write your own code to load and display actual
 * contents.
 */
public class MapDetail extends CustomActivity {

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Place Details
	PlaceDetails placeDetails;

	// Progress dialog
	ProgressDialog pDialog;

	// Navigation Link
	String navigationLink;

	// Call Link
	String callLink;

	// GPS
	GPSTracker gps;

	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realestate.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapdetail);
		
		getActionBar().setTitle("Map Details");

		// creating GPS Class object
		gps = new GPSTracker(this);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your are here", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(this, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return;
		}

		// button show on map
		Button btnNavigateMap = (Button) findViewById(R.id.navigator);

		/** Button click event for shown on map */
		btnNavigateMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent myIntent = new Intent(
						android.content.Intent.ACTION_VIEW, Uri
								.parse(navigationLink));
				myIntent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(myIntent);
			}
		});

		// button show on map
		Button callNumber = (Button) findViewById(R.id.callNumber);

		callNumber.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse(callLink));
				startActivity(intent);
			}
		});

		Intent i = getIntent();
		// Place referece id
		String reference = i.getStringExtra(KEY_REFERENCE);
		// Calling a Async Background thread
		new LoadSinglePlaceDetails().execute(reference);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MapDetail.this);
			pDialog.setMessage("Loading profile ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Profile JSON
		 * */
		protected String doInBackground(String... args) {
			String reference = args[0];

			// creating Places class object
			googlePlaces = new GooglePlaces();

			// Check if used is connected to Internet
			try {
				placeDetails = googlePlaces.getPlaceDetails(reference);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					if (placeDetails != null) {
						String status = placeDetails.status;

						// check place deatils status
						// Check for all possible status
						if (status.equals("OK")) {
							if (placeDetails.result != null) {
								String name = placeDetails.result.name;
								String address = placeDetails.result.formatted_address;
								String phone = placeDetails.result.formatted_phone_number;
								String latitude = Double
										.toString(placeDetails.result.geometry.location.lat);
								String longitude = Double
										.toString(placeDetails.result.geometry.location.lng);

								Log.d("Place ", name + address + phone
										+ latitude + longitude);

								// Displaying all the details in the view
								// single_place.xml
								TextView lbl_name = (TextView) findViewById(R.id.name);
								TextView lbl_address = (TextView) findViewById(R.id.address);
								TextView lbl_phone = (TextView) findViewById(R.id.phone);
								TextView lbl_location = (TextView) findViewById(R.id.location);

								// Check for null data from google
								// Sometimes place details might missing
								name = name == null ? "Not present" : name; // if
																			// name
																			// is
																			// null
																			// display
																			// as
																			// "Not present"
								address = address == null ? "Not present"
										: address;
								phone = phone == null ? "Not present" : phone;
								latitude = latitude == null ? "Not present"
										: latitude;
								longitude = longitude == null ? "Not present"
										: longitude;

								lbl_name.setText(name);
								lbl_address.setText(address);
								lbl_phone.setText(Html
										.fromHtml("<b>Phone:</b> " + phone));
								lbl_location.setText(Html
										.fromHtml("<b>Latitude:</b> "
												+ latitude
												+ ", <b>Longitude:</b> "
												+ longitude));

								navigationLink = "https://maps.google.com/maps?saddr="
										+ gps.getLatitude()
										+ " , "
										+ gps.getLongitude()
										+ "&daddr="
										+ latitude
										+ " , "
										+ longitude
										+ "&sensor=true";

								callLink = "tel:" + phone;
							}
						} else if (status.equals("ZERO_RESULTS")) {
							alert.showAlertDialog(MapDetail.this,
									"Near Places", "Sorry no place found.",
									false);
						} else if (status.equals("UNKNOWN_ERROR")) {
							alert.showAlertDialog(MapDetail.this,
									"Places Error",
									"Sorry unknown error occured.", false);
						} else if (status.equals("OVER_QUERY_LIMIT")) {
							alert.showAlertDialog(
									MapDetail.this,
									"Places Error",
									"Sorry query limit to google places is reached",
									false);
						} else if (status.equals("REQUEST_DENIED")) {
							alert.showAlertDialog(MapDetail.this,
									"Places Error",
									"Sorry error occured. Request is denied",
									false);
						} else if (status.equals("INVALID_REQUEST")) {
							alert.showAlertDialog(MapDetail.this,
									"Places Error",
									"Sorry error occured. Invalid Request",
									false);
						} else {
							alert.showAlertDialog(MapDetail.this,
									"Places Error", "Sorry error occured.",
									false);
						}
					} else {
						alert.showAlertDialog(MapDetail.this, "Places Error",
								"Sorry error occured.", false);
					}

				}
			});

		}

	}
}
