package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    TopTrackAdapter mTopTrack;
    static ArrayList<TrackData> topTrackCache = new ArrayList<>();
    static String artistIdCache = null;
    static final String ARTIST_ID = "ARTIST_ID";
    static final String ARTIST_NAME = "ARTIST_NAME";
    String artistId = null;
    String artistName = null;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        ArrayList<TrackData> topTrack = new ArrayList<>();

        mTopTrack =
                new TopTrackAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_top_tracks, // The name of the layout ID.
                        topTrack);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_top_tracks);
        listView.setAdapter(mTopTrack);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMediaPlayer(topTrackCache, position);
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            artistId = arguments.getString(ARTIST_ID);
            artistName = arguments.getString(ARTIST_NAME);
            ((MainActivity)getActivity()).getSupportActionBar().setSubtitle(artistName);
        }

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(ARTIST_ID) && intent.hasExtra(ARTIST_NAME)) {
            artistId = intent.getStringExtra(ARTIST_ID);
            artistName = intent.getStringExtra(ARTIST_NAME);
            ((TopTracksActivity)getActivity()).getSupportActionBar().setSubtitle(artistName);
        }

        if (artistId != null && artistName != null) {
            if (topTrackCache.isEmpty() || !artistId.equals(artistIdCache)) {
                artistIdCache = artistId;
                SpotifyTopTrackTask spotifyTopTrackTask = new SpotifyTopTrackTask();
                spotifyTopTrackTask.execute(artistId);
            } else {
                update(topTrackCache);
            }
        }

        return rootView;
    }

    public class SpotifyTopTrackTask extends AsyncTask<String, Void, List<Track>> {

        private final String LOG_TAG = SpotifyTopTrackTask.class.getSimpleName();

        @Override
        protected List<Track> doInBackground(String... params) {
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(SpotifyService.COUNTRY, "AU");
            options.put(SpotifyService.OFFSET, 0);
            options.put(SpotifyService.LIMIT, 10);
            String artistId = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Tracks results = spotify.getArtistTopTrack(artistId, options);
            return results.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            topTrackCache.clear();
            for (Track track : tracks) {
                String listImageUrl = null;
                String fullImageUrl = null;
                int largestWidth = 0;
                for (Image image : track.album.images) {
                    if (image.width >= 200) {
                        listImageUrl = image.url;
                    }
                    if (image.width > largestWidth) {
                        largestWidth = image.width;
                        fullImageUrl = image.url;
                    }
                }

                String previewUrl = track.preview_url;
                String songName = track.name;
                String albumName = track.album.name;
                TrackData topTrack = new TrackData(artistName, songName, albumName,listImageUrl, fullImageUrl, previewUrl);
                topTrackCache.add(topTrack);
            }
            update(topTrackCache);
        }

    }

    private void update(List<TrackData> tracks) {
        mTopTrack.clear();
        for (TrackData track : tracks) {
            mTopTrack.add(track);
        }
    }

    public void startMediaPlayer(ArrayList<TrackData> topTrackCache, int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        MediaPlayerFragment newFragment = new MediaPlayerFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(MediaPlayerFragment.TOP_TRACK_CACHE, topTrackCache);
        args.putInt(MediaPlayerFragment.DECK_POSITION, position);
        newFragment.setArguments(args);

        if (getActivity() instanceof MainActivity) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.replace(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }
    }
}
