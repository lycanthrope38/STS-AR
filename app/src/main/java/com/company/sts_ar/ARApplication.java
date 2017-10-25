package com.company.sts_ar;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.company.sts_ar.config.Config;
import com.company.sts_ar.di.ApplicationComponent;
import com.company.sts_ar.di.ApplicationModule;
import com.company.sts_ar.di.DaggerApplicationComponent;

import java.io.File;

import timber.log.Timber;

/**
 * Created by thong.le on 10/17/2017.
 */

public class ARApplication extends Application {


    private ApplicationComponent mAppComponent;
    private static ARApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        File directory = new File(Config.DIRECTORY_PATH);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mAppComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

    }

    public ApplicationComponent getAppComponent() {
        return mAppComponent;
    }

    public static synchronized ARApplication getInstance() {
        return sInstance;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

}
