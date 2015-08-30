package com.jayseeofficial.yetanotherxkcdreader.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.jayseeofficial.yetanotherxkcdreader.BuildConfig;

import de.greenrobot.event.EventBus;

/**
 * Created by jon on 09/08/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Object event) {
        if (BuildConfig.DEBUG)
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Event: " + event.getClass().getSimpleName(),
                    Snackbar.LENGTH_LONG)
                    .show();
    }

}
