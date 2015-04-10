package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by johnsonrw82 on 4/7/2015.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.jrw82.android.criminalintent.date";

    protected Date mDate;

    protected void sendResult(int resultCode) {
        if ( getTargetFragment() != null ) {
            Intent i = new Intent();
            i.putExtra(EXTRA_DATE, mDate);

            // call the target fragment's onActivityResult
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        // get the date from the arguments
        Bundle args = getArguments();
        mDate = (Date) args.getSerializable(EXTRA_DATE);

        // create a calendar and initialize it using specified date
        final Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        // integer components making up the date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // create a new date using values passed in to the method
                mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth,
                        c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0).getTime();

                // add new date to the arguments to preserve on rotation
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setView(v)
                .create();
    }

    /**
     * New instance method for creating a DatePickerFragment, with specified Date
     * @param d the Date object
     * @return a DatePickerFragment containing the specified Date
     */
    public static DatePickerFragment newInstance(Date d) {
        // create argument bundle
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, d);

        // create new fragment
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
