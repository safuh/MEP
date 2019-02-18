package com.appbox.mep;

import android.net.Uri;

public class User {
    private String mId;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    private String mName;
    private String mPhoto;
    private String mEmail;
    private String mBio;

    public String getBio() {
        return mBio;
    }

    public void setBio(String bio) {
        mBio = bio;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhoto() {

        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getName() {

        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    public User(String name,String photo, String email,String id){
        this.mName = name;
        this.mPhoto = photo;
        this.mEmail = email;
        this.mId = id;
    }
}
