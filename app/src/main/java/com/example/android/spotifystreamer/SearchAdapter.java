package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<ArtistData> {

    Context context;

    public SearchAdapter(Context context, int resource, ArrayList<ArtistData> ArtistDatas) {
        super(context, resource, ArtistDatas);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ArtistData ArtistData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_search, parent, false);
        }
        // Lookup view for data population
        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_search_image);
        TextView artistName = (TextView) convertView.findViewById(R.id.list_item_search_artistName);

        if (ArtistData.getImage() != null) {
            Picasso.with(context).load(ArtistData.getImage()).into(image);
        }
        // Populate the data into the template view using the data object
        artistName.setText(ArtistData.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
