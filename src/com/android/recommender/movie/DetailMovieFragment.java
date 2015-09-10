package com.android.recommender.movie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.recommender.HTTPTransfer;
import com.android.recommender.model.Cinema;
import com.example.recommender.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.koushikdutta.ion.Ion;
import com.ppierson.t4jtwitterlogin.T4JTwitterFunctions;

public class DetailMovieFragment extends Fragment {

	public final static String ARG_TITLE = "title";
	private String movieTitleQuery, userName;
	private TextView movieTitle, movieDesc, movieStars, movieGenre, movieRating, movieDirector, movieKeywords;
	private ProgressBar progressBar;
	private LinearLayout mainLayout, historyLayout;
	private ImageView imageView;
	private Spinner spinnerCinema, spinnerDate, spinnerSession;
	private Button buttonConfirm;
	private List<Cinema> cinemaList = new ArrayList<Cinema>();
	private Context context;
	private int movieId;
	private View noMovie;
	private onCinemaSelectedListener cbCinemaSelected;
	
	//http://stackoverflow.com/questions/29379890/android-facebook-sdk-4-in-eclipse
	private FacebookSdk facebook;
	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = "Error";
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = "Success";
                String id = result.getPostId();
                String alertMessage = "Alert Message";
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton("OK", null)
                    .show();
        }
    };
	
	public DetailMovieFragment(String userName){
		this.userName = userName;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
        Bundle args = getArguments();
        movieTitleQuery = args.getString(ARG_TITLE);
        
        context = container.getContext();
        FacebookSdk.sdkInitialize(context);
        
		View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);
		mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
		mainLayout.setVisibility(LinearLayout.INVISIBLE);
		historyLayout = (LinearLayout) rootView.findViewById(R.id.historyLayout);
		movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
		movieDesc = (TextView) rootView.findViewById(R.id.movieDesc);
		movieStars = (TextView) rootView.findViewById(R.id.movieStars);
		movieGenre = (TextView) rootView.findViewById(R.id.movieGenre);
		movieRating = (TextView) rootView.findViewById(R.id.movieRating);
		movieDirector = (TextView) rootView.findViewById(R.id.movieDirector);
		movieKeywords = (TextView) rootView.findViewById(R.id.movieKeywords);
		imageView = (ImageView) rootView.findViewById(R.id.movieIcon);
		spinnerCinema = (Spinner) rootView.findViewById(R.id.spinnerCinema);
		spinnerDate = (Spinner) rootView.findViewById(R.id.spinnerDate);
		spinnerSession = (Spinner) rootView.findViewById(R.id.spinnerSession);
		buttonConfirm = (Button) rootView.findViewById(R.id.buttonConfirm);
		noMovie = rootView.findViewById(R.id.noMovie);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		
		HTTPDetailMovie movie = new HTTPDetailMovie();
		try {
			movie.execute("com.entity.movie/getMovie/" + URLEncoder.encode(movieTitleQuery, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		buttonConfirm.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						int position = spinnerCinema.getSelectedItemPosition();
						Cinema cinema = cinemaList.get(position);
						String date = (String) spinnerDate.getSelectedItem();
						String session = (String) spinnerSession.getSelectedItem();
						
						if(session.equals("Select") || session.equals("No session")){
							setSpinnerSessionItem(new String[] {"Loading.."});
							
							HTTPGetSessions getSessions = new HTTPGetSessions();
							getSessions.execute("com.entity.session/getSessions/movieId="+movieId+"&cinemaId="+cinema.getId()+"&date="+date);
							
							cbCinemaSelected.onCinemaSelected(cinema);
						}else{
							addToHistory(cinema.getName(), session, date);
						}
					}
				});
		
		return rootView;
	}
	
	public interface onCinemaSelectedListener {
		public void onCinemaSelected(Cinema cinema);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			cbCinemaSelected = (onCinemaSelectedListener ) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onTitleSearchListener");
		}
	}
	
	protected void setSpinnerSessionItem(String[] strings){
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSession.setAdapter(dataAdapter);
	}
	
	private void populateSpinnerCinema(String cinemasJson) throws JSONException{
		JSONArray jArray = new JSONArray(cinemasJson);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject json_data = jArray.getJSONObject(i);

			Cinema cinema = new Cinema();
			cinema.setId(json_data.getInt("id"));
			cinema.setName(json_data.getString("name"));
			cinema.setSuburb(json_data.getString("suburb"));
			cinema.setLatitude(json_data.getDouble("latitude"));
			cinema.setLongitude(json_data.getDouble("longitude"));
			cinemaList.add(cinema);
		}
        
		ArrayAdapter<Cinema> dataAdapter = new ArrayAdapter<Cinema>(getActivity(), android.R.layout.simple_spinner_item, cinemaList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCinema.setAdapter(dataAdapter);
	}
	
	private void populateSpinnerDate(){
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 7; i++)
        {
            list.add(format.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDate.setAdapter(dataAdapter);
	}
	
	private void addToHistory(final String cinema, final String session, final String date){
		final String movie = movieTitle.getText().toString();
		
		final SQLiteDatabase myDB = getActivity().openOrCreateDatabase("mydbs.db", 0, null);
		String query = "SELECT * FROM history WHERE username='" + userName + "' AND cinema='"+cinema+"' AND movie='"+movie+"' AND session_time='"+session+"' AND session_date='"+date+"'";
		Log.i("checkHistory", query);
		Cursor c = myDB.rawQuery(query , null);
		if(c.getCount() == 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Are you sure to add the movie to History?").setTitle("Confirmation");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
			   			ContentValues values = new ContentValues();
						values.put("username", userName);
						values.put("cinema", cinema);
						values.put("movie", movie);
						values.put("session_time", session);
						values.put("session_date", date);
						myDB.insert("history", null, values);
						
						//postToFB(cinema, session, movie);
						//postToTwitter(cinema, session, movie);
						
						Toast.makeText(context, "Your movie is saved to History.", Toast.LENGTH_SHORT).show();
						myDB.close();
		           }
		       });
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
			builder.show();
		}else{
			Toast.makeText(context, "Movie already in History.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/*
	private void postToTwitter(String cinema, String session, String movie){
		T4JTwitterFunctions.postToTwitter(
				context,
				getActivity(),
				"T5y2Pu0tfsYv7pRiT7I0uoDLo",
				"tigmrtlN4iDPGYHspCFXJikJySR8f3UGHIyzK8o0ym6ZocJGso",
				movie + "at " + cinema + " at " + session,
				new T4JTwitterFunctions.TwitterPostResponse() {
		    @Override
		    public void OnResult(Boolean success) {
		        // TODO Auto-generated method stub
		        if(success){
		            Toast.makeText(getActivity().getApplicationContext(), "Tweet posted successfully", Toast.LENGTH_SHORT).show();
		        }else{
		            Toast.makeText(getActivity().getApplicationContext(), "Tweet did not post!", Toast.LENGTH_SHORT).show();
		        }
		    }
		});

	}
	*/
	private CallbackManager callbackManager;
	
	private void postToFB(final String cinema, final String session, final String movie) {
		
		callbackManager = CallbackManager.Factory.create();
		
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                    	LoginManager.getInstance().logInWithPublishPermissions(getActivity(), Arrays.asList("publish_actions"));
                    	
                        Profile profile = Profile.getCurrentProfile();
                		ShareLinkContent content = new ShareLinkContent.Builder()
                			.setContentUrl(Uri.parse("https://developers.facebook.com"))
                	        .setContentTitle(movie)
                	        .setContentDescription("at " + cinema + " at " + session)
                	        .build();
                        ShareApi.share(content, shareCallback);
                        Log.i("FBShare", "should be shared");
                    }

                    @Override
                    public void onCancel() {
                    	Log.i("FBShare", "canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    	Log.e("FBShare", exception.getMessage());
                    }
                });
    }
	
	class HTTPDetailMovie extends HTTPTransfer{
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject jObj = new JSONObject(result);
				movieId = jObj.getInt("movieId");
				
				if(movieId == -2){
					mainLayout.setVisibility(LinearLayout.INVISIBLE);
					noMovie.setVisibility(View.VISIBLE);
				}else{
					Ion.with(imageView).placeholder(R.drawable.gif_loader).load(jObj.getString("thumbnail"));
					
					Date date = new SimpleDateFormat("yyyy-MM-dd").parse(jObj.getString("release_date"));
				    Calendar cal = Calendar.getInstance();
				    cal.setTime(date);
				    
				    
					movieTitle.setText(jObj.getString("title") + " (" + cal.get(Calendar.YEAR) + ")");
					movieDesc.setText(jObj.getString("description"));
					movieStars.setText("Stars: "+jObj.getString("stars"));
					movieGenre.setText("Genre: "+jObj.getString("genre"));
					movieRating.setText("Rating: "+jObj.getString("contentrating"));
					movieDirector.setText("Director: "+jObj.getString("director"));
					movieKeywords.setText("Keywords: "+jObj.getString("keywords"));
					
					if(movieId == -1){
						historyLayout.setVisibility(LinearLayout.INVISIBLE);
					}else{
						populateSpinnerCinema(jObj.getString("cinemas"));
						populateSpinnerDate();
					}
					mainLayout.setVisibility(LinearLayout.VISIBLE);
				}
			} catch (JSONException e) {
				Log.e("Error", e.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	class HTTPGetSessions extends HTTPTransfer{
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray jArray = new JSONArray(result);
				int size = jArray.length();
				String[] sessions = new String[size == 0 ? 1 : size];
				
				if(size == 0){
					sessions[0] = "No session";
				}else{
					buttonConfirm.setText("Add to History");
				}
				
				for (int i = 0; i < size; i++) {
					sessions[i] = jArray.getString(i);
				}
		        
				setSpinnerSessionItem(sessions);
			} catch (JSONException e) {
				Log.e("Error", e.toString());
			}
		}
	}
}