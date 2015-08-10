package com.jayseeofficial.yetanotherxkcdreader.retrofit;

import com.jayseeofficial.yetanotherxkcdreader.object.Comic;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by jon on 09/08/15.
 */
public interface XKCDService {
    @GET("/info.0.json")
    void getLatestComic(Callback<Comic> callback);

    @GET("/{id}/info.0.json")
    void getComic(@Path("id") int comicId, Callback<Comic> callback);
}
