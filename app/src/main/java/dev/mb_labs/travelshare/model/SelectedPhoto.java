package dev.mb_labs.travelshare.model;

import android.net.Uri;

public class SelectedPhoto {
    private Uri uri;
    private Double latitude;
    private Double longitude;

    public SelectedPhoto(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() { return uri; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setLocation(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }
}