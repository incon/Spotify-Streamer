package com.example.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

// Used http://www.parcelabler.com/ to start with.

public class TrackData implements Parcelable {
    private String listImageUrl;
    private String fullImageUrl;
    private String songName;
    private String albumName;
    private String previewUrl;
    private String artistName;

    public TrackData(String artistName, String songName, String albumName,String listImageUrl,
                     String fullImageUrl, String previewUrl) {
        this.artistName = artistName;
        this.songName = songName;
        this.albumName = albumName;
        this.listImageUrl = listImageUrl;
        this.fullImageUrl = fullImageUrl;
        this.previewUrl = previewUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getFullImageUrl() {
        return fullImageUrl;
    }

    public String getListImageUrl() {
        return listImageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getSongName() {
        return songName;
    }

    protected TrackData(Parcel in) {
        listImageUrl = in.readString();
        fullImageUrl = in.readString();
        songName = in.readString();
        albumName = in.readString();
        previewUrl = in.readString();
        albumName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listImageUrl);
        dest.writeString(fullImageUrl);
        dest.writeString(songName);
        dest.writeString(albumName);
        dest.writeString(previewUrl);
        dest.writeString(albumName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TrackData> CREATOR = new Parcelable.Creator<TrackData>() {
        @Override
        public TrackData createFromParcel(Parcel in) {
            return new TrackData(in);
        }

        @Override
        public TrackData[] newArray(int size) {
            return new TrackData[size];
        }
    };
}
