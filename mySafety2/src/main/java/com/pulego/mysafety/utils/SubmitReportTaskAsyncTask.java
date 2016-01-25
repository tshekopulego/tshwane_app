package com.pulego.mysafety.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.pulego.mysafety.CoSafetyApplication;
import com.pulego.mysafety.db.DBHelper;

import android.os.AsyncTask;
import android.util.Log;

public class SubmitReportTaskAsyncTask extends AsyncTask<String, Void, Boolean> {

	JSONParser jsonParser = new JSONParser();

	DBHelper mydb;

	// url to report a crime
	private static String url_report_crime = "http://test.tshwanesafety.co.za/dashboard/reportcrime.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_UID = "uid";

	protected Boolean doInBackground(String... params) {

		mydb = new DBHelper(CoSafetyApplication.getAppContext());

		try {

			String desc = params[0];
			String categoryType = params[1];
			String lat = params[2];
			String lot = params[3];
			String user = params[4];
			String location = params[5];
			String mobile = params[6];
			String audioloc = params[7];
			String imageloc = params[8];
			String videoloc = params[9];
			String type = params[10];
			String name = params[11];
			String regId = params[12];

			// Building Parameters
			List<NameValuePair> crimereportparams = new ArrayList<NameValuePair>();
			crimereportparams.add(new BasicNameValuePair("description", desc));
			crimereportparams.add(new BasicNameValuePair("categoryType",
					categoryType));
			crimereportparams.add(new BasicNameValuePair("lat", lat));
			crimereportparams.add(new BasicNameValuePair("lot", lot));
			crimereportparams.add(new BasicNameValuePair("user", user));
			crimereportparams.add(new BasicNameValuePair("mobile", mobile));
			crimereportparams.add(new BasicNameValuePair("location", location));
			crimereportparams.add(new BasicNameValuePair("imagelocation",
					imageloc));

			crimereportparams.add(new BasicNameValuePair("videolocation",
					videoloc));

			crimereportparams.add(new BasicNameValuePair("audiolocation",
					audioloc));

			crimereportparams.add(new BasicNameValuePair("type", type));
			crimereportparams.add(new BasicNameValuePair("name", name));

			crimereportparams.add(new BasicNameValuePair("regId", regId));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_report_crime,
					"POST", crimereportparams);

			// check log cat from response
			Log.d("Create Response", json.toString());

			Log.d("Create Response - UID", json.getString(TAG_UID));
			// check for success tag
			try {

				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					// inserted the record to local db
					mydb.insertCrimeReport(json.getString(TAG_UID),
							categoryType, desc, imageloc, audioloc, videoloc,
							lat, lot, "New");

					// log to db
					boolean isInserted = mydb.insertNotification(categoryType
							+ " - New Case", json.getString(TAG_UID),
							"Case status set to new", null, "New");

					return true;
				} else
					return false;

			} catch (JSONException e) {
				Log.e("ERROR", e.toString());
				mydb.close();
				return false;
			}

		} catch (Exception e) {
			Log.e("ERROR", e.toString());
			mydb.close();
			return false;
		} finally {

		}
	}

	protected void onPostExecute(Boolean result) {
		MyBus.getInstance().post(new AsyncTaskSubmitReportResultEvent(result));
	}
}