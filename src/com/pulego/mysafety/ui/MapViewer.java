package com.pulego.mysafety.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pulego.mysafety.CoSafetyApplication;
import com.pulego.mysafety.MapDetail;
import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.utils.AlertDialogManager;
import com.pulego.mysafety.utils.ConnectionDetector;
import com.pulego.mysafety.utils.GPSTracker;
import com.pulego.mysafety.utils.GooglePlaces;
import com.pulego.mysafety.utils.Place;
import com.pulego.mysafety.utils.PlacesList;
import com.pulego.mysafety.R;

/**
 * The Class MapViewer is the Fragment class that is launched when the user
 * clicks on Map option in Left navigation drawer or when user tap on the Map
 * icon in action bar. It simply shows a Map View with a few dummy location
 * markers on map. You can customize this class to load and display actual
 * locations on map.
 */
public class MapViewer extends CustomFragment {

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	// Places List
	PlacesList nearPlaces;

	// Connection detector class
	ConnectionDetector cd;

	// Progress dialog
	ProgressDialog pDialog;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Lat Long
	LatLng l[] = { new LatLng(0, 0) };

	// GPS Location
	GPSTracker gps;

	// Context
	Context ctx;

	// list flag
	private boolean isListPopulated = false;

	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();

	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name

	public static String POLICE = "police";
	public static String HOSPITAL = "hospital";
	public static String FIREDEPT = "fire_station";

	public static String POLICE_HOSPITAL_FIREDEPT = "police|hospital|fire_station";

