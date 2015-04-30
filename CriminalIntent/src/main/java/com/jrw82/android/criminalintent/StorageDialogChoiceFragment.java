package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

/**
 * Created by johnsonrw82 on 4/29/2015.
 */
public class StorageDialogChoiceFragment extends DialogFragment {
    public static final String EXTRA_CHOICE = "com.jrw82.android.criminalintent.storage_dialog_choice";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.storage_dialog_choice, null);

        final RadioButton deviceRadioButton = (RadioButton) v.findViewById(R.id.storage_device_radioButton);
        final RadioButton extRadioButton = (RadioButton) v.findViewById(R.id.storage_external_radioButton);

        // check to see if external storage is available
        if ( !StorageManager.getInstance(getActivity()).isExternalStorageAvailable() ) {
            extRadioButton.setEnabled(false);  // disable if the storage is not available
            deviceRadioButton.setChecked(true);
        }
        else {
            // set button based on selection
            deviceRadioButton.setChecked(StorageManager.getInstance(getActivity()).isUsingDeviceStorage());
            extRadioButton.setChecked(StorageManager.getInstance(getActivity()).isUsingExternalStorage());
        }

        // listeners
        extRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckChanged(!extRadioButton.isChecked());
            }
        });

        deviceRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckChanged(deviceRadioButton.isChecked());
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.storage_dialog_choice_title)
                .create();
    }

    private void handleCheckChanged(boolean useDeviceStorage) {
        if ( useDeviceStorage ) {
            StorageManager.getInstance(getActivity()).setUsingDeviceStorage();
        }
        else {
            StorageManager.getInstance(getActivity()).setUsingExternalStorage();
        }

        // dismiss dialog
        dismiss();
    }
}
