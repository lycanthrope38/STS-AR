package com.company.sts_ar;

import android.app.Application;
import android.os.Environment;

import com.company.sts_ar.config.Config;

import java.io.File;

import timber.log.Timber;

/**
 * Created by thong.le on 10/17/2017.
 */

public class ARApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + Config.DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
