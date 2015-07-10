package com.example.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements
        MainActivityFragment.Callback {

    private static final String TOPTRACKSFRAGMENT_TAG = "TTFTAG";


    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_tracks_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, new TopTracksFragment(), TOPTRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String artistId, String artistName) {
        Bundle args = new Bundle();
        args.putString(TopTracksFragment.ARTIST_ID, artistId);
        args.putString(TopTracksFragment.ARTIST_NAME, artistName);

        TopTracksFragment fragment = new TopTracksFragment();
        fragment.setArguments(args);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, fragment, TOPTRACKSFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class)
            .putExtra(TopTracksFragment.ARTIST_ID, artistId)
            .putExtra(TopTracksFragment.ARTIST_NAME, artistName);
            startActivity(intent);
        }
    }

    @Override
    public void clearTopTracks() {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, new Fragment(), TOPTRACKSFRAGMENT_TAG)
                    .commit();
        }
    }
}
