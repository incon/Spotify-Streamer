package com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    SearchAdapter mSearchArtist;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ArrayList<ArtistData> searchArtist = new ArrayList<ArtistData>();
        mSearchArtist =
                new SearchAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_search, // The name of the layout ID.
                        searchArtist);

        SpotifySearchTask spotifySearchTask = new SpotifySearchTask();
        spotifySearchTask.execute();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_search);
        listView.setAdapter(mSearchArtist);

        return rootView;
    }

    public class SpotifySearchTask extends AsyncTask<Void, Void, List<Artist>> {
        private final String LOG_TAG = SpotifySearchTask.class.getSimpleName();

        private String getSearchImage(List<Image> images) {
            String imageUrl = null;
            for (Image image : images) {
                if (image.width >= 200) {
                    imageUrl = image.url;
                }
            }
            return imageUrl;

        }

        @Override
        protected List<Artist> doInBackground(Void... params) {
            String artist = "hermitude";
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(artist);
            List<Artist> artists = results.artists.items;
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            if (artists != null) {
                mSearchArtist.clear();
                for (Artist artist : artists) {
                    String name = artist.name;
                    mSearchArtist.add(new ArtistData(getSearchImage(artist.images), name));
                }
            }
        }

    }
}
