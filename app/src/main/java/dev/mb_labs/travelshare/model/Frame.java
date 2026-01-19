package dev.mb_labs.travelshare.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import java.util.List;

@Entity(tableName = "frames")
public class Frame {

    @PrimaryKey
    private int id;

    private String title;
    private String description;
    private String visibility;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("authorAvatar")
    private String authorAvatar;

    //the API returns an array of objects
    //the TypeConverter handles saving this list to the DB
    @SerializedName("photos")
    private List<Photo> photos;

    @SerializedName("photoMetadata")
    private String photoMetadata;

    @SerializedName("created_at")
    private String createdAt;

    private boolean isLiked = false;
    private int likeCount;
    private int commentCount;
    private int userId;

    //getters
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
    public boolean isLiked() { return isLiked; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }


    //setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }
    public void setPhotos(List<Photo> photos) { this.photos = photos; }
    public void setPhotoMetadata(String photoMetadata) { this.photoMetadata = photoMetadata; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public void setLikeCount(int count) { this.likeCount = count; }
    public void setCommentCount(int count) { this.commentCount = count; }
    public void setUserId(int userId) { this.userId = userId; }


    //inner class for photos
    public static class Photo {
        private int id;

        @SerializedName("image")
        private String filename;

        private double latitude;
        private double longitude;

        //getters
        public int getId() { return id; }
        public String getFilename() { return filename; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }

        //setters (might be needed if Room processes this deeply)
        public void setId(int id) { this.id = id; }
        public void setFilename(String filename) { this.filename = filename; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}