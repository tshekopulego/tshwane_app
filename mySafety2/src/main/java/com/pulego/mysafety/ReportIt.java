package com.pulego.mysafety;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pulego.mysafety.utils.ReportType;
import com.pulego.mysafety.utils.AlertDialogManager;
import com.pulego.mysafety.utils.AsyncTaskResultEvent;
import com.pulego.mysafety.utils.AsyncTaskSubmitReportResultEvent;
import com.pulego.mysafety.utils.ConnectionDetector;
import com.pulego.mysafety.utils.GPSTracker;
import com.pulego.mysafety.utils.MyBus;
import com.pulego.mysafety.utils.SubmitReportTaskAsyncTask;
import com.pulego.mysafety.custom.CustomActivity;
import com.pulego.mysafety.db.DBHelper;
import com.squareup.otto.Subscribe;
import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureResolution;

/**
 * The Activity Edit is launched when you taps on main image or on check mark
 * icon in MainActivity. It simply shows a dummy image with some filter options
 * and option for sharing the image. You need to write actual code image
 * processing and filtering.
 * 
 */
public class ReportIt extends CustomActivity {

	private String mCurrentPhotoPath = "";

	private String mCurrentAudioPath = "";

	private String mCurrentVideoPath = "";

	private String mCurrentPhotoFile;

	private ImageView mImageView;

	private ImageView thumbnailIv;

	private LinearLayout pictureadd;

	private Button startstop;

	private Button play;

	private TextView descTextView;

	private TextView mobileTextView;

	private TextView locationTextView;

	private TextView categoryTextView;

	private CheckBox anon;

	private TextView edName;

	private Spinner categoryType;

	private static final String JPEG_FILE_PREFIX = "IMG_";

	private static final String AUDIO_FILE_PREFIX = "AUD_";

	private static final String VIDEO_FILE_PREFIX = "VID_";

	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private static final String VIDEO_FILE_SUFFIX = ".mp4";

	private static final String AUDIO_FILE_SUFFIX = ".wav";

	// Progress Dialog
	private ProgressDialog pDialog;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	private Context ctx;

	// Connection detector class
	ConnectionDetector cd;

	private MediaRecorder myAudioRecorder;

	private File mediaFile;

	private MediaPlayer mp;

	private Boolean isStarted = false;

	public static final int SELECT_PHOTO = 100;
	public static final int CAPTURE_IMAGE = 101;

	private Uri fileUri;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_VIDEO = 200;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// GPS Location
	GPSTracker gps;

	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "MySafety";

	ReportType[] lList;

	Bitmap bitmap;

	String encodedString;
	String encodedAudioString;
	String encodedVideoString;

	RequestParams params = new RequestParams();
	
