package com.appbox.mep;

import android.net.Uri;

import java.io.Serializable;

public class Event implements Serializable {
    private String mName;
    private String mDetail;
    private String mDate;
    private int mTime;
    private String mOwner;
    private Uri mImage;
    private String mLocation;
    private String mEventId;

    public String getEventId() {

        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }
    //todo add category


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Uri getImage() {
        return mImage;
    }

    public void setImage(Uri image) {
        mImage = image;
    }

    public Event(String name, String detail, String date, int time, String owner, Uri image, String location) {
        mName = name;
        mDetail = detail;
        mDate = date;
        mTime = time;
        mOwner = owner;
        mImage = image;
        mLocation = location;
    }

}
