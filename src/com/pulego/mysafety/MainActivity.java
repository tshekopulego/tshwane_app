package com.pulego.mysafety;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.db.DBHelper;
import com.pulego.mysafety.errorhandler.ExceptionHandler;
import com.pulego.mysafety.model.Data;
import com.pulego.mysafety.ui.AboutList;
import com.pulego.mysafety.ui.EmergencyContact;
import com.pulego.mysafety.ui.LeftNavAdapter;
import com.pulego.mysafety.ui.MapViewer;
import com.pulego.mysafety.ui.NotificationList;
import com.pulego.mysafety.ui.ReportCrime;
import com.pulego.mysafety.ui.Survey;
import com.pulego.mysafety.utils.AlertDialogManager;
import com.pulego.mysafety.utils.ConnectionDetector;
import com.pulego.mysafety.utils.EasyRatingDialog;
import com.pulego.mysafety.utils.ServerUtilities;
import com.pulego.mysafety.R;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity is the base activity class of the application. This
 * activity is launched after the Splash and it holds all the Fragments used in
 * the app. It also creates the Navigation Drawer on left side.
 */
public class MainActivity extends CustomActivity {

	/** The drawer layout. */
	private DrawerLayout drawerLayout;

	/** ListView for left side drawer. */
	private ListView drawerLeft;

	/** The drawer toggle. */
	private ActionBarDrawerToggle drawerToggle;

	/** The left navigation list adapter. */
	private LeftNavAdapter adapter;

	private EasyRatingDialog easyRatingDialog;

	GoogleCloudMessaging gcm;
	String regid;
	String PROJECT_NUMBER = "600629729532";

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;

	// Connection detector
	ConnectionDetector cd;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	Editor editor;

	// give your server registration url here
	static String SERVER_URL; /// admin/registerdevice.php";

	static final String DISPLAY_MESSAGE_ACTION = "com.pulego.mysafety.DISPLAY_MESSAGE";

	private static final String SENDER_ID = "600629729532";

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	static final String TAG = "MySafety";

	DBHelper mydb;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		easyRatingDialog = new EasyRatingDialog(this);

		mydb = new DBHelper(CoSafetyApplication.getAppContext());

		setContentView(R.layout.activity_main);

		Bundle extras = getIntent().getExtras();

		setupContainer();

		setupDrawer();

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		

		if (!isInternetPresent) {
			showOffline(this);
			return;

		}

		editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();

