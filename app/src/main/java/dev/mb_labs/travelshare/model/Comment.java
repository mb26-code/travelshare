package dev.mb_labs.travelshare.model;

import com.google.gson.annotations.SerializedName;

public class Comment {
    private int id;
    private int userId;
    private int frameId;
    private String content;
    private String authorName;
    private String authorAvatar;

    @SerializedName("postedOn")
    private String postedOn;

    @SerializedName("postedAt")
    private String postedAt;

    private boolean edited;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public String getPostedOn() { return postedOn; }
    public String getPostedAt() { return postedAt; }
    public boolean isEdited() { return edited; }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setPostedOn(String s) {
        this.postedOn = s;
    }

    public void setPostedAt(String s) {
        this.postedAt = s;
    }
}