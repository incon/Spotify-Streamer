package com.example.android.spotifystreamer;

public class TrackData {
    private String imageUrl;
    private String songName;
    private String albumName;

    public TrackData(String imageUrl, String songName, String albumName) {
        this.imageUrl = imageUrl;
        this.songName = songName;
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
