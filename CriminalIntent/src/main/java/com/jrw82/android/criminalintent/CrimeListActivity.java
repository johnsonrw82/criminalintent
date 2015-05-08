package com.jrw82.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.jrw82.android.lib.SingleFragmentActivity;


/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,
                                                                        CrimeFragment.Callbacks {

    private static final String TAG = "CrimeListActivity";

    @Override
    public void onCrimeUpdated(Crime crime) {
        // get the fragment from the fragment manager
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment)fm.findFragmentById(R.id.fragmentContainer);
        // tell it to update
        listFragment.updateUI();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        // running in phone mode
        if ( findViewById(R.id.detailFragmentContainer) == null ) {
            // start new instance of crime pager activity
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.CRIME_ID_EXTRA, crime.getId());
            startActivity(i);
        }
        else {
            // get fragment manager
            FragmentManager fm = getSupportFragmentManager();
            // begin new transaction
            FragmentTransaction ft = fm.beginTransaction();

            // get the old detail view
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            // make new instance
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            // if the old detail is present, remove it
            if ( oldDetail != null ) {
                ft.remove(oldDetail);
            }

            // commit the new transaction
            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }

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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
