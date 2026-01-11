package dev.mb_labs.travelshare.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Frame {
    private int id;
    private String title;
    private String description;
    private String visibility;

    @SerializedName("photos")
    private List<String> photoUrls;

    @SerializedName("photoMetadata")
    private String photoMetadata;
    //JSON String

    private int userId;
    private String createdAt;



    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVisibility() { return visibility; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public String getPhotoMetadata() { return photoMetadata; }
    public int getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }
}

