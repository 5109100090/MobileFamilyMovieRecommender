package com.android.recommender.model;

public class History {
	private String username;
	private String movie;
	private String cinema;
	private String session_date;
	private String session_time;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMovie() {
		return movie;
	}
	public void setMovie(String movie) {
		this.movie = movie;
	}
	public String getCinema() {
		return cinema;
	}
	public void setCinema(String cinema) {
		this.cinema = cinema;
	}
	public String getSession_date() {
		return session_date;
	}
	public void setSession_date(String session_date) {
		this.session_date = session_date;
	}
	public String getSession_time() {
		return session_time;
	}
	public void setSession_time(String session_time) {
		this.session_time = session_time;
	}
}
