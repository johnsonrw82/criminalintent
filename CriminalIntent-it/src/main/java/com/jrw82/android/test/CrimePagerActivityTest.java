package com.jrw82.android.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jrw82.android.criminalintent.CrimePagerActivity;

public class CrimePagerActivityTest extends ActivityInstrumentationTestCase2<CrimePagerActivity> {

    public CrimePagerActivityTest() {
        super(CrimePagerActivity.class);
    }

    public void testActivity() {
        CrimePagerActivity activity = getActivity();
        assertNotNull(activity);
    }
}

