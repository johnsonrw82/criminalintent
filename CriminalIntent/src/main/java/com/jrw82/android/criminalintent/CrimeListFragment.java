package com.jrw82.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ryan on 3/31/2015.
 */
public class CrimeListFragment extends ListFragment {
    private static final String TAG = "CrimeListFragment";

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // tell the fragment manager that this fragment has an options menu
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // menu item for new crime
            case R.id.menu_item_new_crime:
                // create crime
                Crime crime = new Crime();
                // get crime lab instance
                CrimeLab.get(getActivity()).addCrime(crime);
                // create a new intent
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                // start the activity
                startActivityForResult(i, 0);
                return true;
            // call superclass implementation if the id is not found
            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
