package com.android.recommender.history;

import java.util.ArrayList;
import java.util.List;

import com.android.recommender.model.History;
import com.example.recommender.R;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HistoryFragment extends Fragment {

	private List<History> historyList = new ArrayList<History>();
	private View empty;
	private ListView listView1;
	private String userName;
	private Context context;

	public HistoryFragment(String userName){
		this.userName = userName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = container.getContext();
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		empty = rootView.findViewById(R.id.empty);

		populateListView();
		
		return rootView;
	}

	private void populateListView(){
		SQLiteDatabase myDB = null;
		try {
			myDB = getActivity().openOrCreateDatabase("mydbs.db", 0, null);
			String query = "SELECT * FROM history WHERE username='" + userName + "'";
			Cursor c = myDB.rawQuery(query , null);

			if(c.getCount() == 0){
				empty.setVisibility(View.VISIBLE);
			}else{
				int colMovie = c.getColumnIndex("movie");
				int colCinema = c.getColumnIndex("cinema");
				int colDate = c.getColumnIndex("session_date");
				int colTime = c.getColumnIndex("session_time");
	
				c.moveToFirst();
				if (c != null) {
					do {
						History h = new History();
						h.setMovie(c.getString(colMovie));
						h.setCinema(c.getString(colCinema));
						h.setSession_date(c.getString(colDate));
						h.setSession_time(c.getString(colTime));
						historyList.add(h);
					}while(c.moveToNext());
				}
			}
		}
		catch(Exception e) {
			Log.e("Error", "Error", e);
		} finally {
			if (myDB != null)
				myDB.close();
		}

		HistoryArrayAdapter adapter = new HistoryArrayAdapter(context, historyList, R.layout.list_item_history);
		listView1.setAdapter(adapter);
	}
}