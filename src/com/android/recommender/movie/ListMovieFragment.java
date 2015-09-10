package com.android.recommender.movie;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.recommender.HTTPTransfer;
import com.android.recommender.model.Movie;
import com.example.recommender.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class ListMovieFragment extends Fragment {

	private List<Movie> movieList = new ArrayList<Movie>();
	private ProgressBar progressBar1;
	private View empty;
	private ListView listView1;
	private Context context;
	onTitleSearchListener cbTitleSearch;
	private String queryUrl, userName;
	
	public ListMovieFragment(String userName, String queryUrl){
		this.userName = userName;
		this.queryUrl = queryUrl;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        
		context = container.getContext();
		View rootView = inflater.inflate(R.layout.fragment_list_movie, container, false);
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		empty = rootView.findViewById(R.id.empty);
		
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Movie movie = movieList.get(position);
				cbTitleSearch.onTitleSearch(movie.getTitle());
				Log.i("selectedMovie", position + " " + movie.getTitle());
			}

	    });
		
		HTTPListMovie rest = new HTTPListMovie();
		rest.execute(queryUrl);
		
		return rootView;
	}
	
	public interface onTitleSearchListener {
		public void onTitleSearch(String movieTitle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			cbTitleSearch = (onTitleSearchListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onTitleSearchListener");
		}
	}
	
	class HTTPListMovie extends HTTPTransfer{
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

				if(nMovies == 0){
					empty.setVisibility(View.VISIBLE);
				}
				
				MovieArrayAdapter adapter = new MovieArrayAdapter(context, movieList, R.layout.list_item_movie);
				listView1.setAdapter(adapter);

			} catch (JSONException e) {
				Log.e("Error", e.toString());
			}
			progressBar1.setVisibility(View.INVISIBLE);
		}
	}
}