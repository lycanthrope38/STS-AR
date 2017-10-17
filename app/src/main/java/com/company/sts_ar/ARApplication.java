package com.company.sts_ar;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by thong.le on 10/17/2017.
 */

public class ARApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
