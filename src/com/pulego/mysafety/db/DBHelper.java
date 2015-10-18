package com.pulego.mysafety.db;

import java.util.ArrayList;

import com.pulego.mysafety.utils.CrimeReport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Tsheko.Mashego
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "CoSafety.db";
	public static final String CONTACTS_TABLE_NAME = "notification";
	public static final String CONTACTS_COLUMN_ID = "id";
	public static final String CONTACTS_COLUMN_TITLE = "title";
	public static final String CONTACTS_COLUMN_MESSAGE = "message";
	public static final String CONTACTS_COLUMN_PICTUREURL = "pictureurl";
	public static final String CONTACTS_COLUMN_NOTIFICATIONTYPE = "notificationtype";
	public static final String CONTACTS_COLUMN_CREATEDON = "createdon";

	public static final String CRIMEREPORT_TABLE_NAME = "crimereport";
	public static final String CRIMEREPORT_COLUMN_ID = "id";
	public static final String CRIMEREPORT_COLUMN_TITLE = "title";
	public static final String CRIMEREPORT_COLUMN_UID = "uid";
	public static final String CRIMEREPORT_COLUMN_DESC = "description";
	public static final String CRIMEREPORT_COLUMN_IMAGEURL = "imageurl";
	public static final String CRIMEREPORT_COLUMN_VIDEOURL = "videourl";
	public static final String CRIMEREPORT_COLUMN_AUDIOURL = "audiourl";
	public static final String CRIMEREPORT_COLUMN_LAT = "lat";
	public static final String CRIMEREPORT_COLUMN_LOT = "lot";
	public static final String CRIMEREPORT_COLUMN_STATUS = "status";
	public static final String CRIMEREPORT_COLUMN_CREATEDON = "createdon";
	public static final String CRIMEREPORT_COLUMN_CASENUM = "casenum";

	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table notification "
				+ "(id INTEGER primary key,title TEXT,uid TEXT,message TEXT,pictureurl TEXT,notificationtype TEXT,createdon DATETIME DEFAULT CURRENT_TIMESTAMP)");

		db.execSQL("create table crimereport "
				+ "(id INTEGER primary key, title TEXT,uid TEXT,description TEXT,imageurl TEXT,audiourl TEXT,videourl TEXT,lat TEXT,lot TEXT,status TEXT,createdon DATETIME DEFAULT CURRENT_TIMESTAMP,casenum TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS notification");
		db.execSQL("DROP TABLE IF EXISTS crimereport");
		onCreate(db);
	}

	public boolean insertNotification(String title, String uid, String message, String type, String pictureurl) {

		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}

		db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("title", title);
		contentValues.put("uid", uid);
		contentValues.put("message", message);
		contentValues.put("pictureurl", pictureurl);
		contentValues.put("notificationtype", type);
		db.insert("notification", null, contentValues);
		db.close();

		return true;
	}

	public boolean insertCrimeReport(String uid, String title, String message, String imageurl, String audiourl,
			String videourl, String lat, String lot, String status, String casenum) {

		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}

		db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("uid", uid);
		contentValues.put("title", title);
		contentValues.put("description", message);
		contentValues.put("imageurl", imageurl);
		contentValues.put("audiourl", audiourl);
		contentValues.put("videourl", videourl);
		contentValues.put("lat", lat);
		contentValues.put("lot", lot);
		contentValues.put("status", status);
		contentValues.put("casenum", casenum);
		db.insert("crimereport", null, contentValues);
		db.close();

		return true;
	}

	public Cursor getData(int id) {
		db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from notification where id=" + id + "", null);
		return res;
	}

	public Cursor getCrimeReportData(int uid) {
		db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from crimereport where uid=" + uid + "", null);
		return res;
	}

	public int numberOfRows() {
		db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
		return numRows;
	}
	
	public boolean updateCrimeReportStatus(String uid, String status, String casenum) {
		db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		contentValues.put("casenum", casenum);
		
		db.update("crimereport", contentValues, "uid = ?", new String[] { uid });
		return true;
	}

	public boolean updateNotification(Integer id, String title, String message, String pictureurl) {
		db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("title", title);
		contentValues.put("message", message);
		contentValues.put("pictureurl", pictureurl);

		db.update("notification", contentValues, "id = ?", new String[] { Integer.toString(id) });
		return true;
	}

	public Integer deleteNotification(String id) {
		db = this.getWritableDatabase();
		Log.d("db id is ", id);
		return db.delete("notification", "uid = ?", new String[] { id });
	}
	
	public Integer deleteNotificationByTitle(String title) {
		db = this.getWritableDatabase();
		Log.d("db id is ", title);
		return db.delete("notification", "title = ?", new String[] { title });
	}

	public Integer deleteAllNotification() {
		db = this.getWritableDatabase();

		return db.delete("notification", "uid is null", null);
	}

	public Integer deleteCrimeReport(String id) {
		db = this.getWritableDatabase();
		Log.d("db id is ", id);

		return db.delete("crimereport", "uid = ?", new String[] { id });
	}

	public CrimeReport getCrimeReport(String uid) {

		Cursor cursor = null;
		CrimeReport crimeReport = null;
		
			db = this.getReadableDatabase();
			cursor = db.rawQuery("SELECT * FROM crimereport WHERE uid=? ORDER BY createdon DESC LIMIT 1",
					new String[] { uid });

			if (cursor.getCount() > 0) {

				cursor.moveToFirst();

				crimeReport = new CrimeReport(
						cursor.getString(cursor.getColumnIndex("uid")),
						cursor.getString(cursor.getColumnIndex("title")),
						cursor.getString(cursor.getColumnIndex("createdon")),
						cursor.getString(cursor.getColumnIndex("description")),
						cursor.getString(cursor.getColumnIndex("status")),
						cursor.getString(cursor.getColumnIndex("lat")), 
						cursor.getString(cursor.getColumnIndex("lot")),
						cursor.getString(cursor.getColumnIndex("casenum")));

		

			return crimeReport;
			}
			
			return null;
		
	}

	public ArrayList<String[]> getAllCrimeReports() {

		ArrayList<String[]> sl = new ArrayList<String[]>();

		db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from crimereport order by createdon desc", null);
		res.moveToFirst();
		while (res.isAfterLast() == false) {

			sl.add(new String[] { res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_ID)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_TITLE)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_UID)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_DESC)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_IMAGEURL)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_VIDEOURL)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_AUDIOURL)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_LAT)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_LOT)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_STATUS)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_CREATEDON)),
					res.getString(res.getColumnIndex(CRIMEREPORT_COLUMN_CASENUM))});

			res.moveToNext();
		}
		return sl;
	}

	public ArrayList<String[]> getAllNotification() {

		ArrayList<String[]> sl = new ArrayList<String[]>();

		db = this.getReadableDatabase();
		Cursor res = db.rawQuery(
				"select title,createdon,message,pictureurl from notification where uid is null order by createdon desc",
				null);
		res.moveToFirst();
		while (res.isAfterLast() == false) {

			sl.add(new String[] { res.getString(res.getColumnIndex(CONTACTS_COLUMN_TITLE)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_CREATEDON)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_MESSAGE)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_PICTUREURL)) });

			res.moveToNext();
		}
		return sl;
	}

	public ArrayList<String[]> getCaseUpdateNotification(String uid) {

		ArrayList<String[]> sl = new ArrayList<String[]>();

		db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from notification where uid='" + uid + "' order by createdon desc", null);
		res.moveToFirst();
		while (res.isAfterLast() == false) {

			sl.add(new String[] { res.getString(res.getColumnIndex(CONTACTS_COLUMN_TITLE)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_CREATEDON)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_MESSAGE)),
					res.getString(res.getColumnIndex(CONTACTS_COLUMN_NOTIFICATIONTYPE)) });

			res.moveToNext();
		}
		return sl;
	}

	public void close() {
		// NOTE: openHelper must now be a member of CallDataHelper;
		// you currently have it as a local in your constructor
		if (db != null) {
			db.close();
		}
	}
}