package com.pulego.mysafety.utils;

import android.os.AsyncTask;
import android.util.Log;

public class PhotoUploadTaskAsyncTask extends AsyncTask<String, Void, Boolean> {

	protected Boolean doInBackground(String... params) {
		PhotoUploader photoUploader;
		try {
			photoUploader = new PhotoUploader();
			return photoUploader.Upload(params[0],params[1]);
		} catch (Exception e) {
			Log.e("ERROR", e.toString());
			return false;
		} finally {
			photoUploader = null;
		}
	}

	protected void onPostExecute(Boolean result) {
		MyBus.getInstance().post(new AsyncTaskResultEvent(result));
	}
}