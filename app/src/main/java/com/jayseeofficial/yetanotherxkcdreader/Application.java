package com.jayseeofficial.yetanotherxkcdreader;

import android.util.Log;

import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;
import com.jayseeofficial.yetanotherxkcdreader.retrofit.XKCDBridge;

import de.greenrobot.event.EventBus;

/**
 * Created by jon on 09/08/15.
 */
public class Application extends android.app.Application {
    private static Comic currentComic;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        XKCDBridge.loadLatestComicAsync();
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
