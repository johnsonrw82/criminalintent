package com.jrw82.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context context) {
        this.mAppContext = context;
        this.mCrimes = new ArrayList<Crime>();
        for ( int i = 0 ; i < 100; i++ ) {
            Crime c = new Crime();
            int num = i+1;
            c.setTitle("Crime #" + num);
            c.setSolved(i % 2 == 0);
            mCrimes.add(c);
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

    public Crime getCrime(UUID id) {
        for ( Crime c : mCrimes ) {
            if ( c.getId().equals(id) ) {
                return c;
            }
        }

        return null;
    }
}