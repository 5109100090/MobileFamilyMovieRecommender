package com.android.recommender.fragment;

import com.example.recommender.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SessionFragment extends Fragment {
    
   public SessionFragment(){}
    
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
 
       View rootView = inflater.inflate(R.layout.fragment_session, container, false);
         
       return rootView;
   }
}