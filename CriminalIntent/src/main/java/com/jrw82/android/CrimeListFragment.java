package com.jrw82.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;

/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the title of the activity
        getActivity().setTitle(R.string.crimes_title);
        // get the crimes from the crime lab
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
    }
}
