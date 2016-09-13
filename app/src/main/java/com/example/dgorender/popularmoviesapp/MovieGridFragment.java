package com.example.dgorender.popularmoviesapp;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieGridFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviegrid_fragment, container, false);
        mForecastAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        ListView mForecastView = (ListView) rootView.findViewById(R.id.moviegridlayout);
        mForecastView.setAdapter(mForecastAdapter);
        mForecastAdapter.add("Movie 1");
        mForecastAdapter.add("Movie 2");
        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }



}
