package com.jayseeofficial.yetanotherxkcdreader.cache;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.jayseeofficial.yetanotherxkcdreader.Application;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by jon on 13/08/15.
 */
public class DiskCache {
    static {
        init();
    }

    static Gson gson;

    static boolean initialized = false;

    private DiskCache() {
    }

    public static void init() {
        if (!initialized) {
            EventBus.getDefault().register(new DiskCache());
            gson = new Gson();
            initialized = true;
        }
    }

    public void onEventBackgroundThread(ComicLoadedEvent event) {
        try {
            saveComic(event.getComic());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void saveComic(Comic comic) throws IOException {
        Bitmap bmp = Picasso.with(Application.getContext()).load(comic.getImageUrl()).get();
        File saveDir = Application.getContext().getFilesDir();
        FileOutputStream out = new FileOutputStream(saveDir.getPath() + "/" + comic.getNum() + ".jpg");
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.close();
        FileWriter writer = new FileWriter(new File(saveDir, comic.getNum() + ".json"));
        writer.write(gson.toJson(comic));
        writer.close();
    }

}
