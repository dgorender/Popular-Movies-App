package com.example.dgorender.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.dgorender.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String> imageUrls;

    public ImageListAdapter(Context context, int layoutResource, ArrayList<String> imageUrls) {

        super(context, layoutResource, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_image_item, parent, false);
        }

        Picasso
                .with(context)
                .load(imageUrls.get(position))
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }

    /*public void add(String url)
    {
        imageUrls.add(url);
        notifyDataSetChanged();
    }*/

}