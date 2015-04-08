package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ryan on 3/27/2015.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    public static final String CRIME_KEY = "com.jrw82.android.criminalintent.crime_key";
    public static final String CRIME_ID_EXTRA = "com.jrw82.android.criminalintent.crime_id";

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;


    /**
     * Static method that will be used to create a new crime fragment using a crime ID
     * @param crimeId the UUID of the crime
     * @return a CrimeFragment for the crime specified by crimeId
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID_EXTRA, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");

        if ( savedInstanceState != null ) {
            mCrime = (Crime) savedInstanceState.getSerializable(CRIME_KEY);
        }
        else {
            UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID_EXTRA);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle) called");
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        mTitleField = (EditText)v.findViewById(R.id.crimeTitle);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // blank
            }
        });

        // update the title field to display what's already there
        mTitleField.setText(mCrime.getTitle());

        // display date
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();   // update the button with the date info
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); // set this fragment as the target for the created fragment
                dialog.show(fm, DIALOG_DATE);  // show the dialog
            }
        });

        // set up checkbox
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });


        // is crime solved/
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ( resultCode == Activity.RESULT_OK) {
            if ( requestCode == REQUEST_DATE ) {
                // get the date from the intent
                Date d = (Date)intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                // set the crime's date
                mCrime.setDate(d);
                // update the date display
                updateDate();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateAsString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CRIME_KEY, mCrime);
    }
}
