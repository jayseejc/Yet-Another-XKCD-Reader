package com.jayseeofficial.yetanotherxkcdreader.event;

/**
 * Created by jon on 09/08/15.
 */
public class ComicLoadFailedEvent extends Event {

    private int id;
    private String message;

    public ComicLoadFailedEvent(int id, String errorMessage) {
        this.id = id;
        this.message = errorMessage;
    }

    public int getId() {
        return id;
    }

    public String getErrorMessage(){
        return message;
    }
}
