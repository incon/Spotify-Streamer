package com.example.android.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MediaPlayerFragment extends Fragment {

    static final String CURRENT_POSITION = "CURRENT_POSITION";
    MediaPlayerService mps;
    boolean isBound = false;
    ArrayList<TrackData> trackDataCache;
    int position;
    int currentPosition = -1;
    View rootView;
    ImageButton playPrev;
    ImageButton playNext;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MediaPlayerService.MyLocalBinder binder = (MediaPlayerService.MyLocalBinder) service;
            mps = binder.getService();
            if (currentPosition == -1) {
                currentPosition = position;
            }
            mps.setDeck(currentPosition, trackDataCache.size()-1);
            mps.setRootView(getView());
            SeekBar fSeekBar = ((SeekBar) getView().findViewById(R.id.seekBar));
            fSeekBar.setMax(mps.getLength());
            if (!mps.getLoaded()) {
                fSeekBar.setEnabled(false);
            }
            String currentTrack = trackDataCache.get(currentPosition).getPreviewUrl();
            mps.playNext(currentTrack);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };

    public MediaPlayerFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_POSITION, currentPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unbind from the service
        if (isBound) {
            getActivity().getBaseContext().unbindService(myConnection);
            isBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media_player, container, false);

        //Bind to LocalService
        Intent intentService = new Intent(getActivity().getBaseContext(), MediaPlayerService.class);
        getActivity().getBaseContext().startService(intentService);
        getActivity().getBaseContext().bindService(intentService, myConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("TOP_TRACK_CACHE") && intent.hasExtra("SELECTED_POSITION")) {
            trackDataCache = intent.getParcelableArrayListExtra("TOP_TRACK_CACHE");
            position = intent.getIntExtra("SELECTED_POSITION", 0);
        }

        playPrev = (ImageButton) rootView.findViewById(R.id.playPrev);
        playNext = (ImageButton) rootView.findViewById(R.id.playNext);

        // Listeners
        nextTrackOnClick();
        prevTrackOnClick();

        ImageButton playPause = (ImageButton) rootView.findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mps.playPause();
            }
        });

        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean selected = false;
            boolean changed = false;
            int position;
            int currentMax;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Note: even programmatically setting setProgress will trigger this!!
                if (selected) {
                    changed = true;
                    position = progress;
                    currentMax = mps.getMaxThumb();
                    if (progress > currentMax) {
                        seekBar.setProgress(currentMax);
                    }
                } else {
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                selected = true;
                mps.trackProgress(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                selected = false;
                if (changed) {
                    if (position > currentMax) {
                        mps.seekTo(currentMax);
                    } else {
                        mps.seekTo(position);
                    }
                }
                mps.trackProgress(true);
            }
        });


        return rootView;
    }

    private void nextTrack() {
        currentPosition++;
        String currentTrack = trackDataCache.get(currentPosition).getPreviewUrl();
        mps.playNext(currentTrack);
        if (currentPosition + 1 == trackDataCache.size()) {
            playNext.setVisibility(View.GONE);
        }
        if (playPrev.getVisibility() == View.GONE) {
            playPrev.setVisibility(View.VISIBLE);
            prevTrackOnClick();
        }
    }

    private void prevTrack() {
        currentPosition--;
        String currentTrack = trackDataCache.get(currentPosition).getPreviewUrl();
        mps.playNext(currentTrack);
        if (currentPosition == 0) {
            playPrev.setVisibility(View.GONE);
        }
        if (playNext.getVisibility() == View.GONE) {
            playNext.setVisibility(View.VISIBLE);
            nextTrackOnClick();
        }
    }

    private void nextTrackOnClick() {
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });
    }

    private void prevTrackOnClick() {
        playPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevTrack();
            }
        });
    }

}
