package com.jrw82.android.criminalintent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

/**
 * Created by johnsonrw82 on 4/29/2015.
 */
public class StorageManager {
    private static final String TAG = "StorageManager";
    public static final String STORAGE_SELECTION_KEY = "com.jrw82.android.criminalintent.storage_selection_key";

    private BroadcastReceiver mStorageBroadcastReceiver;
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWritable = false;

    private StorageType mCurrentStorageType;
    private static StorageManager sStorageManager = null;
    private Context mAppContext;

    private StorageManager(Context context) {
        mCurrentStorageType = StorageType.DEVICE; // default to device storage
        mAppContext = context;
    }

    public static StorageManager getInstance(Context context) {
        if ( sStorageManager == null ) {
            sStorageManager = new StorageManager(context.getApplicationContext());
        }
        return sStorageManager;
    }

    public boolean isUsingDeviceStorage() {
        return mCurrentStorageType == StorageType.DEVICE;
    }

    public boolean isUsingExternalStorage() {
        return mCurrentStorageType == StorageType.EXTERNAL;
    }

    public boolean isExternalStorageAvailable() {
        return mExternalStorageAvailable && mExternalStorageWritable;
    }

    public void setUsingDeviceStorage() {
        Log.i(TAG, "Using Device Storage");
        mCurrentStorageType = StorageType.DEVICE;
    }

    public void setUsingExternalStorage() throws ExternalStorageNotAvailableException {
        updateExternalStorageState();
        if ( mExternalStorageAvailable && mExternalStorageWritable ) {
            Log.i(TAG, "Using External Storage");
            mCurrentStorageType = StorageType.EXTERNAL;
        }
        else {
            throw new ExternalStorageNotAvailableException("External storage not available and/or not writable");
        }
    }

    public void startWatchingExternalStorage() {
        Log.i(TAG, "Watching external storage");

        mStorageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);

        mAppContext.registerReceiver(mStorageBroadcastReceiver, filter);
        updateExternalStorageState();
    }

    public void stopWatchingExternalStorage() {
        Log.i(TAG, "Not watching external storage");
        mAppContext.unregisterReceiver(mStorageBroadcastReceiver);
    }

    private void updateExternalStorageState() {
        String storageState = Environment.getExternalStorageState();
        if ( storageState.equals(Environment.MEDIA_MOUNTED) ) {
            mExternalStorageAvailable = mExternalStorageWritable = true;
        }
        else if ( storageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY ) ) {
            mExternalStorageAvailable = true;
            mExternalStorageWritable = false;
        }
        else {
            mExternalStorageAvailable = mExternalStorageWritable = false;
        }
    }

    private enum StorageType {
        EXTERNAL, DEVICE
    }
}
