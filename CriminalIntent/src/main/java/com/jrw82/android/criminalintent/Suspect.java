package com.jrw82.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johnsonrw82 on 5/5/2015.
 */
public class Suspect {
    private static final String JSON_SUSPECT_NAME = "suspect_name";
    private static final String JSON_SUSPECT_NUMBER = "number";

    private String mSuspectName;
    private String mSuspectNumber;

    public Suspect(String suspectName) {
        mSuspectName = suspectName;
    }

    public Suspect(String suspectName, String suspectNumber) {
        this(suspectName);
        mSuspectNumber = suspectNumber;
    }

    public Suspect(JSONObject suspectObject) throws JSONException {
        if ( suspectObject != null ) {
            if (suspectObject.has(JSON_SUSPECT_NAME) ) {
                mSuspectName = suspectObject.getString(JSON_SUSPECT_NAME);
            }
            if ( suspectObject.has(JSON_SUSPECT_NUMBER) ) {
                mSuspectNumber = suspectObject.getString(JSON_SUSPECT_NUMBER);
            }
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject suspectObject = new JSONObject();
        suspectObject.put(JSON_SUSPECT_NUMBER, mSuspectNumber);
        suspectObject.put(JSON_SUSPECT_NAME, mSuspectName);

        return suspectObject;
    }

    public String getSuspectName() {
        return mSuspectName;
    }

    public void setSuspectName(String suspectName) {
        mSuspectName = suspectName;
    }

    public String getSuspectNumber() {
        return mSuspectNumber;
    }

    public void setSuspectNumber(String suspectNumber) {
        mSuspectNumber = suspectNumber;
    }
}
