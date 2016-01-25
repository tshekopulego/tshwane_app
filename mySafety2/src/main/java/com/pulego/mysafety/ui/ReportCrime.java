package com.pulego.mysafety.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
public class ReportCrime extends CustomFragment {

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
		View v = inflater.inflate(R.layout.main_activity, null);
		
		
		setTouchNClick(v.findViewById(R.id.contactcrime));
		setTouchNClick(v.findViewById(R.id.drugs));
		setTouchNClick(v.findViewById(R.id.corruption));
		setTouchNClick(v.findViewById(R.id.property));
		setTouchNClick(v.findViewById(R.id.vehicle));
		setTouchNClick(v.findViewById(R.id.protestaction));
		setTouchNClick(v.findViewById(R.id.traffic));
		setTouchNClick(v.findViewById(R.id.bylaw));
		// buy = true;
		return v;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//inflater.inflate(R.menu.noti_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realestate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);

		Intent i = new Intent(getActivity(), com.pulego.mysafety.ReportIt.class);
		i.putExtra("category", ((Button) v).getText().toString());
		i.putExtra("categoryNum", Integer.parseInt(((Button) v).getTag().toString()));
		Log.i("bundle", ((Button) v).getTag().toString());

		startActivity(i);
	}
	
	
}
