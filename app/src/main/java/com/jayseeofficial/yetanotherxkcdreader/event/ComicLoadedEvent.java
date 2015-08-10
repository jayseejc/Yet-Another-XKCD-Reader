package com.jayseeofficial.yetanotherxkcdreader.event;

import com.jayseeofficial.yetanotherxkcdreader.object.Comic;

/**
 * Created by jon on 09/08/15.
 */
public class ComicLoadedEvent extends Event {

    private Comic comic;

    public ComicLoadedEvent(Comic comic) {
        this.comic = comic;
    }

    public Comic getComic() {
        return comic;
    }

}
