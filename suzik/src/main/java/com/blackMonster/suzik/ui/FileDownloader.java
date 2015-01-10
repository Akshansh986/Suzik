package com.blackMonster.suzik.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.blackMonster.suzik.AppController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

/**
 * Created by akshanshsingh on 09/01/15.
 */
public class FileDownloader {
    public static final String TAG = "FileDownloader";

    public static void saveSongToDisk(String title, String artist, String url, String fileName, Context context){

        deleteFile(getLocationFromFilename(fileName,context));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(artist);
        request.setTitle(title);

        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalFilesDir(context, null, fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    public static void saveImageToDisk(String url, final String location) {
        LOGD(TAG,"save to disk : "  + url);

        if (location == null || location.equals("")) {
            LOGE(TAG,"unable to save...inalid path " + url);
            return;
        }

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        ImageLoader.ImageContainer newContainer =   imageLoader.get(url,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LOGE(TAG, "Image load error :  " + error.toString());
                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response,
                                           boolean isImmediate) {



                           LOGD(TAG,"onresponse saveImageTODisk " + isImmediate );

                            if (response.getBitmap() == null)  {
                                LOGE(TAG,"bitmap null");
                                return;
                            }
                        LOGE(TAG,"bitmap not null");

                        writeToDisk(response.getBitmap(),location);

                    }
                });

    }

    public static void saveImageToDiskAndUpdateView(String url, final String location, final ImageView view) {

        if (location == null || location.equals("")) {
            LOGE(TAG,"unable to save...inalid path " + url);
            return;
        }

        AppController.getInstance().getImageLoader().get(url,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LOGE(TAG, "Image load error :  " + error.toString());
                    }

                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response,
                                           boolean isImmediate) {

                        if (response.getBitmap() == null)  return;

                        writeToDisk(response.getBitmap(),location);
                        view.setImageBitmap(response.getBitmap());


                    }
                });



    }

    public static void writeToDisk(Bitmap bmp , String location) {
        LOGD(TAG,"write to disk called");

        if (bmp == null) {
            LOGD(TAG,"bitmap is null");

            return;
        }


        deleteFile(location);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(location);
            if (bmp.compress(Bitmap.CompressFormat.PNG, 100, out))
                LOGD(TAG, "Albumart written successfully");
            else
                LOGE(TAG, "Albumart written failed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static String  getLocationFromFilename(String filename, Context context) {

        LOGD(TAG, context.getExternalFilesDir(null).toString());

        return context.getExternalFilesDir(null).toString() + "/" + filename;

    }

    public static boolean deleteFile(String location) {
        if (location==null || location.equals("")) return false;
        File file = new File(location);
        return file.delete();
    }


    public static boolean doesFileExist(String location) {
       if (location==null || location.equals("")) return false;

        File file = new File(location);
        return file.exists();
    }

    public static String getNewSongFileName() {
        return "a" + System.currentTimeMillis() + ".msk";
    }

    public static String getNewAlbumArtName() {return "a" + System.currentTimeMillis() + ".psk"; }



}
