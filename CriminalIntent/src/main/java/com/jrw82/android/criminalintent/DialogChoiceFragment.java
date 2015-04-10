package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

/**
 * Created by johnsonrw82 on 4/9/2015.
 */
public class DialogChoiceFragment extends DialogFragment {
    public static final String EXTRA_CHOICE = "com.jrw82.android.criminalintent.dialog_choice";

    boolean setDate = true;

    private void sendResult(int resultCode) {
        Intent i = new Intent();
        i.putExtra(EXTRA_CHOICE, setDate);

        // send the result
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

        // dismiss the dialog
        dismiss();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_choice, null);

        Button mChooseDate = (Button) v.findViewById(R.id.dialog_choice_date);
        Button mChooseTime = (Button) v.findViewById(R.id.dialog_choice_time);

        mChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate = true;
                sendResult(Activity.RESULT_OK);
            }
        });

        mChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate = false;
                sendResult(Activity.RESULT_OK);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_choice_title)
                .setView(v)
                .create();
    }
}
