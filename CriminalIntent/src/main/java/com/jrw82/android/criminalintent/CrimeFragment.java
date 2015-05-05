package com.jrw82.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CHOICE = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CONTACT = 4;

    public static final String CRIME_ID_EXTRA = "com.jrw82.android.criminalintent.crime_id";

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageView mPhotoView;
    private Button mSuspectButton;


    private DialogInterface.OnClickListener mDeletePhotoDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    deletePhoto();
                    break;
            }
        }
    };

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

        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID_EXTRA);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle) called");
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        // enable the home button as up (only for honeycomb or higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // check for a parent activity first
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mTitleField = (EditText) v.findViewById(R.id.crimeTitle);
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

        // if there is no camera, disable the button
        if (!hasCamera) {
            imageButton.setEnabled(false);
        }

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p != null) {
                    // show the image in a dialog view
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                    // create new instance of the photo fragment, using photo path and orientation
                    ImageFragment.newInstance(path, p.getRotation()).show(fm, DIALOG_IMAGE);
                }
            }
        });
        // attach context menu to the thumbnail
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(mPhotoView);
        }
        else {
            mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if ( mPhotoView.getDrawable() != null ) {
                        getActivity().startActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                mode.getMenuInflater().inflate(R.menu.crime_fragment_context, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                if (item.getItemId() == R.id.menu_item_delete_photo) {
                                    // ask the user if they want to delete the photo
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.delete_photo_title)
                                            .setMessage(R.string.delete_photo_text)
                                            .setPositiveButton("Yes", mDeletePhotoDialogListener)
                                            .setNegativeButton("No", mDeletePhotoDialogListener)
                                            .show();
                                }
                                mode.finish();
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {

                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
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

        // suspect button
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        // set the button text to the suspect name
        if (mCrime.getSuspect() != null ) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Crime report button
        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an implicit intent
                Intent i = new Intent(Intent.ACTION_SEND);
                // plain text MIME type
                i.setType("text/plain");
                // put the crime report in the intent
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                // set the subject
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                // create a chooser
                i = Intent.createChooser(i, getString(R.string.send_report));
                // start the desired activity
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        getActivity().getMenuInflater().inflate(R.menu.crime_fragment_context, menu);
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
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); // set this fragment as the target for the created fragment
                    dialog.show(fm, DIALOG_DATE);  // show the dialog
                }
                else {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME); // set this fragment as the target for the created fragment
                    dialog.show(fm, DIALOG_TIME);  // show the dialog
                }
            }
            // if there was a picture taken
            if ( requestCode == REQUEST_PHOTO ) {
                // create a new photo object and attach it to the crime
                String fileName = intent.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                int rotation = intent.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ROTATION, Surface.ROTATION_90);
                if ( fileName != null ) {
                    // get old crime photo if any and delete it
                    deletePhoto();

                    Photo photo = new Photo(fileName, rotation);
                    mCrime.setPhoto(photo);
                    showPhoto();
                    Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
                }
            }
            if ( requestCode == REQUEST_CONTACT ) {
                Uri contactUri = intent.getData();

                // specify the fields you want
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };
                // perform the query
                // the contactUri is like a "where" clause here
                Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

                // make sure we got data
                if ( c.getCount() == 0 ) {
                    c.close();
                    return;
                }

                // pull out first column of first row
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                c.close();
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

    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable drawable = null;
        if ( p != null ) {
            String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
            int rotation = p.getRotation();
            drawable = PictureUtils.getScaledDrawable(getActivity(), path, rotation);
        }
        mPhotoView.setImageDrawable(drawable);
    }

    private void deletePhoto() {
        Photo oldPhoto = mCrime.getPhoto();
        if ( oldPhoto != null ) {
            getActivity().getFileStreamPath(oldPhoto.getFileName()).delete();
            mCrime.setPhoto(null);
            mPhotoView.setImageDrawable(null);
        }
    }

    private String getCrimeReport() {
        // is the crime solved?
        String solvedString = mCrime.isSolved()  ? getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        // format the date
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        // is there a suspect?
        String suspect = mCrime.getSuspect();
        suspect = (suspect == null) ? getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, suspect);

        // construct the report
        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateAsString());
    }

}
