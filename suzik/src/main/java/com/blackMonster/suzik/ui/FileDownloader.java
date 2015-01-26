package com.blackMonster.suzik.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.blackMonster.suzik.util.NetworkUtils;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
    public static final String SONG_FILE_EXTENSION = ".msk";
    public static final String ALBUM_ART_FILE_EXTENSION = ".psk";



    public static void saveSongToDisk(String title, String artist, String url, String fileName, Context context){

        LOGD(TAG, "download url : " + url);

        if (!NetworkUtils.isValidUrl(url)) return;

        deleteFile(getLocationFromFilename(fileName, context));
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


        if (!NetworkUtils.isValidUrl(url)) {
            LOGE(TAG,"unable to save...inalid path " + url);
            return;
        }


        com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                LOGD(TAG,"onresponse saveImageTODisk ");

                if (loadedImage == null)  {
                    LOGE(TAG,"bitmap null");
                    return;
                }
                LOGE(TAG,"bitmap not null");

                writeToDisk(loadedImage,location);
            }
        });



    }

    public static void writeToDisk(Bitmap bmp , String location) {
     //TODO  Checking external storage availability and space avalilabiity before writing to disk

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
        return "a" + System.currentTimeMillis() + SONG_FILE_EXTENSION;
    }

    public static String getNewAlbumArtName() {return "a" + System.currentTimeMillis() + ALBUM_ART_FILE_EXTENSION; }



}
