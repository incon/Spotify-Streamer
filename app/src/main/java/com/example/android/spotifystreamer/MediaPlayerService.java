package com.example.android.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.android.spotifystreamer.R;

import java.io.IOException;

/**
 * Created by incon on 5/07/15.
 */
public class MediaPlayerService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // Set States
    private boolean loaded = false;
    private boolean paused = false;
    private int downloaded = 0;
    private int length = 0;
    private String url = null;
    private int trackNo;
    private int trackNoOf;

    public void setDeck(int trackNo, int trackNoOf) {
        this.trackNo = trackNo;
        this.trackNoOf = trackNoOf;
    }

    // Set thread handler
    private Handler handler = new Handler();

    // UI
    private SeekBar seekBar;
    private ImageButton playPausedBtn;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private Runnable UpdateProgress = new Runnable() {
        public void run() {
            // Set progress and set to poll again
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 100);
        }
    };

    public void setRootView(View rootView) {
        // Get Ui items
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        playPausedBtn = (ImageButton) rootView.findViewById(R.id.playPause);

        if (trackNo == 0) {
            rootView.findViewById(R.id.playPrev).setVisibility(View.GONE);
        }
        if (trackNo == trackNoOf) {
            rootView.findViewById(R.id.playNext).setVisibility(View.GONE);
        }
    }

    public void playPause() {
        if (paused) {
            // Start audio and thread
            mediaPlayer.start();
            handler.postDelayed(UpdateProgress, 100);
            // Change play icon to pause icon
            playPausedBtn.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            // Stop thread and pause audio
            handler.removeCallbacks(UpdateProgress);
            mediaPlayer.pause();
            // Change pause icon to play icon
            playPausedBtn.setImageResource(android.R.drawable.ic_media_play);
        }
        // Toggle state
        paused = !paused;
    }

    private void loadMedia(String url) {
        playPausedBtn.setEnabled(false);
        seekBar.setEnabled(false);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        createMediaListeners();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // While loading disable the button
            playPausedBtn.setEnabled(false);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            // Allow button to be pressed to try again
            playPausedBtn.setEnabled(true);
            mediaPlayer.release();
        }
    }

    private void createMediaListeners() {
        // Create Media Player
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Remove callbacks
                handler.removeCallbacks(UpdateProgress);

                // Pause and set to start
                paused = true;
                seekBar.setProgress(0);
                playPausedBtn.setImageResource(android.R.drawable.ic_media_play);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Set loaded status
                loaded = true;

                // Enable seek bar
                seekBar.setEnabled(true);


                // Start audio
                mediaPlayer.start();
                paused = false;

                // Enable and change to pause
                playPausedBtn.setEnabled(true);
                playPausedBtn.setImageResource(android.R.drawable.ic_media_pause);

                // Set progress size set start
                length = mediaPlayer.getDuration();
                seekBar.setMax(length);
                //seekBar.setProgress(0);

                // Start progress updater
                handler.postDelayed(UpdateProgress, 100);
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                // Set callback
                handler.postDelayed(UpdateProgress, 100);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                downloaded = percent;
                seekBar.setSecondaryProgress(getMaxThumb());
            }
        });
    }

    public void trackProgress(boolean track) {
        // Clear callback stack
        handler.removeCallbacks(UpdateProgress);
        // Track?
        if (track) {
            handler.postDelayed(UpdateProgress, 100);
        }
    }

    public void seekTo(int position) {
        // Stop thread
        handler.removeCallbacks(UpdateProgress);
        // Begin seek
        mediaPlayer.seekTo(position);
    }

    public void playNext(String url) {
        if (this.url == null || !this.url.equals(url)) {
            this.url = url;
            loaded = false;
            loadMedia(url);
        }
    }

    public int getMaxThumb() {
        return (int) (seekBar.getMax() * downloaded/100.0);
    }



    public class MyLocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public boolean getLoaded() {
        return loaded;
    }

    public int getLength() {
        return length;
    }

    public int getCurrentPosition() {
        if (loaded) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }
}
