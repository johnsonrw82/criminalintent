package com.jrw82.android;

import java.util.UUID;

/**
 * Created by Ryan on 3/27/2015.
 */
public class Crime {
    private UUID mId;
    private String mTitle;

    public Crime() {
        // generate a UUID
        mId = UUID.randomUUID();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }
}
