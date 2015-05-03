package com.jrw82.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johnsonrw82 on 5/3/2015.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";

    private String mFileName;

    /**
     * Create a Photo using an existing filename on disk
     *
     * @param fileName the name of the file
     */
    public Photo(String fileName) {
        mFileName = fileName;
    }

    public Photo(JSONObject json ) throws JSONException {
        mFileName = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFileName);
        return json;
    }

    public String getFileName() {
        return mFileName;
    }
}
