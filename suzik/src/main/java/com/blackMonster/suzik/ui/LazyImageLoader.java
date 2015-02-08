package com.blackMonster.suzik.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.blackMonster.suzik.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 07/01/15.
 */
//TODO implemented in complete jugad form, use other library or fix it (problems: I don't understand code | ugly code | causes image flicker | little lag in scrolling
//TODO image loading is seriously not smooth fix it.

public class LazyImageLoader {
    public static final String TAG ="LazyImageLoader";
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();

    Bitmap defaultImage;
    Context context;
    int size;
    public LazyImageLoader(Context context) {
        this.context = context;
        initBMP();
        Resources r = context.getResources();
        size= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.album_art_thumbnail_size), r.getDisplayMetrics());
        LOGD("df", " = " + size);

        defaultImage = decodeSampledBitmapFromResource(context.getResources(), R.drawable.album_art, size, size);
    }

    void initBMP() {
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;

        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptionsCache.inDither = false;

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap getArtworkQuick(Context context, Uri uri, int w, int h) {
        // NOTE: There is in fact a 1 pixel frame in the ImageView used to
        // display this drawable. Take it into account now, so we don't have to
        // scale later.

//        if (album_id < 0) {
//            // This is something that is not in the database, so get the album art directly
//            // from the file.
////            Bitmap bm = getArtworkFromFile(context, null, -1);
////            if (bm != null) {
////                return bm;
////            }
////            return getDefaultArtwork(context);
//            return null;
//        }
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                // Compute the closest power-of-two scale factor
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth>w && nextHeight>h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }



    public static Bitmap getArtwork(Context context, Uri uri) {

//        if (album_id < 0) {
//            // This is something that is not in the database, so get the album art directly
//            // from the file.
////            Bitmap bm = getArtworkFromFile(context, null, -1);
////            if (bm != null) {
////                return bm;
////            }
////            return getDefaultArtwork(context);
//            return null;
//        }

        ContentResolver res = context.getContentResolver();
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
//                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
//                // maybe it never existed to begin with.
//                Bitmap bm = getArtworkFromFile(context, null, album_id);
//                if (bm != null) {
//                    // Put the newly found artwork in the database.
//                    // Note that this shouldn't be done for the "unknown" album,
//                    // but if this method is called correctly, that won't happen.
//
//                    // first write it somewhere
//                    String file = Environment.getExternalStorageDirectory()
//                            + "/albumthumbs/" + String.valueOf(System.currentTimeMillis());
//                    if (ensureFileExists(file)) {
//                        try {
//                            OutputStream outstream = new FileOutputStream(file);
//                            if (bm.getConfig() == null) {
//                                bm = bm.copy(Bitmap.Config.RGB_565, false);
//                                if (bm == null) {
//                                    return getDefaultArtwork(context);
//                                }
//                            }
//                            boolean success = bm.compress(Bitmap.CompressFormat.JPEG, 75, outstream);
//                            outstream.close();
//                            if (success) {
//                                ContentValues values = new ContentValues();
//                                values.put("album_id", album_id);
//                                values.put("_data", file);
//                                Uri newuri = res.insert(sArtworkUri, values);
//                                if (newuri == null) {
//                                    // Failed to insert in to the database. The most likely
//                                    // cause of this is that the item already existed in the
//                                    // database, and the most likely cause of that is that
//                                    // the album was scanned before, but the user deleted the
//                                    // album art from the sd card.
//                                    // We can ignore that case here, since the media provider
//                                    // will regenerate the album art for those entries when
//                                    // it detects this.
//                                    success = false;
//                                }
//                            }
//                            if (!success) {
//                                File f = new File(file);
//                                f.delete();
//                            }
//                        } catch (FileNotFoundException e) {
//                            LOGE(TAG, "error creating file", e);
//                        } catch (IOException e) {
//                            LOGE(TAG, "error creating file", e);
//                        }
//                    }
//                } else {
//                    bm = getDefaultArtwork(context);
//                }
//                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private String data = "";
        boolean shouldDeffer = true;
        boolean isChachedImage;

        public BitmapWorkerTask(ImageView imageView, boolean isChachedImage) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.isChachedImage = isChachedImage;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {

            data = params[0];

            if (isChachedImage) {
                return getCachedBitmap(data);
            }
            else {
                return getArtworkQuick(context,Uri.parse(data),size,size);
            }

//            return getArtwork(context,data);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {


                if (shouldDeffer) {
                    shouldDeffer = false;
                    final Bitmap bmp = bitmap;
                    imageViewReference.get().post(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute(bmp);
                        }
                    });
                    return;
                }

            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }


    }

    private Bitmap getCachedBitmap(String location) {
        if (location==null || location.equals("")) {
            LOGD(TAG,"albumart null or empty location");
            return null;
        }

        File file = new File(location);
        if (file.exists()) {
            LOGD(TAG, "albumart found..setting");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(location, options);
            return  bitmap;
        }

        LOGD(TAG, "albumart not found");
        return null;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.equals("") || !bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


//    public void loadBitmap(int albumId, ImageView imageView) {
//        if (cancelPotentialWork(albumId + "", imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,false);
//            final AsyncDrawable asyncDrawable =
//                    new AsyncDrawable(context.getResources(), defaultImage, task);
//            imageView.setImageDrawable(asyncDrawable);
//            task.execute(albumId+ "");
//        }
//    }

    public void loadBitmap(String location, ImageView imageView, boolean isCached) {
        if (cancelPotentialWork(location, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,isCached);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), defaultImage, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(location);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }






}
