package com.jrw82.android.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jrw82.android.*;

public class CriminalIntentActivityTest extends ActivityInstrumentationTestCase2<CriminalIntentActivity> {

    public CriminalIntentActivityTest() {
        super(CriminalIntentActivity.class);
    }

    public void testActivity() {
        CriminalIntentActivity activity = getActivity();
        assertNotNull(activity);
    }
}

