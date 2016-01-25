package com.pulego.mysafety.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.R;

/**
 * The Class Search is the Fragment class that is launched when the user clicks
 * on Search option in Left navigation drawer and it simply shows a few dummy
 * options for Search property with options for Searching property for Buy and
 * Rent. You can customize this to display actual contents.
 */
public class EmergencyContact extends CustomFragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.contact_detail, null);

		Button button1 = (Button) v.findViewById(R.id.button1);

		// add button listener
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:10111"));
				startActivity(callIntent);

			}

		});

		Button button2 = (Button) v.findViewById(R.id.button2);

		// add button listener
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:10177"));
				startActivity(callIntent);

			}

		});

		Button button3 = (Button) v.findViewById(R.id.button3);

		// add button listener
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:012 310 6300"));
				startActivity(callIntent);

			}

		});

		Button button4 = (Button) v.findViewById(R.id.button4);

		// add button listener
		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:012 310 6400"));
				startActivity(callIntent);

			}

		});

		Button button5 = (Button) v.findViewById(R.id.button5);

		// add button listener
		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:012 358 7095"));
				startActivity(callIntent);

			}

		});

		Button button6 = (Button) v.findViewById(R.id.button6);

		// add button listener
		button6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:012 358 7096"));
				startActivity(callIntent);

			}

		});

		return v;
	}

}
