package com.jrw82.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private CrimeLab(Context context) {
        this.mAppContext = context;
        this.mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try {
            this.mCrimes = mSerializer.loadCrimes();
        }
        catch (Exception ex ) {
            mCrimes = new ArrayList<>();
            Log.e(TAG, "There was an error reading crimes from file: ", ex);
        }
    }

    public static CrimeLab get(Context context) {
        if ( sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "Crimes saved to file " + FILENAME);
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

    public Crime getCrime(UUID id) {
        for ( Crime c : mCrimes ) {
            if ( c.getId().equals(id) ) {
                return c;
            }
        }

        return null;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c) {
        mCrimes.remove(c);
    }
}
