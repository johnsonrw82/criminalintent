package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by johnsonrw82 on 5/3/2015.
 */


public class PictureUtils {
    /**
     * Get a BitmapDrawable that is scaled to the default device view size
     * @param a Activity requesting the drawable
     * @param path The path to the local file
     * @return BitmapDrawable that is scaled to the default device view size
     */
    @SuppressWarnings("deprectation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Display display = a.getWindowManager().getDefaultDisplay();
        // deprecated, but necessary
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // read in the dimenstions of the file on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // set the input sample size
        int inSampleSize = 1;
        // if the image is larger than the default size, set the sample size according to the dimensions
        if ( srcHeight > destHeight || srcWidth > destWidth ) {
            // if the image is landscape, use height ratio, else use width ratio
            inSampleSize = (srcWidth > srcHeight ) ? Math.round(srcHeight/destHeight) : Math.round(srcWidth/destWidth);
        }

        // new options
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // decode the file
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(a.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView) {
        if ( imageView.getDrawable() instanceof BitmapDrawable ) {
            // clean up for sake of memory
            BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
            b.getBitmap().recycle();
            imageView.setImageDrawable(null);
        }
    }
}
