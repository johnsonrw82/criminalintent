package com.jrw82.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by johnsonrw82 on 5/3/2015.
 */


public class PictureUtils {
    private static final String TAG = "PictureUtils";
    /**
     * Get a BitmapDrawable that is scaled to the default device view size
     * @param a Activity requesting the drawable
     * @param path The path to the local file
     * @param rotation the rotation constant corresponding to the orientation of the picture
     * @return BitmapDrawable that is scaled to the default device view size
     */
    @SuppressWarnings("deprectation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path, int rotation) {
        Display display = a.getWindowManager().getDefaultDisplay();
        // deprecated, but necessary
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // read in the dimensions of the file on disk
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

        // handle rotation of the image
        Matrix matrix = new Matrix();  // used for rotating the image
        matrix.preRotate(rotationToDegrees(rotation));

        // create bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        // adjust bitmap using matrix
        Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(a.getResources(), adjustedBitmap);
    }

    // convert rotation constant to degrees
    private static int rotationToDegrees(int rotation) {
        // if in portrait mode, rotate 90 degrees
        if (rotation == Surface.ROTATION_0) { return 90; }
        // same as above, plus 180
        else if (rotation == Surface.ROTATION_180) {  return 270; }
        // if in reverse landscape, rotate 180
        else if (rotation == Surface.ROTATION_270) {  return 180; }
        // default is landscape
        return 0;
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
