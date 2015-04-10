package com.jrw82.android.criminalintent;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ryan on 3/27/2015.
 */
public class Crime implements Serializable {
    private static final long serialVersionUID = 1234L;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        // generate a UUID
        mId = UUID.randomUUID();
        // date of crime
        mDate = new Date();
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    // return crime's date in "Day of Week, Month day, year" format
    public CharSequence getDateAsString() {
        return DateFormat.format("EEEE, MMM dd, yyyy - hh:mm a", getDate());
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
