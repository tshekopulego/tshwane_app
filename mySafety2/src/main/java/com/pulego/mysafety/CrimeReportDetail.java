package com.pulego.mysafety;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.image.SmartImageView;
import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.db.DBHelper;
import com.pulego.mysafety.utils.ServerUtilities;
import com.pulego.mysafety.R;

/**
 * The Class PropertyDetail is the Activity class that is launched when the user
 * clicks on an item in Feed list or in Search results list. It simply shows
 * dummy details of a property with dummy image of property and also includes a
 * Google Map view. You need to write your own code to load and display actual
 * contents.
 */
public class CrimeReportDetail extends CustomActivity {

	private ShareActionProvider mShareActionProvider;

	String subtitle;
	String title;
	String pictureurl;
	String datetime;
	String status;
	String uid;
	String lat;
	String lot;

	TextView emptyMessage;
	private ListView obj;
	private ArrayList<String[]> fList;
	DBHelper mydb;
	ArrayList array_list;
	NotificationsAdapter arrayAdapter;
	
	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realestate.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crimereport_detail);
		
		getActionBar().setTitle("Crime Report Detail");

		Bundle extras = getIntent().getExtras();
		title = extras.getString("title");
		subtitle = extras.getString("description");

		status = extras.getString("status");
		datetime = extras.getString("datetime");
		
		uid = extras.getString("uid");
		
		lat = extras.getString("lat");
		lot = extras.getString("lot");
		
		try {
			setupMap(savedInstanceState);
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(title);

		TextView subtitleTextView = (TextView) findViewById(R.id.subTitle);
		subtitleTextView.setText(subtitle);

		TextView datetimeTextView = (TextView) findViewById(R.id.datetime);
		datetimeTextView.setText(datetime);

		emptyMessage = (TextView) findViewById(R.id.empty);

		mydb = new DBHelper(this);
		loadCrimeUpdateNotification(uid);

		if (fList.isEmpty()) {
			obj.setVisibility(View.GONE);
			emptyMessage.setVisibility(View.VISIBLE);
		} else {
			obj.setVisibility(View.VISIBLE);
			emptyMessage.setVisibility(View.GONE);
		}

	}

	private void loadCrimeUpdateNotification(String uid) {

		fList = mydb.getCaseUpdateNotification(uid);

		arrayAdapter = new NotificationsAdapter(this);

		// adding it to the list view.
		obj = (ListView) findViewById(R.id.list);
		obj.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

	}

	/**
	 * The Class FeedAdapter is the adapter class for Feed ListView. The current
	 * implementation simply shows dummy contents and you can customize this
	 * class to display actual contents as per your need.
	 */
	private class NotificationsAdapter extends BaseAdapter {
		Context context;

		public NotificationsAdapter(Context context) {
			this.context = context;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return fList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String[] getItem(int position) {
			return fList.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			 LayoutInflater inflater = (LayoutInflater) context
				        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.crimereportlist_item, null);
			}

			String[] d = getItem(position);
			TextView lbl = (TextView) convertView.findViewById(R.id.title);
			lbl.setText(d[0]);

			lbl = (TextView) convertView.findViewById(R.id.message);
			lbl.setText(d[2]);

			TextView lblduration = (TextView) convertView
					.findViewById(R.id.duration);
			lblduration.setText(d[1]);

			ServerUtilities.getTimeDifference(lblduration.getText().toString(),
					lblduration);


			return convertView;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
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
		mMapView.onResume();
		
		mMap = mMapView.getMap();
		if (mMap != null)
		{
			mMap.setMyLocationEnabled(false);
			mMap.getUiSettings().setAllGesturesEnabled(false);
			mMap.setInfoWindowAdapter(null);
			
			setupMarker(lat,lot);
		}

	}
	
	/**
	 * Setup and initialize the Google map view.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 * @throws GooglePlayServicesNotAvailableException 
	 */
	private void setupMap(Bundle savedInstanceState) throws GooglePlayServicesNotAvailableException
	{
		MapsInitializer.initialize(this);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);

	}
	
	/**
	 * This method simply place a dummy location marker on Map View. You can
	 * write your own logic for loading the locations and placing the marker for
	 * each location as per your need.
	 */
	private void setupMarker(String lat,String lot)
	{
		mMap.clear();
		
		LatLng l = new LatLng(Double.parseDouble(lat), Double.parseDouble(lot));
		MarkerOptions opt = new MarkerOptions();
		opt.position(l).title(title)
				.snippet(subtitle);
		opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

		mMap.addMarker(opt);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 16));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.socialshare.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.menu_share) {
			startActivity(new Intent(Intent.ACTION_DIAL,
					Uri.parse("tel:0123456789")));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.noti, menu);
		// menu.findItem(R.id.menu_sort).setVisible(false);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_share);

		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		Intent myIntent = new Intent();

		myIntent.setAction(Intent.ACTION_SEND);

		myIntent.putExtra(Intent.EXTRA_TEXT, title + " - " + subtitle);

		myIntent.setType("text/plain");

		mShareActionProvider.setShareIntent(myIntent);

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
}
