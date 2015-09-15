package com.pulego.mysafety.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import android.net.ParseException;
import android.util.Log;
import android.widget.TextView;
 
public final class ServerUtilities {
	
	
	 /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    public static void post(String endpoint, Map<String, String> params)
            throws IOException {   	
        
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v("Posting", "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
        	Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }

	public static void getTimeDifference(String pDate, TextView time) {
	    int diffInDays = 0;
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    format.setTimeZone(TimeZone.getTimeZone("GMT"));
	    Calendar c = Calendar.getInstance();
	    String formattedDate = format.format(c.getTime());
	
	    Date d1 = null;
	    Date d2 = null;
	    try {
	
	        d1 = format.parse(formattedDate);
	        d2 = format.parse(pDate);
	        long diff = d1.getTime() - d2.getTime();
	
	        diffInDays = (int) (diff / (1000 * 60 * 60 * 24));
	        if (diffInDays > 0) {
	            if (diffInDays == 1) {
	                time.setText(diffInDays + " day ago");
	            } else {
	                time.setText(diffInDays + " days ago");
	            }
	        } else {
	            int diffHours = (int) (diff / (60 * 60 * 1000));
	            if (diffHours > 0) {
	                if (diffHours == 1) {
	                    time.setText(diffHours + " hr ago");
	                } else {
	                    time.setText(diffHours + " hrs ago");
	                }
	            } else {
	
	                int diffMinutes = (int) ((diff / (60 * 1000) % 60));
	                if (diffMinutes == 1) {
	                    time.setText(diffMinutes + " min ago");
	                } else {
	                    time.setText(diffMinutes + " mins ago");
	                }
	
	            }
	        }
	
	    } catch (ParseException e) {
	        // System.out.println("Err: " + e);
	        e.printStackTrace();
	    } catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
    
}
