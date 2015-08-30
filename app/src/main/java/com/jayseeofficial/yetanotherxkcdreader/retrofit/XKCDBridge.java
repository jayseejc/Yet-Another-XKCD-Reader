package com.jayseeofficial.yetanotherxkcdreader.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jayseeofficial.yetanotherxkcdreader.Application;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadFailedEvent;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Scanner;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jon on 09/08/15.
 */
public class XKCDBridge {

    private static final String TAG = XKCDBridge.class.getSimpleName();

    private static ArrayMap<Integer, Comic> cachedComicMap = new ArrayMap<>();
    private static int latestComic = -1;

    private static AsyncTask<Void, Integer, Boolean> loadCacheTask = new AsyncTask<Void, Integer, Boolean>() {
        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = Application.getContext();
            File filesDir = context.getFilesDir();
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".json");
                }
            };

            Gson gson = new Gson();
            int total = 0;
            long currentTime = System.currentTimeMillis();
            for (String filename : filesDir.list(filter)) {
                try {
                    StringBuilder builder = new StringBuilder();
                    Scanner in = new Scanner(new FileInputStream(new File(filesDir, filename)));
                    while (in.hasNext()) {
                        builder.append(in.nextLine());
                    }
                    try {
                        Comic comic = gson.fromJson(builder.toString(), Comic.class);
                        cachedComicMap.put(comic.getNum(), comic);
                        total++;
                    } catch (JsonSyntaxException exception) {
                        Log.d(TAG, "Invallid json: " + builder.toString());
                    } catch (Exception ex) {
                        Log.e(TAG, "Error loading " + filename);
                        String number = filename.substring(0, filename.indexOf(".json"));
                        loadComicAsync(Integer.parseInt(number));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Loaded metadata for " + total + " comics in " + (System.currentTimeMillis() - currentTime) + "ms");
            return true;
        }
    };

    public static void loadLatestComicAsync() {
        if (latestComic != -1) {
            loadComicAsync(latestComic);
        }
        XKCDServiceFactory.getXKCDService().getLatestComic(new StandardCallback(-1));
    }

    public static void loadComicAsync(int id) {
        Comic comic = cachedComicMap.get(id);
        if (comic != null) {
            EventBus.getDefault().post(new ComicLoadedEvent(comic));
        } else {
            XKCDServiceFactory.getXKCDService().getComic(id, new StandardCallback(id));
        }
    }

    public static void loadRandomComicAsync() {
        // need to know the latest comic to know our random's range
        if (latestComic == -1) {
            EventBus.getDefault().register(new Object() {
                public void onEventBackgroundThread(ComicLoadedEvent event) {
                    loadRandomComicAsync();
                    EventBus.getDefault().unregister(this);
                }
            });
            loadLatestComicAsync();
        } else {
            Random random = new Random();
            // There is no XKCD #404. That's just the standard 404 page for the site.
            int rnum = -1;
            do {
                rnum = random.nextInt(latestComic);
            } while (rnum == 404);
            loadComicAsync(rnum);
        }
    }

    public static int getLatestComicNumber() {
        return latestComic;
    }

    public static void loadAllComics() {
        final XKCDService service = XKCDServiceFactory.getXKCDService();
        Callback<Comic> callback = new LoadAllCallback(service);
        service.getLatestComic(callback);
    }

    public static void loadCache() {
        if (loadCacheTask.getStatus() != AsyncTask.Status.FINISHED || loadCacheTask.getStatus() != AsyncTask.Status.RUNNING)
            loadCacheTask.execute();
    }

    public static Bitmap loadComicImage(Context context, Comic comic) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, comic.getNum() + ".jpg");
        try {
            if (imageFile.exists()) {
                Log.d(TAG,"Loading #"+comic.getNum()+" from disk");
                return Picasso.with(context).load(imageFile).get();
            } else {
                Log.d(TAG,"Loading #"+comic.getNum()+" from network");
                return Picasso.with(context).load(comic.getImageUrl()).get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class StandardCallback implements Callback<Comic> {
        private int id;

        public StandardCallback(int comicId) {
            id = comicId;
        }

        @Override
        public void success(Comic comic, Response response) {
            cachedComicMap.put(comic.getNum(), comic);
            if (id != comic.getNum()) latestComic = comic.getNum();
            EventBus.getDefault().post(new ComicLoadedEvent(comic));
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.getDefault().post(new ComicLoadFailedEvent(id, error.getMessage()));
        }
    }

    private static class LoadAllCallback implements Callback<Comic> {
        private XKCDService service;
        private static int currentComic;

        public LoadAllCallback(XKCDService service) {
            this.service = service;
        }

        @Override
        public void success(final Comic comic, Response response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File filesDir = Application.getContext().getFilesDir();

                        Bitmap bmp = Picasso.with(Application.getContext()).load(comic.getImageUrl()).get();
                        OutputStream imageOutputStream = new FileOutputStream(
                                new File(filesDir, comic.getNum() + ".jpg"));
                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, imageOutputStream);
                        imageOutputStream.close();

                        FileWriter gsonWriter = new FileWriter(new File(Application.getContext().getFilesDir(), comic.getNum() + ".json"));
                        gsonWriter.write(new Gson().toJson(comic));
                        gsonWriter.close();

                        Log.i(TAG, "Saved comic #" + comic.getNum());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (comic.getNum() == 1) {
                        return;
                    } else {
                        int num = comic.getNum() - 1;
                        // There is no XKCD #404. That's just the standard 404 page for the site.
                        while (cachedComicMap.get(num) != null || num == 404) num--;
                        currentComic = num;
                        service.getComic(num, LoadAllCallback.this);
                    }
                }
            }).start();
            cachedComicMap.put(comic.getNum(), comic);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(TAG, error.getMessage());
            EventBus.getDefault().post(new ComicLoadFailedEvent(currentComic, error.getMessage()));
        }
    }

}
