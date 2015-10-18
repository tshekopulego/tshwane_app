package com.pulego.mysafety.notification;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pulego.mysafety.CrimeReportDetail;
import com.pulego.mysafety.NotificationListActivity;
import com.pulego.mysafety.R;
import com.pulego.mysafety.db.DBHelper;
import com.pulego.mysafety.utils.CrimeReport;

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
		new Handler();
		mydb = new DBHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		String casenum = extras.getString("casenum");
		String status = extras.getString("status");
		String title = extras.getString("title");
		String message = extras.getString("message");
		String pictureurl = extras.getString("pictureurl");
		String uid = extras.getString("uid");

		if (title != null && message != null && casenum == null) {

			mydb.insertNotification(title, null, message, null, pictureurl);

			Intent notificationIntent = new Intent(this, NotificationListActivity.class);

			generateNotification(this, title, message, pictureurl, notificationIntent);

			Intent i = new Intent("notification_refreshed");

			sendBroadcast(i);

			Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("pictureurl"));

			GcmBroadcastReceiver.completeWakefulIntent(intent);

		} else if (casenum != null) {

			mydb.insertNotification(title, uid, message, messageType, null);

			mydb.updateCrimeReportStatus(uid, status,casenum);

			CrimeReport crimeReport = mydb.getCrimeReport(uid);

			if (crimeReport != null) {

				Intent notificationIntent = new Intent(this, CrimeReportDetail.class).putExtra("uid", crimeReport.uid)
						.putExtra("title", crimeReport.title).putExtra("description", crimeReport.description)
						.putExtra("status", crimeReport.status).putExtra("createdon", crimeReport.datetime)
						.putExtra("lat", crimeReport.lat).putExtra("lot", crimeReport.lot).putExtra("casenum", casenum);

				generateNotification(this, extras.getString("title") + " - " + extras.getString("casenum"), extras.getString("message"), null,
						notificationIntent);
			}

		}

	}

	private static void generateNotification(Context context, String title, String message, String pictureurl,
			Intent notificationIntent) {

		int icon = R.drawable.ic_launcher;
		System.currentTimeMillis();

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(icon)
				.setContentTitle(title).setContentText(message).setContentIntent(contentIntent)
				.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.GREEN, 3000, 3000)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification noti = mBuilder.build();

		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(NOTIFICATION_ID, noti);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mydb.close();
	}

}