		if (extras != null) {
			Boolean flag = extras.getBoolean("not_flag");

			if (flag) {
				launchFragment(3);
			}
		}

	}

	private void showOffline(Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

			// set title
			alertDialogBuilder.setTitle("Internet Connection Error");

			// set dialog message
			alertDialogBuilder
				.setMessage("Please enable your internet connection and try again!")
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						MainActivity.this.finish();
					}
				  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Setup the drawer layout. This method also includes the method calls for
	 * setting up the Left side drawer.
	 */
	private void setupDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				setActionBarTitle();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.app_name);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		drawerLayout.closeDrawers();

		setupLeftNavDrawer();
	}

	public void getRegId(final Context ctx) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
				String regid = prefs.getString("regId", "");

				// Check device for Play Services APK. If check succeeds,
				// proceed with GCM registration.
				if (checkPlayServices()) {
					gcm = GoogleCloudMessaging.getInstance(ctx);
					regid = getRegistrationId(ctx);

					if (regid.isEmpty()) {
						registerInBackground(ctx);
					}
				} else
					Log.i(TAG, "No valid Google Play Services APK found.");

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

			}
		}.execute(null, null, null);
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String registrationId = prefs.getString("regId", "");

		return registrationId;
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
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
					sendRegistrationIdToBackend(regid, "");

					// Persist the regID - no need to register again.
					storeRegistrationId(regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(TAG, msg + "\n");
			}
		}.execute(null, null, null);
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

		SERVER_URL = CoSafetyApplication.serverURL() + "/dashboard/registerdevice.php";

		try {
			ServerUtilities.post(SERVER_URL, newparams);
		} catch (IOException e) {
			Log.i(TAG, e.toString() + "\n");
		}
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 */
	private void storeRegistrationId(String regId) {
		editor.putString("regId", regId);
		editor.putString("phoneNumber", "");
		editor.commit();

	}

	/**
	 * Setup the left navigation drawer/slider. You can add your logic to load
	 * the contents to be displayed on the left side drawer. You can also setup
	 * the Header and Footer contents of left drawer if you need them.
	 */
	private void setupLeftNavDrawer() {
		drawerLeft = (ListView) findViewById(R.id.left_drawer);

		adapter = new LeftNavAdapter(this, getDummyLeftNavItems());
		drawerLeft.setAdapter(adapter);
		drawerLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				drawerLayout.closeDrawers();
				launchFragment(pos);
			}
		});

	}

	/**
	 * This method returns a list of dummy items for left navigation slider. You
	 * can write or replace this method with the actual implementation for list
	 * items.
	 * 
	 * @return the dummy items
	 */
	private ArrayList<Data> getDummyLeftNavItems() {
		ArrayList<Data> al = new ArrayList<Data>();
		al.add(new Data("Report Incident", R.drawable.report, R.drawable.report));
		al.add(new Data("Nearby Services", R.drawable.nearby_ser, R.drawable.nearby_ser));
		al.add(new Data("Emergency Contacts", R.drawable.emergency_cont, R.drawable.emergency_cont));
		//al.add(new Data("Case History", R.drawable.case_history, R.drawable.case_history));
		al.add(new Data("Notifications", R.drawable.not_, R.drawable.not_));
		al.add(new Data("Survey", R.drawable.survey, R.drawable.survey));
		al.add(new Data("About", R.drawable.about, R.drawable.about));
		return al;

	}

	/**
	 * This method can be used to attach Fragment on activity view for a
	 * particular tab position. You can customize this method as per your need.
	 * 
	 * @param pos
	 *            the position of tab selected.
	 */
	private void launchFragment(int pos) {
		Fragment f = null;
		String title = null;
		if (pos == 0) {
			title = "Report Incident";
			f = new ReportCrime();
		} else if (pos == 1) {
			title = "Nearby Services";
			f = new MapViewer();
		} else if (pos == 2) {
			title = "Emergency Contacts";
			f = new EmergencyContact();
		} 
		/*else if (pos == 3) {
			title = "Crime History Report";
			f = new CrimeReportList();
		} */
		else if (pos == 3) {
			title = "Notifications";
			f = new NotificationList();
		} else if (pos == 4) {
			title = "Survey";
			f = new Survey();

		} else if (pos == 5) {
			title = "About";
			f = new AboutList();
		}

		if (f != null) {
			while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				getSupportFragmentManager().popBackStackImmediate();
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(title)
					.commit();
			if (adapter != null)
				adapter.setSelection(pos);
		}
	}

	/**
	 * Setup the container fragment for drawer layout. The current
	 * implementation of this method simply calls launchFragment method for tab
	 * position 0. You can customize this method as per your need to display
	 * specific content.
	 */
	private void setupContainer() {
		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {

			@Override
			public void onBackStackChanged() {
				setActionBarTitle();
			}
		});
		launchFragment(0);
	}

	/**
	 * Set the action bar title text.
	 */
	@SuppressLint("NewApi")
	private void setActionBarTitle() {

		if (drawerLayout == null) {
			setupDrawer();
		}

		if (drawerLayout.isDrawerOpen(drawerLeft)) {
			getActionBar().setTitle(R.string.app_name);
			return;
		}
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			return;
		String title = getSupportFragmentManager()
				.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		getActionBar().setTitle(title);
		getActionBar().setLogo(R.drawable.icon);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.
	 * Configuration )
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.newsfeeder.custom.CustomActivity#onOptionsItemSelected(android.view
	 * .MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			// getSupportFragmentManager().popBackStackImmediate();
			// } else
			// finish();

			moveTaskToBack(true);
			MainActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		easyRatingDialog.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		easyRatingDialog.showIfNeeded();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mydb.close();
	}

}
