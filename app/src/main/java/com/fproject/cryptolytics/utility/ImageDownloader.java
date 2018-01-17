package com.fproject.cryptolytics.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Responsable for downloading a Bitmap from the specified URL
 * and displaying it in the specified ImageView.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private final Context context;
    private final WeakReference<ImageView> imageView;

    public ImageDownloader(ImageView imageView) {
        this.imageView = new WeakReference<ImageView>(imageView);
        this.context   = imageView.getContext();
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        if (url[0] == null) {
            return null;
        }

        String fileName = Uri.encode(url[0]);
        Bitmap bitmap   = null;

        if (isCancelled()){
            return null;
        }

        if (bitmapOnDisk(fileName)) {
            bitmap = bitmapFromFile(fileName);
        }
        else {
            bitmap = bitmapFromUrl(url[0]);

            if (bitmap != null){
                bitmapToFile(bitmap, fileName);
            }


        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = this.imageView.get();

        if (isCancelled()) {
            imageView.setImageDrawable(null);
            return;
        }

        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        } else  {
            imageView.setImageDrawable(null);
        }
    }

    /**
     * Determines weather the bitmap was already downloaded.
     */
    private boolean bitmapOnDisk(String fileName){
        File file = new File(context.getFilesDir(), fileName);

        return (file.exists());
    }

    /**
     * Download the image from the specified url.
     */
    private Bitmap bitmapFromUrl(String url) {
        Bitmap bitmap = null;

        try {

             InputStream inputStream = (InputStream) new URL(url).getContent();
             bitmap = BitmapFactory.decodeStream(inputStream);

        }catch (Exception exception) {
            Log.d(getClass().getName(), " bitmapFromUrl(): " + exception.toString() );
        }

        return bitmap;
    }

    /**
     * Loads the specified bitmap from a file.
     */
    private Bitmap bitmapFromFile(String fileName) {

        File file = new File(context.getFilesDir(),fileName);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        return bitmap;
    }

    /**
     * Write the specified bitmap into a file.
     */
    private boolean bitmapToFile(Bitmap bitmap, String fileName) {
        File file = null;

        try {

            file = new File(context.getFilesDir(), fileName);
            file.createNewFile();
        }
        catch (IOException exception){
            Log.d(getClass().getName(), "bitmapToFile() " + exception.toString());

            return false;
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] bytes = bitmapToBytes(bitmap);

            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException exception) {
            Log.d(getClass().getName(), "bitmapToFile()" + exception.toString());

            return false;
        }

        Log.d(getClass().getName(),file.getAbsolutePath());

        return true;
    }

    /**
     * Convert a bitmap into an array of bytes.
     */
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , outputStream);

        return outputStream.toByteArray();
    }

}
