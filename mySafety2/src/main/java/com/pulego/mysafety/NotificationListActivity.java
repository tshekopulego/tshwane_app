package com.pulego.mysafety;

import android.os.Bundle;
import android.view.MenuItem;

import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.ui.NotificationList;
import com.pulego.mysafety.R;

/**
 * The SearchResultActivity is the activity class that shows a dummy list of
 * property search results. You need to write your own code to load and display
 * actual search results.
 */
public class NotificationListActivity extends CustomActivity
{
	/* (non-Javadoc)
	 * @see com.food.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setTitle("Notifications");

		addFragment();
	}

	/**
	 * Attach the appropriate SearchResult fragment with activity and also
	 * passes the bundle with fragment based on the 'buy' parameter in Intent to
	 * differentiate between Buy and Rent result listing. You may need to pass
	 * additional parameters based on your needs.
	 */
	private void addFragment()
	{
		CustomFragment f = new NotificationList();
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f).commit();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
