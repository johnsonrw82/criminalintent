package com.jrw82.android.criminalintent;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

/**
 * Created by johnsonrw82 on 5/2/2015.
 */
public class CrimeCameraFragment extends Fragment {
    private static String TAG = "CrimeCameraFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        super.onCreateView(inflater, group, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime_camera, group, false);

        Button cameraButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        // setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated, but are required for pre-3.0 support
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "Error while setting preview display: ", ex);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera != null) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    // determine preview size
                    Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                    // set preview size
                    parameters.setPreviewSize(s.width, s.height);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.startPreview();
                    } catch (Exception ex) {
                        Log.e(TAG, "There was an error starting preview: ", ex);
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // surface can no longer be used to display data
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        return v;
    }

    /**
     * Simple algorithm to determine the best size for use with camera preview
     *
     * @param sizes list of supported Sizes
     * @param width the width of the preview area
     * @param height the height of the preview area
     * @return the best Size fit
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestFit = sizes.get(0);
        int largestArea = bestFit.width * bestFit.height;
        for (Camera.Size size : sizes ) {
            int area = size.width * size.height;
            if ( area > largestArea ) {
                bestFit = size;
                largestArea = area;
            }
        }

        return bestFit;
    }

    private void releaseCamera() {
        if ( mCamera != null ) {
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0); // open first camera available, on API 9 or higher
        }
        else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // release the camera if it is in use
        releaseCamera();
    }
}