	DBHelper mydb;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.newsfeeder.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.reportit);

		getActionBar().setTitle("Report It");

		this.ctx = this;

		Bundle extras = getIntent().getExtras();

		String category = extras.getString("category");

		int categoryNum = extras.getInt("categoryNum");

		anon = (CheckBox) findViewById(R.id.anon);
		anon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (anon.isChecked()) {
					mobileTextView.setVisibility(View.GONE);
					edName.setVisibility(View.GONE);
				} else {
					mobileTextView.setVisibility(View.VISIBLE);
					edName.setVisibility(View.VISIBLE);
				}
			}
		});

		categoryTextView = (TextView) findViewById(R.id.crimeCategory);
		categoryTextView.setText(category);
		categoryTextView.setTag(String.valueOf(categoryNum));

		descTextView = (TextView) findViewById(R.id.edDescription);
		locationTextView = (TextView) findViewById(R.id.edLocation);
		mobileTextView = (TextView) findViewById(R.id.edMobileNo);
		edName = (TextView) findViewById(R.id.edName);
		thumbnailIv = (ImageView) findViewById(R.id.iv_thumbnail);

		startstop = (Button) findViewById(R.id.start);

		play = (Button) findViewById(R.id.play);

		pictureadd = (LinearLayout) findViewById(R.id.pictureadd);

		mImageView = (ImageView) findViewById(R.id.photo);

		categoryType = (Spinner) findViewById(R.id.area);

		categorySpinner(categoryNum);

		mCurrentAudioPath = "";

		cd = new ConnectionDetector(ctx);

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);

			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setCancelable(true);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("Please connect to working Internet connection.");
			builder.setInverseBackgroundForced(true);
			builder.setNegativeButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(ctx, MainActivity.class));
						}
					});

			AlertDialog alert = builder.create();
			alert.show();

		}

		// creating GPS Class object
		gps = new GPSTracker(ctx);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your are here", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(ctx, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return;
		}

		final Button captureBtn = (Button) findViewById(R.id.videostart);
		captureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startVideoCaptureActivity();

			}
		});

		final Button playBtn = (Button) findViewById(R.id.videoplay);
		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				playVideo();

			}
		});

		// setTouchNClick(R.id.anon);
		setTouchNClick(R.id.pictureadd);
		setTouchNClick(R.id.start);
		setTouchNClick(R.id.play);
		setTouchNClick(R.id.videorec);
		setTouchNClick(R.id.btnSubmit);

		setupActionBar();

		MyBus.getInstance().register(this);

	}

	private void startVideoCaptureActivity() {
		final CaptureConfiguration config = createCaptureConfiguration();

		// Create an video file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String videoFileName = VIDEO_FILE_PREFIX + timeStamp
				+ VIDEO_FILE_SUFFIX;

		mCurrentVideoPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MySafety/" + videoFileName;

		int index = mCurrentVideoPath.lastIndexOf("/");
		String currentVideoPath = mCurrentVideoPath.substring(index + 1);
		params.put("filename", currentVideoPath);

		final String filename = mCurrentVideoPath;

		final Intent intent = new Intent(this.ctx, VideoCaptureActivity.class);
		intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION,
				config);
		intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
		startActivityForResult(intent, CAPTURE_VIDEO);
	}

	private CaptureConfiguration createCaptureConfiguration() {
		final CaptureResolution resolution = getResolution(2); // 360p
		final CaptureQuality quality = getQuality(2); // Low
		int fileDuration = CaptureConfiguration.NO_DURATION_LIMIT;
		try {
			fileDuration = Integer.valueOf(2000);
		} catch (final Exception e) {
			// NOP
		}
		int filesize = CaptureConfiguration.NO_FILESIZE_LIMIT;
		try {
			filesize = Integer.valueOf(5000);
		} catch (final Exception e2) {
			// NOP
		}
		final CaptureConfiguration config = new CaptureConfiguration(
				resolution, quality, fileDuration, filesize);
		return config;
	}

	private CaptureResolution getResolution(int position) {
		final CaptureResolution[] resolution = new CaptureResolution[] {
				CaptureResolution.RES_1080P, CaptureResolution.RES_720P,
				CaptureResolution.RES_480P, CaptureResolution.RES_360P,
				CaptureResolution.RES_1440P };
		return resolution[position];
	}

	private CaptureQuality getQuality(int position) {
		final CaptureQuality[] quality = new CaptureQuality[] {
				CaptureQuality.HIGH, CaptureQuality.MEDIUM, CaptureQuality.LOW };
		return quality[position];
	}

	private int getCategoryResourceId(int categoryNum) {

		int resId = 0;// = R.array.contactcrime;

		switch (categoryNum) {
		case 1:

			resId = R.array.contactcrime;
			ReportType sArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Murder", "1"),
					new ReportType("Sexual Crimes", "2"),
					new ReportType("Assault", "3"),
					new ReportType("Robbery", "4"),
					new ReportType("Child Abuse and Neglect", "5") };
			// convert array to list
			lList = sArray;
			break;
		case 2:

			resId = R.array.drugs;
			ReportType sDrugArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Drug Possession", "6"),
					new ReportType("Drug Sales", "7"),
					new ReportType("Illegal Gun or Ammunition", "8"),
					new ReportType("Gun Firing", "9") };
			// convert array to list
			lList = sDrugArray;
			break;
		case 3:

			resId = R.array.corruption;
			ReportType sCorruptionArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Bribery", "10"),
					new ReportType("Fraud", "11"),
					new ReportType("Misuse of public property", "12") };
			// convert array to list
			lList = sCorruptionArray;
			break;
		case 4:

			resId = R.array.property;
			ReportType sPropertyArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Burglary", "13"),
					new ReportType("Arson", "14"),
					new ReportType("Vandalism", "15"),
					new ReportType("Malicious Damage", "16"),
					new ReportType("Theft", "17") };
			// convert array to list
			lList = sPropertyArray;
			break;
		case 5:

			resId = R.array.vehicle;
			ReportType sVehicleArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Theft", "18"),
					new ReportType("Hijacking", "19"),
					new ReportType("Breaking", "20") };
			// convert array to list
			lList = sVehicleArray;
			break;
		case 6:

			resId = R.array.protest;
			ReportType sProtestArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Protest", "21"),
					new ReportType("Violent Protest", "22") };
			// convert array to list
			lList = sProtestArray;
			break;
		case 7:

			resId = R.array.traffic;
			ReportType sTrafficArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Accident", "0"),
					new ReportType("Traffic Lights", "23"),
					new ReportType("Driving under influence", "24") };
			// convert array to list
			lList = sTrafficArray;
			break;
		case 8:

			resId = R.array.bylaw;
			ReportType sBylawArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Public Amenities", "25"),
					new ReportType("Public Order and Public Places", "26"),
					new ReportType("Street Trading", "27"),
					new ReportType("Electricity / Water", "28"),
					new ReportType("Other", "29") };
			// convert array to list
			lList = sBylawArray;
			break;

		case 9:

			resId = R.array.other;
			ReportType sOtherArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Other", "30") };
			// convert array to list
			lList = sOtherArray;
			break;

		case 10:

			resId = R.array.test;
			ReportType sTestArray[] = new ReportType[] {
					new ReportType("Select Type", "0"),
					new ReportType("Test", "31") };
			// convert array to list
			lList = sTestArray;
			break;
		}

		return resId;
	}

	public void categorySpinner(int categorynr) {

		getCategoryResourceId(categorynr);

		ArrayAdapterItem Adapter = new ArrayAdapterItem(this,
				R.layout.report_item, lList);

		categoryType.setAdapter(Adapter);

		categoryType
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	}

	@Override
	protected void onDestroy() {
		// MyBus.getInstance().unregister(this);
		super.onDestroy();

	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		File mediaStorageDir = createFolderIfNotExists();

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	private static File createFolderIfNotExists() {
		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}
		return mediaStorageDir;
	}

	/**
	 * ------------ Helper Methods ----------------------
	 * */

	/*
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * Recording video
	 */
	private void recordVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

		// set video quality
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
															// name

		// Create an video file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String videoFileName = VIDEO_FILE_PREFIX + timeStamp
				+ VIDEO_FILE_SUFFIX;

		createFolderIfNotExists();

		mCurrentVideoPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MySafety/" + videoFileName;

		int index = mCurrentVideoPath.lastIndexOf("/");
		String currentVideoPath = mCurrentVideoPath.substring(index + 1);
		params.put("filename", currentVideoPath);

		// start the video capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
	}

	protected void SetupAudio() {

		// Create an video file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String audioFileName = AUDIO_FILE_PREFIX + timeStamp
				+ AUDIO_FILE_SUFFIX;

		mCurrentAudioPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MySafety/" + audioFileName;
		Log.d("MySafety", mCurrentAudioPath);

		int index = mCurrentAudioPath.lastIndexOf("/");
		String currentAudioPath = mCurrentAudioPath.substring(index + 1);
		params.put("filename", currentAudioPath);

		// Check that the SDCard is mounted
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "MySafety");

		// Create the storage directory(MySafety) if it does not exist
		if (!mediaStorageDir.exists()) {

			if (!mediaStorageDir.mkdirs()) {

				Log.d("MySafety", "Failed to create directory MySafety.");
			}
		}

		if (myAudioRecorder == null)
			myAudioRecorder = new MediaRecorder();

		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(mCurrentAudioPath);
	}

	/**
	 * This method will setup the top title bar (Action bar) content and display
	 * values. It will also setup the custom background theme for ActionBar. You
	 * can override this method to change the behavior of ActionBar for
	 * particular Activity
	 */
	protected void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Report It");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.icon);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#549e3b"));
		actionBar.setBackgroundDrawable(colorDrawable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.reportitmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {

		case android.R.id.home:

			Intent intentHome = new Intent(this, MainActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);

			break;

		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("requestCode", String.valueOf(SELECT_PHOTO));
		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Uri selectedImage = data.getData();

					mCurrentPhotoPath = getPath(selectedImage, this.ctx);

					Log.i("mCurrentPhotoPath", mCurrentPhotoPath);

					int index = mCurrentPhotoPath.lastIndexOf("/");
					mCurrentPhotoFile = mCurrentPhotoPath.substring(index + 1);
					params.put("filename", mCurrentPhotoFile);

					handleBigCameraPhoto();

				} catch (Throwable e) {
					e.printStackTrace();
					Toast.makeText(this.ctx,
							getResources().getString(R.string.error),
							Toast.LENGTH_SHORT).show();
				}
			} else
				mCurrentPhotoPath = "";
			break;
		case CAPTURE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					handleBigCameraPhoto();
				} catch (Throwable e) {
					e.printStackTrace();
					Toast.makeText(this,
							getResources().getString(R.string.error),
							Toast.LENGTH_SHORT).show();
				}
			} else
				mCurrentPhotoPath = "";
			break;
		case CAPTURE_VIDEO:
			if (resultCode == RESULT_OK) {
				// Video captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Video saved", Toast.LENGTH_LONG).show();

				updateStatusAndThumbnail();

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
				Toast.makeText(this, "User cancelled the video capture.",
						Toast.LENGTH_LONG).show();

				mCurrentVideoPath = "";

			} else {
				// Video capture failed, advise user
				Toast.makeText(this, "Video capture failed.", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	private void updateStatusAndThumbnail() {

		final Bitmap thumbnail = getThumbnail();

		if (thumbnail != null) {
			thumbnailIv.setImageBitmap(thumbnail);
		} else {
			thumbnailIv.setImageResource(R.drawable.video);
		}
	}

	private Bitmap getThumbnail() {
		if (mCurrentVideoPath == null)
			return null;
		return ThumbnailUtils.createVideoThumbnail(mCurrentVideoPath,
				Thumbnails.FULL_SCREEN_KIND);
	}

	public static String getPath(Uri uri, Context context) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		String filePath = null;

		try {
			Cursor cursor = context.getContentResolver().query(uri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			filePath = cursor.getString(columnIndex);
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
			filePath = uri.getPath();
		}

		if (filePath == null) {
			filePath = uri.getPath();
		}

		return filePath;
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			// mCurrentPhotoPath = null;
		}

	}

	public void playVideo() {
		if (mCurrentVideoPath == null)
			return;

		final Intent videoIntent = new Intent(Intent.ACTION_VIEW);
		videoIntent.setDataAndType(Uri.parse(mCurrentVideoPath), "video/*");
		try {
			startActivity(videoIntent);
		} catch (ActivityNotFoundException e) {
			// NOP
		}
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);

	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		Log.i("Photo", mCurrentPhotoPath);

		String fileNameSegments[] = mCurrentPhotoPath.split("/");
		String fileName = fileNameSegments[fileNameSegments.length - 1];
		// Put file name in Async Http Post Param which will used in Php web app
		params.put("filename", fileName);
		this.sendBroadcast(mediaScanIntent);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		if (v.getId() == R.id.anon) {

			if (anon.isChecked())
				mobileTextView.setVisibility(View.GONE);

			else
				mobileTextView.setVisibility(View.VISIBLE);

		} else if (v.getId() == R.id.pictureadd) {

			mImageView = (ImageView) findViewById(R.id.photo);
			attachImage();

		} else if (v.getId() == R.id.start) {

			Log.i("MySafety", isStarted.toString());

			if (isStarted == false) {
				SetupAudio();
				startRecording();
				startstop.setText("stop");
				isStarted = true;
			} else {
				startstop.setText("start");
				stopRecording();
				isStarted = false;

			}
		} else if (v.getId() == R.id.play) {

			Log.i("MySafety", isStarted.toString());
			playRecording();

		} else if (v.getId() == R.id.videorec) {

			Log.i("MySafety", isStarted.toString());

			// record video
			recordVideo();

		} else if (v.getId() == R.id.btnSubmit) {

			if (!anon.isChecked()) {

				if (mobileTextView.getText().toString().trim().equals("")) {
					// Toast.makeText(getApplicationContext(),
					// "contact number is required!", Toast.LENGTH_LONG)
					// .show();

					mobileTextView.setError("contact number is required!");
					return;

				}

				if (edName.getText().toString().trim().equals("")) {

					// Toast.makeText(getApplicationContext(),
					// "Name is required!", Toast.LENGTH_LONG).show();

					edName.setError("name is required!");
					return;
				}

			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you currently at the crime scene?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();

		}

	}

	private void submitCrimeReport(boolean isAtCrimeScene) {
		pDialog = new ProgressDialog(ctx);
		pDialog.setMessage("Please wait while we submit your report..");

		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.setIcon(R.drawable.ic_launcher);

		pDialog.show();

		if (mCurrentPhotoPath != "") {

			encodeImagetoString(mCurrentPhotoPath);

			Log.i("Upload", mCurrentPhotoPath);
		}

		if (mCurrentAudioPath != "") {

			encodedAudiotoString(mCurrentAudioPath);

			Log.i("Upload", mCurrentAudioPath);
		}

		if (mCurrentVideoPath != "") {

			encodedVideotoString(mCurrentVideoPath);

			Log.i("Upload", mCurrentVideoPath);
		}

		String[] params = new String[13];
		params[0] = descTextView.getText().toString();
		params[1] = categoryTextView.getText().toString();

		if (isAtCrimeScene == true) {
			params[2] = String.valueOf(gps.getLatitude());
			params[3] = String.valueOf(gps.getLongitude());
		} else if (isAtCrimeScene == false) {
			params[2] = "";
			params[3] = "";
		}

		params[4] = "mobileapp";
		params[5] = locationTextView.getText().toString();
		params[6] = mobileTextView.getText().toString();

		String mCurrentAudioFile;
		int index = mCurrentAudioPath.lastIndexOf("/");
		if (index > 0) {
			mCurrentAudioFile = mCurrentAudioPath.substring(index + 1);

			params[7] = mCurrentAudioFile;
		} else
			params[7] = "";

		if (mCurrentPhotoFile != null)
			params[8] = mCurrentPhotoFile;
		else
			params[8] = "";

		String mCurrentVideoFile;
		index = mCurrentVideoPath.lastIndexOf("/");
		if (index > 0) {
			mCurrentVideoFile = mCurrentVideoPath.substring(index + 1);

			params[9] = mCurrentVideoFile;
		} else
			params[9] = "";

		params[10] = ((ReportType) categoryType.getSelectedItem()).getName();

		params[11] = edName.getText().toString();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		params[12] = prefs.getString("regId", "");

		new SubmitReportTaskAsyncTask().execute(params);
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				submitCrimeReport(true);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				submitCrimeReport(false);
				break;
			}
		}
	};

	protected void startRecording() {
		try {
			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// startstop.setEnabled(false);
		// stop.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Recording started",
				Toast.LENGTH_LONG).show();
	}

	protected void stopRecording() {
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		// startstop.setEnabled(false);
		play.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_LONG).show();
	}

	protected void playRecording() {

		try {
			mp = new MediaPlayer();
			mp.reset();
			mp.setDataSource(mCurrentAudioPath);
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	protected void startRecordingVideo() {
		// create new Intentwith with Standard Intent action that can be
		// sent to have the camera application capture an video and return it.
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		// create a file to save the video
		fileUri = Uri.fromFile(mediaFile);

		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// set the video image quality to high
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

		// start the Video Capture Intent
		startActivityForResult(intent, CAPTURE_VIDEO);
	}

	@Subscribe
	public void onAsyncTaskResult(AsyncTaskResultEvent event) {
		Log.i("Results", event.getResult().toString());

	}

	// AsyncTask - To convert Image to String
	public void encodeImagetoString(final String imgPath) {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {

			};

			@Override
			protected String doInBackground(Void... params) {
				BitmapFactory.Options options = null;
				options = new BitmapFactory.Options();
				options.inSampleSize = 3;
				bitmap = BitmapFactory.decodeFile(imgPath, options);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// Must compress the Image to reduce image size to make upload
				// easy
				bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
				byte[] byte_arr = stream.toByteArray();
				// Encode Image to String
				encodedString = Base64.encodeToString(byte_arr, 0);
				Log.i("encodedString", encodedString);
				return "";
			}

			@Override
			protected void onPostExecute(String msg) {
				pDialog = new ProgressDialog(ctx);
				pDialog.setMessage("Please wait ...");
				// Put converted Image string into Async Http Post param
				params.put("image", encodedString);

				// Trigger Image upload
				makeHTTPCall("http://test.tshwanesafety.co.za/dashboard/uploader.php");
			}
		}.execute(null, null, null);
	}

	// AsyncTask - To convert Image to String
	public void encodedAudiotoString(final String audioPath) {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {

			};

			@Override
			protected String doInBackground(Void... params) {

				FileInputStream fin = null;
				try {
					fin = new FileInputStream(audioPath);
				} catch (FileNotFoundException e) {
					Log.i("encodedAudiotoString", e.toString() + "\n");
				}
				BufferedInputStream bis = new BufferedInputStream(fin);
				DataInputStream dis = new DataInputStream(bis);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					copy(dis, out);
				} catch (IOException e) {
					Log.i("encodedAudiotoString", e.toString() + "\n");
				}
				byte fileContent[] = out.toByteArray();
				encodedAudioString = Base64.encodeToString(fileContent, 0);
				return "";

			}

			@Override
			protected void onPostExecute(String msg) {
				pDialog = new ProgressDialog(ctx);
				pDialog.setMessage("Please wait ...");
				// Put converted Image string into Async Http Post param
				params.put("audio", encodedAudioString);
				// Trigger Image upload
				makeHTTPCall("http://test.tshwanesafety.co.za/dashboard/uploaderaudio.php");
			}
		}.execute(null, null, null);
	}

	public void encodedVideotoString(final String videoPath) {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {

			};

			@Override
			protected String doInBackground(Void... params) {

				FileInputStream fin = null;
				try {
					fin = new FileInputStream(videoPath);
				} catch (FileNotFoundException e) {
					Log.i("encodedAudiotoString", e.toString() + "\n");
				}
				BufferedInputStream bis = new BufferedInputStream(fin);
				DataInputStream dis = new DataInputStream(bis);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					copy(dis, out);
				} catch (IOException e) {
					Log.i("encodedAudiotoString", e.toString() + "\n");
				}
				byte fileContent[] = out.toByteArray();
				encodedVideoString = Base64.encodeToString(fileContent, 0);
				return "";

			}

			@Override
			protected void onPostExecute(String msg) {
				pDialog = new ProgressDialog(ctx);
				pDialog.setMessage("Please wait ...");
				// Put converted Image string into Async Http Post param
				params.put("video", encodedVideoString);
				// Trigger Image upload
				makeHTTPCall("http://test.tshwanesafety.co.za/dashboard/uploadervideo.php");
			}
		}.execute(null, null, null);
	}

	private static long copy(InputStream from, OutputStream to)
			throws IOException {

		byte[] buf = new byte[4000];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1)
				break;

			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}

	public void makeHTTPCall(String Url) {

		pDialog.setMessage("Uploading...");

		AsyncHttpClient client = new AsyncHttpClient();
		client.setMaxConnections(1);

		client.post(Url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				pDialog.hide();
				Log.i("response", response + "\n");
				Toast.makeText(getApplicationContext(), response,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// Hide Progress Dialog
				pDialog.hide();
				Log.i("statusCode", statusCode + "\n");
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(),
							"Requested resource not found", Toast.LENGTH_LONG)
							.show();
					Log.i("error", error + "\n");

				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(),
							"Something went wrong at server end",
							Toast.LENGTH_LONG).show();
					Log.i("error", error + "\n");
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							getApplicationContext(),
							error
									+ " Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
									+ statusCode, Toast.LENGTH_LONG).show();
					Log.i("error", error + "\n");
				}
			}
		});
	}

	@Subscribe
	public void onAsyncTaskSubmitReportResult(
			AsyncTaskSubmitReportResultEvent event) {

		Log.i("Results", event.getResult().toString());

		pDialog.dismiss();

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setCancelable(true);
		builder.setTitle("Report It");
		builder.setMessage("Thank you for your report. The severity of the evidence provided will prompt the monitoring officer to contact you, if necessary.");
		builder.setInverseBackgroundForced(true);
		builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				startActivity(new Intent(ctx, MainActivity.class));
			}
		});
		builder.setPositiveButton("Dial 10111",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:10111"));
						startActivity(intent);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public class CustomAdapter extends BaseAdapter {

		private String[] mValues;

		public CustomAdapter(Context context, String[] values) {
			mValues = values;
		}

		public int getCount() {
			return mValues.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// Override this method according to your need
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup viewGroup) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(android.R.layout.simple_spinner_item,
					viewGroup, false);
			TextView v = (TextView) row.findViewById(android.R.id.text1);
			v.setText(mValues[position]);
			return row;
		}
	}

	public void attachImage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);
		builder.setItems(R.array.attach_options,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) { // take picture
							Intent captureIntent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);

							// Create an image file name
							String timeStamp = new SimpleDateFormat(
									"yyyyMMdd_HHmmss").format(new Date());
							String imageFileName = JPEG_FILE_PREFIX + timeStamp
									+ JPEG_FILE_SUFFIX;

							File f = new File(Environment
									.getExternalStorageDirectory()
									+ "/MySafety/", imageFileName);
							mCurrentPhotoFile = imageFileName;
							mCurrentPhotoPath = f.getAbsolutePath();

							if (!f.exists()) {
								try {
									f.getParentFile().mkdirs();
									f.createNewFile();

								} catch (IOException e) {

								}
							}

							captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(f));
							startActivityForResult(captureIntent, CAPTURE_IMAGE);
						} else if (item == 1) { // attach picture
							Intent photoPickerIntent = new Intent(
									Intent.ACTION_PICK);
							photoPickerIntent.setType("image/*");
							startActivityForResult(Intent.createChooser(
									photoPickerIntent, "Select Picture"),
									SELECT_PHOTO);
						}
					}
				});

		builder.create().show();
	}

	class ArrayAdapterItem extends ArrayAdapter<ReportType> {

		Context mContext;
		int layoutResourceId;
		ReportType data[] = null;

		public ArrayAdapterItem(Context mContext, int layoutResourceId,
				ReportType[] data) {

			super(mContext, layoutResourceId, data);

			this.layoutResourceId = layoutResourceId;
			this.mContext = mContext;
			this.data = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				// inflate the layout
				LayoutInflater inflater = ((Activity) mContext)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutResourceId, parent, false);
			}

			// object item based on the position
			ReportType objectItem = data[position];

			// get the TextView and then set the text (item name) and tag (item
			// ID) values
			TextView textViewItem = (TextView) convertView
					.findViewById(R.id.typename);
			textViewItem.setText(objectItem.getName());
			textViewItem.setTag(objectItem.getId());

			return convertView;

		}

	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mydb.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mydb = new DBHelper(CoSafetyApplication.getAppContext());
	}


}
