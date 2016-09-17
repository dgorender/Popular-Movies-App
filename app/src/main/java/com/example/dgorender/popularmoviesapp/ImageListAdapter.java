package com.example.dgorender.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Movie> movies;

    public ImageListAdapter(Context context, int layoutResource, ArrayList<Movie> imageUrls) {

        super(context, layoutResource, imageUrls);

        this.context = context;
        this.movies = imageUrls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_image_item, parent, false);
        }

        Picasso
                .with(context)
                .load(movies.get(position).getPosterPath())
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }

    public Object getItem(int position) {
        return movies.get(position);
    }

}