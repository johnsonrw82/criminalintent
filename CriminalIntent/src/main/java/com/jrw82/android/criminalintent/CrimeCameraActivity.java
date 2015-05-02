package com.jrw82.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;
import com.jrw82.android.lib.SingleFragmentActivity;

/**
 * Created by johnsonrw82 on 5/2/2015.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // hide window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide status bar and other os level stuff
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
