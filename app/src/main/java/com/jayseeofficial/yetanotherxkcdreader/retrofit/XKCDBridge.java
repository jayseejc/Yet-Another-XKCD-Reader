package com.jayseeofficial.yetanotherxkcdreader.retrofit;

import android.support.v4.util.ArrayMap;

import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadFailedEvent;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;

import java.util.Random;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jon on 09/08/15.
 */
public class XKCDBridge {

    private static ArrayMap<Integer, Comic> cachedComicMap = new ArrayMap<>();
    private static int latestComic = -1;

    public static void loadLatestComicAsync() {
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
            loadComicAsync(random.nextInt(latestComic));
        }
    }

    public static int getLatestComicNumber() {
        return latestComic;
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

}
