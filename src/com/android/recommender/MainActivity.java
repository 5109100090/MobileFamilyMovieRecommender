package com.android.recommender;
//reference: http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.recommender.fragment.CinemasFragment;
import com.android.recommender.fragment.SessionFragment;
import com.android.recommender.history.HistoryFragment;
import com.android.recommender.home.HomeFragment;
import com.android.recommender.model.Cinema;
import com.android.recommender.movie.DetailMovieFragment;
import com.android.recommender.movie.ListMovieFragment;
import com.android.recommender.movie.MoviesFragment;
import com.example.recommender.R;

public class MainActivity extends ActionBarActivity 
	implements MoviesFragment.OnMovieSelectedListener, MoviesFragment.onTitleSearchListener,
	ListMovieFragment.onTitleSearchListener, DetailMovieFragment.onCinemaSelectedListener {

	private String[] mMenuTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String TAG = "NavigationDrawerProject";
	private String userName;
	private Double latitude, longitude;
	private Cinema selectedCinema = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMenuTitles = getResources().getStringArray(R.array.menus_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_launcher);

		Intent intent = getIntent();
		userName = intent.getStringExtra("userName");
		latitude = intent.getDoubleExtra("latitude", 0.0);
		longitude = intent.getDoubleExtra("longitude", 0.0);
		
		FragmentManager fragmentManager = getFragmentManager();
		//fragmentManager.beginTransaction().add(new HomeFragment(), null).commit();
		fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new ListMovieFragment(userName, "com.entity.movie/getLatest");
				break;
			case 1:
				fragment = new MoviesFragment(userName);
				break;
			case 2:
				fragment = new CinemasFragment(latitude, longitude, selectedCinema);
				break;
			case 3:
				fragment = new HistoryFragment(userName);
				break;
			case 4:
				finish();
				return;
			default:
				Log.e(TAG, "Invalid Selection");
			}
			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			// Highlight the selected item, update the title, and close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(mMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}
	
	@Override
	public void onTitleSearch(String movieTitle){
		// Create fragment and give it an argument for the selected article
        Fragment newFragment = new DetailMovieFragment(userName);
        Bundle args = new Bundle();
        args.putString(DetailMovieFragment.ARG_TITLE, movieTitle);
        newFragment.setArguments(args);
    
        FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, newFragment).commit();
	}

	@Override
	public void onMovieSelected(String queryUrl) {
		Fragment newFragment = new ListMovieFragment(userName, queryUrl);
    
        FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, newFragment).commit();
	}

	@Override
	public void onCinemaSelected(Cinema cinema) {
		selectedCinema = cinema;
	}
}