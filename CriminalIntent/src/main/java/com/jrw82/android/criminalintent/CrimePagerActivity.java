package com.jrw82.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by johnsonrw82 on 4/5/2015.
 */
public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCrimeUpdated(Crime crime) {
        // not required for this activity
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate a view pager, not created with XML
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        // get crime list
        mCrimes = CrimeLab.get(this).getCrimes();

        // use fragment manager to handle setting up a pager adapter
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            // create a new CrimeFragment from the Crime found at position
            @Override
            public Fragment getItem(int position) {
                Crime c = mCrimes.get(position);
                return CrimeFragment.newInstance(c.getId());
            }

            // get the size of the crimes list
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // empty
            }

            @Override
            public void onPageSelected(int position) {
                Crime c = mCrimes.get(position);
                if ( c.getTitle() != null ) {
                    setTitle(c.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // empty
            }
        });

        // set current item
        setCurrentItem();
    }

    private void setCurrentItem() {
        // get the clicked crime id from the intent
        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.CRIME_ID_EXTRA);

        // set the current item to the id of the crime that's been clicked
        for ( int i = 0; i < mCrimes.size(); i++ ) {
            if ( mCrimes.get(i).getId().equals(crimeId) ) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}


