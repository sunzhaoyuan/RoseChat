package edu.rosehulman.sunz1.rosechat.sys.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * Created by sun on 8/6/17.
 *
 * Get images at an URL
 */

public class GetImgTask extends AsyncTask<String, Void, Bitmap>{

    private static final String TAG = Constants.TAG_ASYNC;
    private PicConsumer mPicConsumer;

    public GetImgTask(PicConsumer activity) {
        mPicConsumer = activity;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];
        Bitmap bitmap;
        InputStream in = null;
        try {
            in = new java.net.URL(urlString).openStream();
            Log.d(TAG, "Worked");
        } catch (IOException e) {
            Log.d(TAG, "ERROR: " + e.toString());
        }
        bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mPicConsumer.onPicLoaded(bitmap);
    }

    public interface PicConsumer {
        void onPicLoaded(Bitmap bitmap);
    }
}
