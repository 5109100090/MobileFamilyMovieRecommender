package com.android.recommender.movie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.recommender.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MoviesFragment extends Fragment {

	private String userName;
	private Spinner spinnerYear, spinnerGenre, spinnerRating;
	private EditText editTextTitle;
	onTitleSearchListener cbTitleSearch;
	OnMovieSelectedListener cbMovieSelected;
	
	public MoviesFragment(String userName){
		this.userName = userName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
		editTextTitle = (EditText) rootView.findViewById(R.id.editTextTitle);
		spinnerYear = (Spinner) rootView.findViewById(R.id.spinnerYear);
		spinnerGenre = (Spinner) rootView.findViewById(R.id.spinnerGenre);
		spinnerRating = (Spinner) rootView.findViewById(R.id.spinnerRating);

		populateSpinnerYear();

		rootView.findViewById(R.id.buttonSearch).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						String movieTitle = editTextTitle.getText().toString(); 
						Log.i("movieTitle", movieTitle);
						if (!movieTitle.equals(""))
						{
							cbTitleSearch.onTitleSearch(movieTitle);
						}
						else
						{
							String query = "";
							List<String> conditions = new ArrayList<String>();
							String genre = (String) spinnerGenre.getSelectedItem();
							String rating = (String) spinnerRating.getSelectedItem();
							String year = (String) spinnerYear.getSelectedItem();
							
							if(!year.equals("Select"))
								conditions.add("pubYear=" + year);
							if(!genre.equals("Select"))
								conditions.add("genre=" + genre);
							if(!rating.equals("Select"))
								conditions.add("rating=" + rating);
							
							for(String s : conditions){
								query += s + "&";
							}
							query = query.substring(0, query.length()-1);
							Log.i("queryConditions", query);
							cbMovieSelected.onMovieSelected("com.entity.movie/search/" + query);
						}
					}
				});

		return rootView;
	}

	private void populateSpinnerYear(){
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		List<String> list = new ArrayList<String>();
		list.add("Select");
		for(int i = currentYear; i>=1964; i--){
			list.add(String.valueOf(i));
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerYear.setAdapter(dataAdapter);
	}

	public interface onTitleSearchListener {
		public void onTitleSearch(String movieTitle);
	}
	
	public interface OnMovieSelectedListener {
		public void onMovieSelected(String queryUrl);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			cbTitleSearch = (onTitleSearchListener) activity;
			cbMovieSelected = (OnMovieSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement Callback Listener");
		}
	}
}