package dev.mb_labs.travelshare.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("profilePicture")
    private String profilePictureUrl;

    public int getId() { return id; }
    public String getEmail() { return email; }

    public String getName() { return name; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
}

