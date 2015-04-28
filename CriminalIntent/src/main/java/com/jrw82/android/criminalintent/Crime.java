package com.jrw82.android.criminalintent;

import android.text.format.DateFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ryan on 3/27/2015.
 */
public class Crime implements Serializable {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";

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

    public Crime(JSONObject jsonObject) throws JSONException {
        if ( jsonObject != null ) {
            mId = UUID.fromString(jsonObject.getString(JSON_ID));
            if ( jsonObject.has(JSON_TITLE) ) {
                mTitle = jsonObject.getString(JSON_TITLE);
            }
            mSolved = jsonObject.getBoolean(JSON_SOLVED);
            mDate = new Date(jsonObject.getLong(JSON_DATE));
        }
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

    /**
     * Method that will convert this object to a JSONObject
     *
     * @return the JSONObject representing this Crime object
     * @throws JSONException if there is a problem converting to JSON
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, this.mId);
        jsonObject.put(JSON_TITLE, this.mTitle);
        jsonObject.put(JSON_SOLVED, this.mSolved);
        jsonObject.put(JSON_DATE, this.mDate.getTime());
        return jsonObject;
    }

    @Override
    public String toString() {
        return mTitle;
    }
}
