package com.android.recommender.movie;

import java.util.List;
import com.android.recommender.model.*;
import com.example.recommender.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
	private int view;
	private final Context context;
	private final List<Movie> values;

	public MovieArrayAdapter(Context context, List<Movie> values,
			int view) {
		super(context, view, values);

		this.context = context;
		this.values = values;
		this.view = view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(view, parent, false);
		
		TextView movieTitle = (TextView) rowView.findViewById(R.id.movieTitle);
		TextView movieGenre = (TextView) rowView.findViewById(R.id.movieGenre);

		Movie movie = values.get(position);
		movieTitle.setText(movie.getTitle() + " (" + String.valueOf(movie.getPubYear()) + ")");
		movieGenre.setText(movie.getGenre() + " (" + movie.getRating() + ")");
		
		return rowView;
	}
}
