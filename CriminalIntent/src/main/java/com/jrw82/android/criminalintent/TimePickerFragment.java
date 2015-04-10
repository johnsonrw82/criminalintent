package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by johnsonrw82 on 4/9/2015.
 */
public class TimePickerFragment extends DatePickerFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        // expand arguments
        Bundle args = getArguments();
        // get date
        mDate = (Date) args.getSerializable(DatePickerFragment.EXTRA_DATE);

        // set the time
        final Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        // get time components
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        // find the time picker
        TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minutes);

        // set listener
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // set new date to preserve time
                mDate = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                        hourOfDay, minute, 0).getTime();

                // change args to preserve during rotation
                getArguments().putSerializable(DatePickerFragment.EXTRA_DATE, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setView(v)
                .create();
    }

    public static TimePickerFragment newInstance(Date d) {
        Bundle args = new Bundle();
        args.putSerializable(DatePickerFragment.EXTRA_DATE, d);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