	HashMap<String, String> refMap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.map, null);
		setHasOptionsMenu(true);

		ctx = v.getContext();

		cd = new ConnectionDetector(v.getContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return v;
		}

		// creating GPS Class object
		gps = new GPSTracker(v.getContext());

		setTouchNClick(v.findViewById(R.id.map));

		setTouchNClick(v.findViewById(R.id.btnpolice));
		setTouchNClick(v.findViewById(R.id.btnhospital));
		setTouchNClick(v.findViewById(R.id.btnfiredept));

		setupMap(v, savedInstanceState);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your are here", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());
			showCurrentLocation(new LatLng(gps.getLatitude(),
					gps.getLongitude()));

		} else {
			// Can't get user's current location
			alert.showAlertDialog(ctx, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return v;
		}

		return v;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btnpolice)
			new LoadPlaces().execute(POLICE);
		else if (v.getId() == R.id.btnhospital)
			new LoadPlaces().execute(HOSPITAL);
		else if (v.getId() == R.id.btnfiredept)
			new LoadPlaces().execute(FIREDEPT);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		if (mMapView != null)
			mMapView.onPause();

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		if (mMapView != null)
			mMapView.onDestroy();

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

		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (mMapView != null) {
			mMapView.onResume();

			mMap = mMapView.getMap();
			if (mMap != null) {
				// mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.setMyLocationEnabled(true);
				mMap.setInfoWindowAdapter(null);
				setupMarker(((CoSafetyApplication) getActivity()
						.getApplication()).getMarkerType());
			}
		}
	}

	/**
	 * Setup and initialize the Google map view.
	 * 
	 * @param v
	 *            the root view
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	private void setupMap(View v, Bundle savedInstanceState) {

		MapsInitializer.initialize(getActivity());

		mMapView = (MapView) v.findViewById(R.id.map);

		mMapView.onCreate(savedInstanceState);

	}

	/**
	 * This method simply place a few dummy location markers on Map View. You
	 * can write your own logic for loading the locations and placing the marker
	 * for each location as per your need.
	 */
	private void showCurrentLocation(LatLng currentLatLot) {

		setUpMapIfNeeded();

		if (mMap != null) {

			mMap.clear();

			MarkerOptions opt = new MarkerOptions();
			opt.position(currentLatLot).title("You are here")
					.snippet("Your current location");
			opt.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.map_marker));
			mMap.addMarker(opt);

			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLot,
					10));
		}
	}

	private void setupMarker(String type) {
		mMap.clear();

		if (isListPopulated) {

			int placeIndex = 0;

			refMap = new HashMap<String, String>();

			for (LatLng ll : l) {

				Place p = nearPlaces.results.get(placeIndex);

				refMap.put(p.name, p.reference);

				MarkerOptions opt = new MarkerOptions();
				opt.position(ll).title(p.name)
						.snippet("Tap to Call or Navigate here");

				if (type == POLICE)
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_police));

				else if (type == HOSPITAL)
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_amb));
				else if (type == FIREDEPT)
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_firedept));

				mMap.addMarker(opt);

				placeIndex++;
			}

			if (l != null)
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l[0], 10));
		}

		mMap.setOnMapClickListener(new OnMapClickListener() {

			public void onMapClick(LatLng ll) {

				// Creating an instance of MarkerOptions to set position
				MarkerOptions markerOptions = new MarkerOptions();

				// Setting position on the MarkerOptions
				markerOptions.position(ll);

				// Animating to the currently touched position
				mMap.animateCamera(CameraUpdateFactory.newLatLng(ll));

				// Adding marker on the GoogleMap
				Marker marker = mMap.addMarker(markerOptions);

				// Showing InfoWindow on the GoogleMap
				marker.showInfoWindow();
			}
		});

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			public void onInfoWindowClick(Marker m) {

				Intent in = new Intent(getActivity(), MapDetail.class);
				String ref = refMap.get(m.getTitle());

				in.putExtra(KEY_REFERENCE, ref);
				startActivity(in);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		String types;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage(Html.fromHtml("Loading Nearby places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();

			try {
				// Separate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				//

				types = args[0];// "police|hospital|fire_station"; //
								// Listing places
								// only cafes,
				((CoSafetyApplication) getActivity().getApplication())
						.setMarkerType(types);

				Log.i("Nearby places", args[0]);

				// Radius in meters - increase this value if you don't find any
				// places
				double radius = 25000; // 1000 meters

				// get nearest places
				nearPlaces = googlePlaces.search(gps.getLatitude(),
						gps.getLongitude(), radius, types);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			getActivity().runOnUiThread(new Runnable() {
				public void run() {

					if (nearPlaces != null) {

						/**
						 * Updating parsed Places into LISTVIEW
						 * */
						// Get json response status
						String status = nearPlaces.status;

						// Check for all possible status
						if (status.equals("OK")) {
							// Successfully got places details
							if (nearPlaces.results != null) {

								l = new LatLng[nearPlaces.results.size()];

								// loop through each place
								int number = 0;
								for (Place p : nearPlaces.results) {

									l[number] = new LatLng(
											p.geometry.location.lat,
											p.geometry.location.lng);

									number++;

								}
								isListPopulated = true;
								setupMarker(types);

							}
						} else if (status.equals("ZERO_RESULTS")) {
							// Zero results found
							alert.showAlertDialog(
									ctx,
									"Error",
									"Sorry no places found.",
									false);
						} else if (status.equals("UNKNOWN_ERROR")) {
							alert.showAlertDialog(ctx, "Error",
									"Sorry unknown error occured.", false);
						} else if (status.equals("OVER_QUERY_LIMIT")) {
							alert.showAlertDialog(
									ctx,
									"Error",
									"Sorry query limit to google places is reached",
									false);
						} else if (status.equals("REQUEST_DENIED")) {
							alert.showAlertDialog(ctx, "Error",
									"Sorry error occured. Request is denied",
									false);
						} else if (status.equals("INVALID_REQUEST")) {
							alert.showAlertDialog(ctx, "Error",
									"Sorry error occured. Invalid Request",
									false);
						} else {
							alert.showAlertDialog(ctx, "Error",
									"Sorry error occured.", false);
						}
					} else
						alert.showAlertDialog(ctx, "Error",
								"Sorry error occured.", false);
				}
			});

		}

	}

}
