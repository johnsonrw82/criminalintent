package com.jrw82.android;

import android.support.v4.app.Fragment;
import android.util.Log;
import com.jrw82.android.lib.SingleFragmentActivity;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String TAG = "CrimeActivity";

    /**
     * This method returns the Fragment associated with this particular activity
     * @return the fragment associated with this activity
     */
    @Override
    protected Fragment createFragment() {
        Log.d(TAG, "createFragment() called");
        return new CrimeFragment();
    }
}

