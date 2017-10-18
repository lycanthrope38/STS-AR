package com.company.sts_ar.view;

import android.app.Activity;


import com.company.sts_ar.loader.SceneLoader;

import java.io.File;

/**
 * Created by thong.le on 10/11/2017.
 */

public class BaseActivity extends Activity {

    public String paramAssetDir;
    public String paramAssetFilename;
    public String paramFilename;
    public SceneLoader scene;

    public File getParamFile() {
        return getParamFilename() != null ? new File(getParamFilename()) : null;
    }

    public String getParamAssetDir() {
        return paramAssetDir;
    }

    public String getParamAssetFilename() {
        return paramAssetFilename;
    }

    public String getParamFilename() {
        return paramFilename;
    }

    public SceneLoader getScene() {
        return scene;
    }

}
