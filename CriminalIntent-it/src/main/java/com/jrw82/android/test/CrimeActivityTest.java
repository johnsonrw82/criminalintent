package com.jrw82.android.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jrw82.android.criminalintent.CrimeActivity;

public class CrimeActivityTest extends ActivityInstrumentationTestCase2<CrimeActivity> {

    public CrimeActivityTest() {
        super(CrimeActivity.class);
    }

    public void testActivity() {
        CrimeActivity activity = getActivity();
        assertNotNull(activity);
    }
}

