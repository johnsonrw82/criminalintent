package com.jrw82.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by johnsonrw82 on 5/3/2015.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "com.jrw82.android.criminalintent.extra_image_path";
    public static final String EXTRA_ROTATION = "com.jrw82.android.criminalintent.extra_rotation";

    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath, int rotation) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putInt(EXTRA_ROTATION, rotation);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        super.onCreateView(inflater, group, savedInstanceState);
        mImageView = new ImageView(getActivity());

        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int rotation = getArguments().getInt(EXTRA_ROTATION);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, rotation);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
