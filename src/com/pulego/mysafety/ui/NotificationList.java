package com.pulego.mysafety.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.loopj.android.image.SmartImageView;
import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.db.DBHelper;
import com.pulego.mysafety.utils.ServerUtilities;
import com.pulego.mysafety.R;

/**
 * The Class FeedList is the Fragment class that is launched when the user
 * clicks on Feed option in Left navigation drawer and this is also used as a
 * default fragment for MainActivity. It simply shows a dummy list of Property
 * Feeds. You can customize this class to display actual Feed listing.
 */
public class NotificationList extends CustomFragment {

	private ListView obj;
	View emptyMessage;
	Button clearButton;
	DBHelper mydb;
	ArrayList<String> array_list;
	NotificationsAdapter arrayAdapter;
	View v;
	MenuItem item;

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
		v = inflater.inflate(R.layout.notification, null);

		v.getContext().registerReceiver(Updated, new IntentFilter("notification_refreshed"));

		try {
			setHasOptionsMenu(true);
		} catch (Exception e) {

		}

		emptyMessage = (View) v.findViewById(R.id.container_empty);

		mydb = new DBHelper(v.getContext());
		loadNotifications();

		if (fList.isEmpty()) {
			showEmpty();
		}

		obj.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
				Intent i = new Intent(getActivity(), com.pulego.mysafety.NotificationDetail.class);
				String[] value = (String[]) adapter.getItemAtPosition(position);
				i.putExtra("title", value[0]);
				i.putExtra("datetime", value[1]);
				i.putExtra("message", value[2]);
				i.putExtra("pictureurl", value[3]);
				startActivity(i);
			}
		});
		
		

		return v;
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				mydb.deleteAllNotification();
				loadNotifications(); // Data was inserted upload it
				showEmpty();
				break;

			}
		}
	};

	private void showEmpty() {
		
		emptyMessage.setVisibility(View.VISIBLE);
		obj.setVisibility(View.GONE);
	}

	private final BroadcastReceiver Updated = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			loadNotifications(); // Data was inserted upload it
			showEmpty();
		}
	};

	private void loadNotifications() {

		fList = mydb.getAllNotification();

		arrayAdapter = new NotificationsAdapter();

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
	private class NotificationsAdapter extends BaseAdapter {

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
				convertView = getLayoutInflater(null).inflate(R.layout.notification_item, null);

			String[] d = getItem(position);
			TextView lbl = (TextView) convertView.findViewById(R.id.title);
			lbl.setText(d[0]);

			lbl = (TextView) convertView.findViewById(R.id.message);
			lbl.setText(d[2]);

			TextView lblduration = (TextView) convertView.findViewById(R.id.duration);
			lblduration.setText(d[1]);

			ServerUtilities.getTimeDifference(lblduration.getText().toString(), lblduration);

			SmartImageView img = (SmartImageView) convertView.findViewById(R.id.loaderImageView);

			img.setImageUrl(d[3]);
			
			Button b2 = (Button) convertView.findViewById(R.id.deleteNotButton);
	        b2.setTag(position);
	        b2.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	               
	                int pos = Integer.parseInt(arg0.getTag().toString());
	                String[] d = getItem(pos);
	                mydb.deleteNotificationByTitle(d[0]);
	                fList.remove(pos);
	                NotificationsAdapter.this.notifyDataSetChanged();            }
	        });

			return convertView;
		}

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
		inflater.inflate(R.menu.noti_list, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.
	 * MenuItem )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_trash) {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setMessage("Are you sure you want to clear notifications?")
					.setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

			return true;
		}

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
