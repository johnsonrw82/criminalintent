package com.jrw82.android.criminalintent;

/**
 * Created by johnsonrw82 on 4/29/2015.
 */
public class ExternalStorageNotAvailableException extends RuntimeException {
    public ExternalStorageNotAvailableException() {

    }

    public ExternalStorageNotAvailableException(String message) {
        super(message);
    }
}
