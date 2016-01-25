package com.pulego.mysafety.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.pulego.mysafety.custom.CustomFragment;
import com.pulego.mysafety.utils.AboutType;
import com.pulego.mysafety.R;

/**
 * The Class FeedList is the Fragment class that is launched when the user
 * clicks on Feed option in Left navigation drawer and this is also used as a
 * default fragment for MainActivity. It simply shows a dummy list of Property
 * Feeds. You can customize this class to display actual Feed listing.
 */
public class AboutList extends CustomFragment {

	View v;

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
		v = inflater.inflate(R.layout.about, null);

		ListView listView1 = (ListView) v.findViewById(R.id.listView1);
		
		PackageInfo pInfo;
		String version;
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			version = "";
		}
		
		final AboutType[] items = { new AboutType("About Tshwane Safety",""), new AboutType("Terms of Use",""), new AboutType("Version",version) };

		ArrayAdapterItem adapter = new ArrayAdapterItem(v.getContext(),
				R.layout.about_item, items);

		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				view.setSelected(true);
				
				AboutType about = items[position];

				if (about.getName() == "About Tshwane Safety") {

					final FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.replace(R.id.content_frame, new About(), "About");

					ft.addToBackStack("About Tshwane Safety");
					ft.commit();
				} else if (about.getName() == "Terms of Use") {
					final FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.replace(R.id.content_frame, new Terms(), "Terms");

					ft.addToBackStack("Terms of Use");
					ft.commit();
				}

			}
		});

		listView1.setAdapter(adapter);

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
		/*
		 * if (item.getItemId() == R.id.menu_sort) showSortPopup(); else if
		 * (item.getItemId() == R.id.menu_locate) startActivity(new
		 * Intent(getActivity(), MapViewActivity.class)); else if
		 * (item.getItemId() == R.id.menu_search) startActivity(new
		 * Intent(getActivity(), SearchResultActivity.class));
		 */

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
	class ArrayAdapterItem extends ArrayAdapter<AboutType> {

	    Context mContext;
	    int layoutResourceId;
	    AboutType data[] = null;

	    public ArrayAdapterItem(Context mContext, int layoutResourceId, AboutType[] data) {

	        super(mContext, layoutResourceId, data);

	        this.layoutResourceId = layoutResourceId;
	        this.mContext = mContext;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        /*
	         * The convertView argument is essentially a "ScrapView" as described is Lucas post 
	         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
	         * It will have a non-null value when ListView is asking you recycle the row layout. 
	         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
	         */
	        if(convertView==null){
	            // inflate the layout
	            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
	            convertView = inflater.inflate(layoutResourceId, parent, false);
	        }

	        // object item based on the position
	        AboutType objectItem = data[position];

	        // get the TextView and then set the text (item name) and tag (item ID) values
	        TextView textViewItem = (TextView) convertView.findViewById(R.id.aboutitem);
	        textViewItem.setText(objectItem.getName());
	        
	        TextView versionViewItem = (TextView) convertView.findViewById(R.id.version);
	        versionViewItem.setText(objectItem.getVersion());
	       

	        return convertView;

	    }

	}

}
