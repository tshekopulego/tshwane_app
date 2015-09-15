package com.pulego.mysafety.utils;

import com.pulego.mysafety.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author mac
 *
 */
public class ArrayAdapterItem extends ArrayAdapter<ReportType> {

	Context mContext;
	int layoutResourceId;
	ReportType data[] = null;

	public ArrayAdapterItem(Context mContext, int layoutResourceId, ReportType[] data) {

		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}

		// object item based on the position
		ReportType objectItem = data[position];

		// get the TextView and then set the text (item name) and tag (item
		// ID) values
		TextView textViewItem = (TextView) convertView.findViewById(R.id.typename);
		textViewItem.setText(objectItem.getName());
		textViewItem.setTag(objectItem.getId());

		return convertView;

	}

}