package com.android.recommender.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.recommender.HTTPTransfer;
import com.android.recommender.model.Cinema;
import com.example.recommender.R;
import com.mapquest.android.maps.AnnotationView;
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.OverlayItem;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CinemasFragment extends Fragment {
	
	private Double latitude, longitude;
	private Cinema selectedCinema;
	MapView map;
	DefaultItemizedOverlay blackMarkersOverlay, redMarkersOverlay;
	AnnotationView annotation;
	
   public CinemasFragment(Double latitude, Double longitude, Cinema selectedCinema){
	   this.latitude = latitude;
	   this.longitude = longitude;
	   this.selectedCinema = selectedCinema;
   }
    
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
 
       View rootView = inflater.inflate(R.layout.fragment_cinemas, container, false);
       map = (MapView) rootView.findViewById(R.id.map);
       map.getController().setZoom(15);
       map.getController().setCenter(new GeoPoint(latitude, longitude));
       map.setBuiltInZoomControls(true);
       
       Drawable black_marker = getResources().getDrawable(R.drawable.black_marker);
       Drawable red_marker = getResources().getDrawable(R.drawable.red_marker);
       Drawable green_flag = getResources().getDrawable(R.drawable.green_flag);
       blackMarkersOverlay = new DefaultItemizedOverlay(black_marker);
       redMarkersOverlay = new DefaultItemizedOverlay(red_marker);
       final DefaultItemizedOverlay greenFlagOverlay = new DefaultItemizedOverlay(green_flag);
       
       OverlayItem myLocation = new OverlayItem(new GeoPoint(latitude, longitude), "You are here", "");
       greenFlagOverlay.addItem(myLocation);
       map.getOverlays().add(greenFlagOverlay);
       
       HTTPCinemas cinemas = new HTTPCinemas();
       cinemas.execute("com.entity.cinema/getNearestCinemas/"+latitude+","+longitude);
       
       annotation = new AnnotationView(map);
       blackMarkersOverlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
    	      @Override
    	        public void onTap(GeoPoint pt, MapView mapView) {
    	           // when tapped, show the annotation for the overlayItem 
    	    	  markerOnTap(blackMarkersOverlay);
    	         }
    	      });
       redMarkersOverlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
 	      @Override
 	        public void onTap(GeoPoint pt, MapView mapView) {
 	           // when tapped, show the annotation for the overlayItem 
 	    	  markerOnTap(redMarkersOverlay);
 	         }
 	      });
       greenFlagOverlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
 	      @Override
 	        public void onTap(GeoPoint pt, MapView mapView) {
 	           // when tapped, show the annotation for the overlayItem 
 	    	  markerOnTap(greenFlagOverlay);
 	         }
 	      });
       
       return rootView;
   }
   
   private void markerOnTap(DefaultItemizedOverlay overlay){
	   int lastTouchedIndex = overlay.getLastFocusedIndex();
       if(lastTouchedIndex>-1){
           OverlayItem tapped = overlay.getItem(lastTouchedIndex);
           annotation.showAnnotationView(tapped);
       }
   }
   
	class HTTPCinemas extends HTTPTransfer{
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONArray jArray = new JSONArray(result);
				int nCinemas = jArray.length();
				for (int i = 0; i < nCinemas; i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					
					OverlayItem cinema = new OverlayItem(new GeoPoint(jObj.getDouble("latitude"), jObj.getDouble("longitude")), jObj.getString("name"), "Phone: "+jObj.getString("phone")+"\nFeatures: "+jObj.getString("features"));
					
					if(selectedCinema != null && jObj.getInt("id") == selectedCinema.getId()){
						redMarkersOverlay.addItem(cinema);
						map.getOverlays().add(redMarkersOverlay);
					}else{
						blackMarkersOverlay.addItem(cinema);
					}
				}
				
				map.getOverlays().add(blackMarkersOverlay);
			} catch (JSONException e) {
				Log.e("Error", e.toString());
			}
		}
	}
}