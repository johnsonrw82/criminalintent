package com.jrw82.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeListFragment extends ListFragment {
    private static final String TAG = "CrimeListFragment";
    private static final String DIALOG_STORAGE_CHOICE = "storage_choice";

    private static final int REQUEST_STORAGE = 0;

    private boolean mSubtitleVisible;

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register receiver for external storage state
        StorageManager.getInstance(getActivity()).startWatchingExternalStorage();

        // tell the fragment manager that this fragment has an options menu
        setHasOptionsMenu(true);

        // set retain instance
        setRetainInstance(true);
        mSubtitleVisible = false;

        // set the title of the activity
        getActivity().setTitle(R.string.crimes_title);
        // get the crimes from the crime lab
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        // create an ArrayAdapter that will be used to display the list items on the device
        //ArrayAdapter<Crime> adapter = new ArrayAdapter<Crime>(getActivity(), android.R.layout.simple_list_item_1, mCrimes);

        // create instance of our custom CrimeAdapter to display the crimes in a custom list
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop watching external storage
        StorageManager.getInstance(getActivity()).stopWatchingExternalStorage();
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState ) {
        // inflate our own view, that specifies the list view and the empty view
        View v = inflater.inflate(R.layout.fragment_crime_list, viewGroup, false);

        // restore options state
        if ( savedInstanceState != null ) {
            if ( savedInstanceState.getBoolean(StorageManager.STORAGE_SELECTION_KEY, true) ) {
                StorageManager.getInstance(getActivity()).setUsingDeviceStorage();
            }
            else {
                StorageManager.getInstance(getActivity()).setUsingExternalStorage();
            }
        }

        // set click listener for the button
        Button newCrimeButton = (Button) v.findViewById(R.id.empty_list_add_new_crime_button);
        newCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add new crime
                handleNewCrimePressed();
            }
        });

        // check to make sure the subtitle is visible already, and show it again
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            if ( mSubtitleVisible ) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }

        // get reference to the list view
        ListView crimeListView = (ListView) v.findViewById(android.R.id.list);
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
            // register for a context menu (Gingerbread and lower)
            registerForContextMenu(crimeListView);
        }
        else {
            // multiple selection context menu
            crimeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            crimeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    // required but not used
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // required but not used
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());

                            // determine the items that are checked
                            for ( int i = adapter.getCount() - 1; i >= 0; i--) {
                                // if the list view item is checked, delete it
                                if ( getListView().isItemChecked(i) ) {
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }

                            mode.finish();
                            return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // required but not used
                }
            });
        }

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // cast the list adapter returned to the custom adapter, and get the item position
        // cast to Crime is not necessary, because CrimeAdapter extends the ArrayAdapter with type parameter Crime
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
        Log.d(TAG, c.getTitle() + " clicked");

        // create a new Intent, that will be used to start the new activity
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        // put the crime id as an extra
        i.putExtra(CrimeFragment.CRIME_ID_EXTRA, c.getId());
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState(Bundle) called");
        outState.putBoolean(StorageManager.STORAGE_SELECTION_KEY, StorageManager.getInstance(getActivity()).isUsingDeviceStorage());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        // if the subtitle is visible and the menu item is found, set proper title for the menu item
        if ( mSubtitleVisible && showSubtitle != null ) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }

        MenuItem chooseStorage = menu.findItem(R.id.menu_item_choose_storage);
        chooseStorage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // launch dialog
                FragmentManager fm = getFragmentManager();
                StorageDialogChoiceFragment dialog = new StorageDialogChoiceFragment();
                dialog.show(fm, DIALOG_STORAGE_CHOICE);
                return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        int position = info.position; // position in the list
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter(); // get reference to the list adapter for this list
        Crime c = adapter.getItem(position);

        // determine the context action
        switch ( menuItem.getItemId() ) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(c);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // menu item for new crime
            case R.id.menu_item_new_crime:
                handleNewCrimePressed();
                return true;
            case R.id.menu_item_show_subtitle:
                // if a subtitle is not present, show it. else, clear
                if (getActivity().getActionBar().getSubtitle() == null ) {
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true; // subtitle is now visible
                    menuItem.setTitle(R.string.hide_subtitle);
                }
                else {
                    getActivity().getActionBar().setSubtitle(null);
                    mSubtitleVisible = false; // subtitle hidden
                    menuItem.setTitle(R.string.show_subtitle);
                }
                return true;
            case R.id.menu_item_choose_storage:
                // launch activity to display dialog


            // call superclass implementation if the id is not found
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    // helper function to add the new crime to the list
    private void handleNewCrimePressed() {
        // create crime
        Crime crime = new Crime();
        // get crime lab instance
        CrimeLab.get(getActivity()).addCrime(crime);
        // create a new intent
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        // start the activity
        startActivityForResult(i, 0);
    }


    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            // call the superclass to properly hook up the adapter
            // 0 used for layout ID since the layout is not predefined
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if a view wasn't supplied, we need to inflate a new one
            if ( convertView == null ) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.crime_list_item, null);
            }

            // configure the view for the active crime
            Crime c = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_crimeTitle);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_crimeDate);
            CheckBox crimeSolvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);

            titleTextView.setText(c.getTitle());
            dateTextView.setText(c.getDateAsString());
            crimeSolvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}
