package com.jrw82.android.criminalintent;

import android.os.Bundle;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
