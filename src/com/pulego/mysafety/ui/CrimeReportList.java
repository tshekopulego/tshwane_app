package com.pulego.mysafety.ui;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.db.DBHelper;
import com.pulego.mysafety.R;

/**
 * The Class FeedList is the Fragment class that is launched when the user
 * clicks on Feed option in Left navigation drawer and this is also used as a
 * default fragment for MainActivity. It simply shows a dummy list of Property
 * Feeds. You can customize this class to display actual Feed listing.
 */
public class CrimeReportList extends CustomFragment {

	private ListView obj;
	View emptyMessage;
	DBHelper mydb;
	ArrayList<String> array_list;
	CrimeReportsAdapter arrayAdapter;
	View v;

	/** The feed list. */
	private ArrayList<String[]> fList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.crimereportlist, null);

		v.getContext().registerReceiver(Updated, new IntentFilter("crimereported_deleted"));

		try {
			setHasOptionsMenu(true);
		} catch (Exception e) {

		}

		emptyMessage = (View) v.findViewById(R.id.container_empty);

		mydb = new DBHelper(v.getContext());

		loadAllCrimeReports();

		showEmpty();

		obj.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
				Intent i = new Intent(getActivity(), com.pulego.mysafety.CrimeReportDetail.class);
				String[] value = (String[]) adapter.getItemAtPosition(position);
				i.putExtra("uid", value[2]);
				i.putExtra("title", value[1]);
				i.putExtra("datetime", "Logged: " + value[10]);
				i.putExtra("description", value[3]);
				i.putExtra("status", value[9]);
				i.putExtra("lat", value[7]);
				i.putExtra("lot", value[8]);

				startActivity(i);
			}
		});

		return v;
	}

	private void showEmpty() {
		if (fList.isEmpty()) {
			emptyMessage.setVisibility(View.VISIBLE);
			obj.setVisibility(View.GONE);
		}
	}

	private final BroadcastReceiver Updated = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			loadAllCrimeReports(); // Data was inserted upload it
			showEmpty();
		}
	};

	public void loadAllCrimeReports() {

		fList = mydb.getAllCrimeReports();

		arrayAdapter = new CrimeReportsAdapter();

		// adding it to the list view.
		obj = (ListView) v.findViewById(R.id.list);
		obj.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

	}

	/**
	 * The Class FeedAdapter is the adapter class for Feed ListView. The current
	 * implementation simply shows dummy contents and you can customize this
	 * class to display actual contents as per your need.
	 */
	private class CrimeReportsAdapter extends BaseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return fList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String[] getItem(int position) {
			return fList.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getLayoutInflater(null).inflate(R.layout.crimereport_item, null);

			String[] d = getItem(position);
			TextView lbl = (TextView) convertView.findViewById(R.id.title);
			lbl.setText(d[1]);

			lbl = (TextView) convertView.findViewById(R.id.message);
			lbl.setText(d[3]);

			TextView lblduration = (TextView) convertView.findViewById(R.id.status);
			lblduration.setText(d[9]);

			TextView lbldateTimeLogged = (TextView) convertView.findViewById(R.id.dateTimeLogged);
			lbldateTimeLogged.setText("Logged:" + d[10]);

			String lat = d[7];
			String lot = d[8];

			TextView lbllat = (TextView) convertView.findViewById(R.id.lat);
			TextView lbllot = (TextView) convertView.findViewById(R.id.lot);
			TextView lbllatLabel = (TextView) convertView.findViewById(R.id.latLabel);
			TextView lbllotLabel = (TextView) convertView.findViewById(R.id.lotLabel);

			ImageView loaderImageView = (ImageView) convertView.findViewById(R.id.img1);

			if (!lat.isEmpty() || !lot.isEmpty()) {

				lbllat.setText(lat);
				lbllot.setText(lot);
			} else {
				lbllat.setVisibility(View.GONE);
				lbllot.setVisibility(View.GONE);
				loaderImageView.setVisibility(View.GONE);

				lbllatLabel.setVisibility(View.GONE);
				lbllotLabel.setVisibility(View.GONE);
			}

			int drawable = getCrimeDrawable(d[1]);

			Drawable dr = getResources().getDrawable(drawable);

			ImageView img = (ImageView) convertView.findViewById(R.id.loaderImageView);

			img.setBackgroundDrawable(dr);

			return convertView;
		}

	}

	public int getCrimeDrawable(String type) {
		if (type.equals("Contact Crime"))
			return R.drawable.contactcrime;
		else if (type.equals("Drugs/Guns"))
			return R.drawable.drugsguns72;
		else if (type.equals("Corruption"))
			return R.drawable.corruption72;
		else if (type.equals("Property Crime"))
			return R.drawable.property72;
		else if (type.equals("Vehicle Crime"))
			return R.drawable.vehicle72;
		else if (type.equals("Protest Action"))
			return R.drawable.public72;
		else if (type.equals("Traffic Incident"))
			return R.drawable.traffic72;
		else if (type.equals("Bylaw Infringement"))
			return R.drawable.bylaw72;
		else if (type.equals("Other"))
			return R.drawable.help;
		else if (type.equals("Test"))
			return R.drawable.test;
		else
			return 0;

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
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.
	 * MenuItem )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mydb.close();
	}

}
