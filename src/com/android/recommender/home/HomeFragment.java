package com.android.recommender.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.recommender.HTTPTransfer;
import com.android.recommender.model.Movie;
import com.android.recommender.movie.MovieArrayAdapter;
import com.example.recommender.R;

public class HomeFragment extends Fragment {

	private List<Movie> movieList = new ArrayList<Movie>();
	private ListView listView1;
	private Context context;
	private ProgressBar progressBar;

	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		context = container.getContext();
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Movie movie = movieList.get(position);
			}

	    });
		
		HTTPHome rest = new HTTPHome(); 
		rest.execute("com.entity.movie/getLatest");

		return rootView;
	}
	
	class HTTPHome extends HTTPTransfer{
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray jArray = new JSONArray(result);
				int nMovies = jArray.length();
				for (int i = 0; i < nMovies; i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					Movie movie = new Movie();
					movie.setId(json_data.getInt("id"));
					movie.setTitle(json_data.getString("title"));
					movie.setPubYear(json_data.getInt("pubYear"));
					movie.setGenre(json_data.getString("genre"));
					movie.setRating(json_data.getString("rating"));
					movieList.add(movie);
				}

				MovieArrayAdapter adapter = new MovieArrayAdapter(context, movieList, R.layout.list_item_movie);
				listView1.setAdapter(adapter);
			} catch (JSONException e) {
				Log.e("Error", e.toString());
			}
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}