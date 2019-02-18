package com.appbox.mep;

import android.net.Uri;

public class Chat{
    private String mName;
    private String mText;
    private Uri mPhoto;
    private String mTimeStamp;
    private String mUserId;
    private String mEventId;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public Chat() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Uri getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Uri photo) {
        mPhoto = photo;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public Chat(String name, String text, Uri photo, String timeStamp) {

        mName = name;
        mText = text;
        mPhoto = photo;
        mTimeStamp = timeStamp;
    }
}
