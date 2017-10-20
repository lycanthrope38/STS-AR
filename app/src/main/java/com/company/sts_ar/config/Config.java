package com.company.sts_ar.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by thong.le on 10/18/2017.
 */

public @interface Config {
    String PROJECT_JSON = "project.json";
    String DIRECTORY_NAME = "STS-AR";
    String DIRECTORY_PATH = Environment.getExternalStorageDirectory() + File.separator + Config.DIRECTORY_NAME;
}
