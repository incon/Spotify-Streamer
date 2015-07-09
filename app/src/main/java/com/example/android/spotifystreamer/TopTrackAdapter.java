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

public class TopTrackAdapter extends ArrayAdapter<TrackData> {

    Context context;

    public TopTrackAdapter(Context context, int resource, ArrayList<TrackData> tracks) {
        super(context, resource, tracks);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrackData track = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_top_tracks, parent, false);
        }
        // Lookup view for data population
        ImageView album_image = (ImageView) convertView.findViewById(R.id.list_item_album_image);
        TextView name = (TextView) convertView.findViewById(R.id.list_item_song_name);
        TextView album = (TextView) convertView.findViewById(R.id.list_item_album_name);

        if (track.getListImageUrl() != null) {
            Picasso.with(context).load(track.getListImageUrl()).into(album_image);
        }
        // Populate the data into the template view using the data object
        name.setText(track.getSongName());
        album.setText(track.getAlbumName());
        // Return the completed view to render on screen
        return convertView;
    }
}
