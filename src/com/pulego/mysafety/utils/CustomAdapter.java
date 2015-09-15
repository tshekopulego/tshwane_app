/**
 * 
 */
package com.pulego.mysafety.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author mac
 *
 */
public class CustomAdapter extends BaseAdapter {

	private String[] mValues;
	private Context mContext;

	public CustomAdapter(Context context, String[] values) {
		mValues = values;
		mContext = context;
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
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
		View row = inflater.inflate(android.R.layout.simple_spinner_item, viewGroup, false);
		TextView v = (TextView) row.findViewById(android.R.id.text1);
		v.setText(mValues[position]);
		return row;
	}
}

