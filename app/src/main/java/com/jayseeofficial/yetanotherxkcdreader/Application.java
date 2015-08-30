package com.jayseeofficial.yetanotherxkcdreader;

import android.content.Context;
import android.util.Log;

import com.jayseeofficial.yetanotherxkcdreader.cache.DiskCache;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;
import com.jayseeofficial.yetanotherxkcdreader.retrofit.XKCDBridge;

import de.greenrobot.event.EventBus;

/**
 * Created by jon on 09/08/15.
 */
public class Application extends android.app.Application {
    private static Context context;
    private static Comic currentComic;

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = this;
        EventBus.getDefault().register(this);
        DiskCache.init();
        XKCDBridge.loadLatestComicAsync();
        //XKCDBridge.loadAllComics();
        XKCDBridge.loadCache();
    }

    public static Context getContext() {
        return context;
    }

    public static Comic getCurrentComic() {
        return currentComic;
    }

    public void onEventBackgroundThread(Object event) {
        Log.d("Event", event.getClass().getName());
    }

    public void onEventBackgroundThread(ComicLoadedEvent event) {
        currentComic = event.getComic();
    }

}
