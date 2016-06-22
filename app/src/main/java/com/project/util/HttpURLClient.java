package com.project.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
/**
 * Created by guanghaoshao on 16/4/12.
 */
public class HttpURLClient {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();


    private static HttpURLClient mInstance;
    private static final Handler mDelivery;

    static {
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mDelivery = new Handler(Looper.getMainLooper());

    }
    public static HttpURLClient getInstance()
    {
        if (mInstance == null)
        {
            synchronized (HttpURLClient.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpURLClient();
                }
            }
        }
        return mInstance;
    }

    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    public static void enqueue(Request request, Callback responseCallback){
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }
    public void game(){

    }
    public static String enqueue(Request request){

        final String[] s = new String[1];

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override

            public void onFailure(Request request, IOException e) {
                Log.i("tag", "失败");
                s[0] = request.toString();

            }

            @Override
            public void onResponse(Response response) throws IOException {

                Log.i("tag", "成功");

                s[0] = response.body().string().trim();

                Log.i("tag", "  " + response.body().string().toString());

            }
        });
        return s[0];
    }
    private Response _getAsyn(String url) throws IOException
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }
    private void _displayImage(final ImageView view, final String url, final int errorResId)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                setErrorResId(view, errorResId);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                try {
                    is = response.body().byteStream();


                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try {
                        is.reset();
                    } catch (IOException e) {
                        response = _getAsyn(url);
                        is = response.body().byteStream();
                    }

                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    if (FileUtils.isHasSD()) {
                        try {
                            File cache = new File(FileUtils.FILE_CACHE);
                            if (cache.isFile()) {
                                cache.delete();
                            }
                            if (!cache.exists()) {
                                cache.mkdir();
                            }
                            OutputStream stream = new FileOutputStream(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bm);
                        }
                    });
                } catch (Exception e) {
                    setErrorResId(view, errorResId);

                } finally {
                    if (is != null) try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void _displayImage1(final ImageView view, final String url, final int errorResId)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                setErrorResId(view, errorResId);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try {
                        is.reset();
                    } catch (IOException e) {
                        response = _getAsyn(url);
                        is = response.body().byteStream();
                    }
                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bm);
                        }
                    });
                } catch (Exception e) {
                    setErrorResId(view, errorResId);

                } finally {
                    if (is != null) try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private Response response=null;
    private Response _displayImage1(final String url)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            response = mOkHttpClient.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

        return  response;
    }
    public static void displayImage(final ImageView view, String url)
    {
        mInstance._displayImage(view, url, -1);
    }

    public synchronized static Bitmap displayImage2(String url){
        Response response=mInstance._displayImage1(url);
        InputStream is = response.body().byteStream();
        BitmapFactory.Options ops = new BitmapFactory.Options();
        final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
        return bm;
    }
    private void setErrorResId(final ImageView view, final int errorResId) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
    }
}