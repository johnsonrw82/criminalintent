package com.jrw82.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ryan on 3/27/2015.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_CHOICE = "choice";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CHOICE = 2;
    private static final int REQUEST_PHOTO = 3;

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

        // tell the fragment manager that the options menu is enabled
        setHasOptionsMenu(true);

        if ( savedInstanceState != null ) {
            mCrime = (Crime) savedInstanceState.getSerializable(CRIME_KEY);
        }
        else {
            UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID_EXTRA);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle) called");
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        // enable the home button as up (only for honeycomb or higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            // check for a parent activity first
            if (NavUtils.getParentActivityName(getActivity())!= null ) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

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

        // image button
        ImageButton imageButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD ? Camera.getNumberOfCameras() > 0 :
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

        if ( !hasCamera ) {
            imageButton.setEnabled(false);
        }

        // display date
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();   // update the button with the date info
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DateTimeDialogChoiceFragment dialog = new DateTimeDialogChoiceFragment();
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOICE); // set this fragment as the target for the created fragment
                dialog.show(fm, DIALOG_CHOICE);  // show the dialog
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // if there is an activity identified as the parent, use it to navigate up the stack
                navigateUp();
                return true;
            case R.id.menu_item_delete_crime:
                // delete the crime from crimelab
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                // navigate up to the previous activity
                navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ( resultCode == Activity.RESULT_OK) {
            if ( requestCode == REQUEST_DATE || requestCode == REQUEST_TIME) {
                // get the date from the intent
                Date d = (Date)intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                // set the crime's date
                mCrime.setDate(d);
                // update the date display
                updateDate();
            }
            if ( requestCode == REQUEST_CHOICE ) {
                boolean setDate = intent.getBooleanExtra(DateTimeDialogChoiceFragment.EXTRA_CHOICE, true);

                // determine which fragment to show
                if ( setDate ) {
                    FragmentManager fm = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); // set this fragment as the target for the created fragment
                    dialog.show(fm, DIALOG_DATE);  // show the dialog
                }
                else {
                    FragmentManager fm = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME); // set this fragment as the target for the created fragment
                    dialog.show(fm, DIALOG_TIME);  // show the dialog
                }
            }
            // if there was a picture taken
            if ( requestCode == REQUEST_PHOTO ) {
                // create a new photo object and attach it to the crime
                String fileName = intent.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                if ( fileName != null ) {
                    Log.i(TAG, "Photo filename: " + fileName);
                }
            }
        }
    }

    private void navigateUp() {
        if (NavUtils.getParentActivityName(getActivity()) != null ) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        else {
            getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateAsString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CRIME_KEY, mCrime);
    }

}
