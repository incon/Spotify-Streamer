package com.example.android.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    static List<Artist> searchCache = new ArrayList<>();


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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_search);
        listView.setAdapter(mSearchArtist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArtistData artistId = mSearchArtist.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, artistId.getId())
                        .putExtra("ARTIST_NAME", artistId.getName());
                startActivity(intent);
            }
        });

        //Update List View
        updateSearchListView(searchCache);

        EditText searchInput = (EditText) rootView.findViewById(R.id.search_input);

        searchInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = v.getText().toString();
                    SpotifySearchTask spotifySearchTask = new SpotifySearchTask();
                    spotifySearchTask.execute(search);

                    v.clearFocus();
                    hideSoftKeyboard(getActivity(), v);

                    // True will keep the keyboard open
                    // handled = true;
                }
                return handled;
            }
        });

        if (!searchCache.isEmpty()) {
            // http://stackoverflow.com/questions/5056734/android-force-edittext-to-remove-focus
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        return rootView;
    }

    // http://stackoverflow.com/questions/18414804/android-edittext-remove-focus-after-clicking-a-button
    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public class SpotifySearchTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = SpotifySearchTask.class.getSimpleName();

        @Override
        protected List<Artist> doInBackground(String... params) {
            List<Artist> artists = new ArrayList<>();

            //Clear Cache
            searchCache.clear();

            String artist = params[0];
            if (!artist.isEmpty()) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(artist);
                artists = results.artists.items;
            }
            searchCache = artists;
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            if (artists != null && !artists.isEmpty()) {
                updateSearchListView(artists);
            } else {
                mSearchArtist.clear();
                Context context = getActivity();
                String message = getResources().getString(R.string.no_match_found);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }

    }

    private String getSearchImage(List<Image> images) {
        String imageUrl = null;
        for (Image image : images) {
            if (image.width >= 200) {
                imageUrl = image.url;
            }
        }
        return imageUrl;

    }

    private void updateSearchListView(List<Artist> artists) {
        mSearchArtist.clear();
        for (Artist artist : artists) {
            String name = artist.name;
            String id = artist.id;
            mSearchArtist.add(new ArtistData(getSearchImage(artist.images), name, id));
        }
    }
}
