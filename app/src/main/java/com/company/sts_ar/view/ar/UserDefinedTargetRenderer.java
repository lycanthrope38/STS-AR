/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.company.sts_ar.view.ar;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import com.company.sts_ar.loader.GLUtil;
import com.company.sts_ar.loader.Object3D;
import com.company.sts_ar.loader.Object3DBuilder;
import com.company.sts_ar.loader.Object3DData;
import com.company.sts_ar.loader.SceneLoader;
import com.company.sts_ar.session.AppRenderer;
import com.company.sts_ar.session.AppRendererControl;
import com.company.sts_ar.session.ApplicationSession;
import com.company.sts_ar.util.SampleUtils;
import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


// The renderer class for the ImageTargetsBuilder sample.
public class UserDefinedTargetRenderer implements GLSurfaceView.Renderer, AppRendererControl {
    private static final String LOGTAG = "user-defined";

    private ApplicationSession vuforiaAppSession;
    private AppRenderer mAppRenderer;

    private boolean mIsActive = false;

    // Reference to main activity
    private UserDefinedTargets mActivity;

    private Object3DBuilder drawer;
    private SceneLoader scene;
    private Map<byte[], Integer> textures = new HashMap<>();

    private int mCurrentPage = 0;


    public UserDefinedTargetRenderer(UserDefinedTargets activity,
                                     ApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;

        // AppRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mAppRenderer = new AppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 10f, 5000f);
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mAppRenderer.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call function to update rendering when render surface
        // parameters have changed:
        mActivity.updateRendering();

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mAppRenderer.onConfigurationChanged(mIsActive);

        // Call function to initialize rendering:
        initRendering();
    }


    public void setActive(boolean active) {
        mIsActive = active;

        if (mIsActive)
            mAppRenderer.configureVideoBackground();
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;

        // Call our function to render content from AppRenderer class
        mAppRenderer.render();

        scene = mActivity.getScene();
        if (scene == null) {
            // scene not ready
            return;
        }
    }


    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by AppRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix) {

        // Renders video background replacing Renderer.DrawVideoBackground()
        mAppRenderer.renderVideoBackground();

        mActivity.currentPageObserver()
                .observeOn(Schedulers.computation())
                .doOnNext(page -> {
                    Log.d(LOGTAG, "page received: " + page);
                })
                .subscribe(page -> mCurrentPage = page);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Render the RefFree UI elements depending on the current state
        mActivity.refFreeFrame.render();

        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            Log.d(LOGTAG, "trackable result");
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            Matrix44F modelViewMatrix_Vuforia = Tool
                    .convertPose2GLMatrix(trackableResult.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            List<Object3DData> objects = scene.getObjects();
            for (int i = 0; i < 1; i++) {
                try {
                    Object3DData objData = objects.get(mCurrentPage);

                    Object3D drawerObject = drawer.getDrawer(objData, true);

                    Integer textureId = textures.get(objData.getTextureData());
                    if (textureId == null && objData.getTextureData() != null) {
                        ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
                        textureId = GLUtil.loadTexture(textureIs);
                        textureIs.close();
                        textures.put(objData.getTextureData(), textureId);
                    }
                    drawerObject.draw(objData, projectionMatrix, modelViewMatrix,
                            textureId != null ? textureId : -1);

                } catch (IOException ex) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            "There was a problem creating 3D object", Toast.LENGTH_LONG).show();
                }
            }

            SampleUtils.checkGLError("UserDefinedTargets renderFrame");
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        Renderer.getInstance().end();
    }


    private void initRendering() {
        Log.d(LOGTAG, "initRendering");

        scene = mActivity.getScene();
        drawer = new Object3DBuilder();

        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);
    }

}
