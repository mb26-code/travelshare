package dev.mb_labs.travelshare.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Frame {
    private int id;
    private String title;
    private String description;
    private String visibility;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("authorAvatar")
    private String authorAvatar;

    //API returns an array of objects, not strings
    @SerializedName("photos")
    private List<Photo> photos;

    @SerializedName("photoMetadata")
    private String photoMetadata;

    private int userId;
    private String createdAt;


    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVisibility() { return visibility; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public List<Photo> getPhotos() { return photos; }
    public String getPhotoMetadata() { return photoMetadata; }
    public int getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }

    //inner class to match the JSON structure of photos: { "id": 10, "image": "...", ... }
    public static class Photo {
        private int id;

        @SerializedName("image")
        private String filename;

        private double latitude;
        private double longitude;

        public String getFilename() { return filename; }
    }
}