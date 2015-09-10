package com.android.recommender.history;

import java.util.List;
import com.android.recommender.model.*;
import com.example.recommender.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryArrayAdapter extends ArrayAdapter<History> {
	private int view;
	private final Context context;
	private final List<History> values;

	public HistoryArrayAdapter(Context context, List<History> values,
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
		TextView cinema = (TextView) rowView.findViewById(R.id.cinema);
		TextView session = (TextView) rowView.findViewById(R.id.session);

		History h = values.get(position);
		movieTitle.setText(h.getMovie());
		cinema.setText(h.getCinema());
		session.setText(h.getSession_date() + " " + h.getSession_time());
		
		return rowView;
	}
}
