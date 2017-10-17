package com.company.sts_ar.loader;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.company.sts_ar.BaseActivity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 *
 * @author andresoviedo
 */
public class SceneLoader {

    /**
     * Default model color: yellow+
     */
    private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
    /**
     * Parent component
     */
    protected final BaseActivity parent;
    /**
     * List of data objects containing info for building the opengl objects
     */
    private List<Object3DData> objects = new ArrayList<Object3DData>();
    /**
     * Object selected by the user
     */
    private Object3DData selectedObject = null;

    /**
     * Light bulb 3d data
     */

    public SceneLoader(BaseActivity main) {
        this.parent = main;
    }

    public void init(String assetDir, String assetName) {

        // Load object
        if (assetDir != null || assetName != null) {

            // Initialize assets url handler
            Handler.assets = parent.getAssets();
            // Handler.classLoader = parent.getClassLoader(); (optional)
            // Handler.androidResources = parent.getResources(); (optional)

            // Create asset url
            final URL url;
            try {
                url = new URL("file:///android_asset/" + assetDir + File.separator + assetName);
            } catch (MalformedURLException e) {
                Log.e("SceneLoader", e.getMessage(), e);
                throw new RuntimeException(e);
            }

            Object3DBuilder.loadV6AsyncParallel(parent, url, parent.getParamFile(), assetDir,
                    assetName, new Object3DBuilder.Callback() {

                        long startTime = SystemClock.uptimeMillis();

                        @Override
                        public void onBuildComplete(Object3DData data) {
                            final String elapsed = (SystemClock.uptimeMillis() - startTime) / 1000 + " secs";
                            makeToastText("Load complete (" + elapsed + ")", Toast.LENGTH_LONG);
                        }

                        @Override
                        public void onLoadComplete(Object3DData data) {
                            data.setColor(DEFAULT_COLOR);
                            data.setScale(new float[]{5f, 5f, 5f});
                            addObject(data);
                        }

                        @Override
                        public void onLoadError(Exception ex) {
                            Log.e("SceneLoader", ex.getMessage(), ex);
                            Toast.makeText(parent.getApplicationContext(),
                                    "There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }

    private void makeToastText(final String text, final int toastDuration) {
        parent.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(parent.getApplicationContext(), text, toastDuration).show();
            }
        });
    }

    protected synchronized void addObject(Object3DData obj) {
        List<Object3DData> newList = new ArrayList<Object3DData>(objects);
        newList.add(obj);
//        objects = new ArrayList<>();
        this.objects = newList;
//		requestRender();
    }

    public synchronized List<Object3DData> getObjects() {
        return objects;
    }

    public Object3DData getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Object3DData selectedObject) {
        this.selectedObject = selectedObject;
    }

}
