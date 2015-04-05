package com.jrw82.android.criminalintent;

import android.support.v4.app.Fragment;
import android.util.Log;
import com.jrw82.android.lib.SingleFragmentActivity;


/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    private static final String TAG = "CrimeListActivity";

    @Override
    protected Fragment createFragment() {
        Log.d(TAG, "createFragment called");
        return new CrimeListFragment();
    }
}
