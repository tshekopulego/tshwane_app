package com.pulego.mysafety.ui;

import android.content.Intent;
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

import com.pulego.mysafety.R;
import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.db.DBHelper;

import java.util.ArrayList;

/**
 * The Class FeedList is the Fragment class that is launched when the user
 * clicks on Feed option in Left navigation drawer and this is also used as a
 * default fragment for MainActivity. It simply shows a dummy list of Property
 * Feeds. You can customize this class to display actual Feed listing.
 */
public class CrimeReportList extends CustomFragment {

	TextView emptyMessage;
	DBHelper mydb;
	ArrayList array_list;
	CrimeReportsAdapter arrayAdapter;
	View v;
    private ListView obj;
    /**
     * The feed list.
     */
    private ArrayList<String[]> fList;

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
		v = inflater.inflate(R.layout.crimereportlist, null);

		try {
			setHasOptionsMenu(true);
		} catch (Exception e) {

		}

		emptyMessage = (TextView) v.findViewById(R.id.empty);

		mydb = new DBHelper(v.getContext());
		loadAllCrimeReports();

		if (fList.isEmpty()) {
			obj.setVisibility(View.GONE);
			emptyMessage.setVisibility(View.VISIBLE);
		} else {
			obj.setVisibility(View.VISIBLE);
			emptyMessage.setVisibility(View.GONE);
		}

		obj.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				Intent i = new Intent(getActivity(),
						com.pulego.mysafety.CrimeReportDetail.class);
				String[] value = (String[]) adapter.getItemAtPosition(position);
				i.putExtra("uid", value[2]);
				i.putExtra("title", value[1]);
				i.putExtra("datetime", "Logged: " + value[10]);
				i.putExtra("description", value[3]);
				i.putExtra("status", value[9]);

				startActivity(i);
			}
		});

		// setNotificationList(v, false);
		return v;
	}

	private void loadAllCrimeReports() {

		fList = mydb.getAllCrimeReports();

		arrayAdapter = new CrimeReportsAdapter();

		// adding it to the list view.
		obj = (ListView) v.findViewById(R.id.list);
		obj.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

    }

    public int getCrimeDrawable(String type) {
        switch (type) {
            case "Contact Crime":
                return R.drawable.personscrime72;
            case "Drugs/Guns":
                return R.drawable.drugsguns72;
            case "Corruption":
                return R.drawable.corruption72;
            case "Property Crime":
                return R.drawable.property72;
            case "Vehicle Crime":
                return R.drawable.vehicle72;
            case "Protest Action":
                return R.drawable.public72;
            case "Traffic Incident":
                return R.drawable.traffic72;
            case "Bylaw Infringement":
                return R.drawable.bylaw72;
            case "Other":
                return R.drawable.help;
            case "Test":
                return R.drawable.test;
            default:
                return 0;
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

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
     * )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
       
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mydb != null)
            mydb.close();
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
                convertView = getLayoutInflater(null).inflate(
                        R.layout.crimereport_item, null);

            String[] d = getItem(position);
            TextView lbl = (TextView) convertView.findViewById(R.id.title);
            lbl.setText(d[1]);

            lbl = (TextView) convertView.findViewById(R.id.message);
            lbl.setText(d[3]);

            TextView lblduration = (TextView) convertView
                    .findViewById(R.id.status);
            lblduration.setText(d[9]);

            TextView lbldateTimeLogged = (TextView) convertView
                    .findViewById(R.id.dateTimeLogged);
            lbldateTimeLogged.setText("Logged:" + d[10]);

            int drawable = getCrimeDrawable(d[1]);

            Drawable dr = getResources().getDrawable(drawable);

            ImageView img = (ImageView) convertView
                    .findViewById(R.id.loaderImageView);

            img.setBackgroundDrawable(dr);

            return convertView;
        }

	}

}
