package com.pulego.mysafety.notification;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pulego.mysafety.MainActivity;
import com.pulego.mysafety.NotificationListActivity;
import com.pulego.mysafety.R;
import com.pulego.mysafety.db.DBHelper;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmMessageHandler extends IntentService {

	String mes;
	private Handler handler;

	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;

	public static DBHelper mydb;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new Handler();
		mydb = new DBHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		generateNotification(this, extras.getString("title"),
				extras.getString("message"), extras.getString("pictureurl"));

		Log.i("GCM",
				"Received : (" + messageType + ")  "
						+ extras.getString("pictureurl"));

		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	private static void generateNotification(Context context, String title,
			String message, String pictureurl) {

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();

		// log to db
		boolean isInserted = mydb
				.insertNotification(title,null, message, pictureurl,null);

		Intent notificationIntent = new Intent(context,
				NotificationListActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(message)
				.setContentIntent(contentIntent)
				.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
				.setLights(Color.GREEN, 3000, 3000)
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification noti = mBuilder.build();

		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mydb.close();
	}

}
