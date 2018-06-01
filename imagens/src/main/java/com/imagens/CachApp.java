package com.imagens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.os.Environment.isExternalStorageRemovable;

/**
 *  Usa para armazenar o Cech de imagem da Aplicação
 * Created by Jetro Domigos on 08/11/2017.
 *
 */

public class CachApp {

    private LruCache<String, Bitmap> lruCache;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
    private static final String DISK_CACHE_SUBDIR = "Cache";
    private  Context mContext;


    CachApp(Context context) {
        mContext= context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 6;

        // inicializar cahe para as imagems
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);
    }

    @SuppressLint("StaticFieldLeak")
    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir,1,1,DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }


    public void addLruCache(Object key, Bitmap bitmap) {

        try {
            if (key instanceof String) {

                if ( getLruCache(key) == null) {
                    lruCache.put((String) key, bitmap);
                }
            }
            if (key instanceof byte[]) {

                if (getLruCache(key.toString()) == null) {
                    lruCache.put(key.toString(), bitmap);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap getLruCache(Object key) {

        if (key instanceof String) {

            return lruCache.get((String) key);
        } else if (key instanceof byte[]) {

            return lruCache.get(key.toString());
        }

        return null;
    }


    public void addDiskLruCache(String key, Bitmap bitmap) throws IOException {

        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
             DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                editor.newOutputStream(0).write(Imagens.getInstance().convertBitmapPraBytArray(bitmap,80));
                editor.commit();
            }
        }
    }

    public Bitmap getDiskLruCache(String key) {

        Bitmap bitmap = null;

        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }

            if (mDiskLruCache != null) {
                byte[] imagebyt;

                try {
                    InputStream in = new BufferedInputStream(mDiskLruCache.get(key).getInputStream(0));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }

                    imagebyt = out.toByteArray();
                    out.close();
                    in.close();
                    bitmap = Imagens.getInstance().converteByArrayPraBitmap(imagebyt);
                } catch (Exception e) {
                    e.printStackTrace();}
            }
        }
        return bitmap;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    private static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
