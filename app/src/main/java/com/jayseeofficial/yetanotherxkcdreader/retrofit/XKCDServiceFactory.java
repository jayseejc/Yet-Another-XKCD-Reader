package com.jayseeofficial.yetanotherxkcdreader.retrofit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by jon on 09/08/15.
 */
public class XKCDServiceFactory {

    private static Gson gson;
    private static RestAdapter adapter;

    static {
        setupGson();
        setupAdapter();
    }

    public static XKCDService getXKCDService() {
        if (gson == null) {
            setupGson();
            setupAdapter();
        } else if (adapter == null) {
            setupAdapter();
        }
        return adapter.create(XKCDService.class);
    }

    private static void setupGson() {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private static void setupAdapter() {
        adapter = new RestAdapter.Builder()
                .setEndpoint("https://xkcd.com/")
                .setConverter(new GsonConverter(gson))
                .build();
    }

}
