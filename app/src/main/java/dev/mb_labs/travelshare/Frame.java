package dev.mb_labs.travelshare;

import java.util.List;

public class Frame {
    String authorName;
    String location;
    String description;
    List<Integer> imagesIds;

    private boolean isLiked;

    public Frame(String authorName, String location, String description, List<Integer> imagesIds) {
        this.authorName = authorName;
        this.location = location;
        this.description = description;
        this.imagesIds = imagesIds;
        this.isLiked = false;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}