package com.jayseeofficial.yetanotherxkcdreader.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jayseeofficial.yetanotherxkcdreader.Application;
import com.jayseeofficial.yetanotherxkcdreader.R;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadFailedEvent;
import com.jayseeofficial.yetanotherxkcdreader.event.ComicLoadedEvent;
import com.jayseeofficial.yetanotherxkcdreader.object.Comic;
import com.jayseeofficial.yetanotherxkcdreader.retrofit.XKCDBridge;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class MainActivity extends BaseActivity {

    ProgressDialog loadingDialog;

    @Bind(R.id.imageview)
    ImageViewTouch imageView;

    @Bind(R.id.txt_error)
    TextView txtError;

    void loadRandomComic() {
        XKCDBridge.loadRandomComicAsync();
        loadingDialog.show();
    }

    void loadPreviousComic() {
        Comic currentComic = Application.getCurrentComic();
        if(currentComic==null) return;
        if (currentComic.getNum() > 1) {
            XKCDBridge.loadComicAsync(currentComic.getNum() - 1);
            loadingDialog.show();
        }
    }

    void loadNextComic() {
        Comic currentComic = Application.getCurrentComic();
        if(currentComic==null) return;
        if (currentComic.getNum() < XKCDBridge.getLatestComicNumber()) {
            XKCDBridge.loadComicAsync(currentComic.getNum() + 1);
            loadingDialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        resetLoadingDialog();
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        if (Application.getCurrentComic() != null) {
            loadingDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadComic(Application.getCurrentComic());
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())) {
            case R.id.action_previous_comic:
                loadPreviousComic();
                return true;
            case R.id.action_random_comic:
                loadRandomComic();
                return true;
            case R.id.action_next_comic:
                loadNextComic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void loadComic(final Comic comic) {
        txtError.setVisibility(View.GONE);
        loadingDialog.setProgress(1);
        try {
            final Bitmap bmp = Picasso.with(this).load(comic.getImageUrl()).get();
            loadingDialog.setProgress(2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bmp);
                    setTitle(comic.getTitle());
                    resetLoadingDialog();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetLoadingDialog() {
        if (loadingDialog == null) loadingDialog = new ProgressDialog(this);
        loadingDialog.hide();
        loadingDialog.setMessage("Loading...");
        loadingDialog.setIndeterminate(false);
        loadingDialog.setProgress(0);
        loadingDialog.setMax(2);
        loadingDialog.setCancelable(false);
    }

    public void onEventBackgroundThread(ComicLoadedEvent event) {
        loadComic(event.getComic());
    }

    public void onEventMainThread(ComicLoadFailedEvent event){
        txtError.setText(event.getErrorMessage());
        txtError.setVisibility(View.VISIBLE);
    }

}
