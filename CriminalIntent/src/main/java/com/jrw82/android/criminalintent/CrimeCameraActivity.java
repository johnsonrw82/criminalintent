package com.jrw82.android.criminalintent;

import android.support.v4.app.Fragment;
import com.jrw82.android.lib.SingleFragmentActivity;

/**
 * Created by johnsonrw82 on 5/2/2015.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
