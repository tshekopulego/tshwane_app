package com.pulego.mysafety;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.utils.ServerUtilities;
import com.pulego.mysafety.R;

/**
 * The Class PropertyDetail is the Activity class that is launched when the user
 * clicks on an item in Feed list or in Search results list. It simply shows
 * dummy details of a property with dummy image of property and also includes a
 * Google Map view. You need to write your own code to load and display actual
 * contents.
 */
public class NotificationDetail extends CustomActivity {

	private ShareActionProvider mShareActionProvider;

	String subtitle;
	String title;
	String pictureurl;
	String datetime;
	String duration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realestate.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_detail);

		getActionBar().setTitle("Notification Detail");

		Bundle extras = getIntent().getExtras();
		title = extras.getString("title");
		subtitle = extras.getString("message");
		
		pictureurl = extras.getString("pictureurl");
		datetime = extras.getString("datetime");

		TextView titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(title);

		TextView subtitleTextView = (TextView) findViewById(R.id.subTitle);
		subtitleTextView.setText(subtitle);
		
		TextView datetimeTextView = (TextView) findViewById(R.id.duration);
		datetimeTextView.setText(datetime);
		

		SmartImageView img = (SmartImageView) findViewById(R.id.loaderImageView);
		img.setImageUrl(pictureurl);
		
		ServerUtilities.getTimeDifference(datetime, datetimeTextView);

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
