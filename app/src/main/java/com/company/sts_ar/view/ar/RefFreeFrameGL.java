package com.company.sts_ar.view.ar;

import android.content.res.Configuration;
import android.opengl.GLES20;
import android.util.Log;

import com.company.sts_ar.session.ApplicationSession;
import com.company.sts_ar.util.SampleUtils;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.Vec4F;
import com.vuforia.VideoBackgroundConfig;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class RefFreeFrameGL {

    private static final String LOGTAG = "RefFreeFrameGL";

    UserDefinedTargets mActivity;
    ApplicationSession vuforiaAppSession;

    // OpenGL handles for the various shader related variables
    private int shaderProgramID; // The Shaders themselves
    private int vertexHandle; // Handle to the Vertex Array
    private int textureCoordHandle; // Handle to the Texture Coord Array
    private int colorHandle; // Handle to the color vector
    private int mvpMatrixHandle; // Handle to the product of the Projection
    // and Modelview Matrices

    // Projection and Modelview Matrices
    Matrix44F projectionOrtho, modelview;

    // Color vector
    Vec4F color;

    // Vertices, texture coordinates and vector indices
    int NUM_FRAME_VERTEX_TOTAL = 4;
    int NUM_FRAME_INDEX = 1 + NUM_FRAME_VERTEX_TOTAL;

    float frameVertices_viewfinder[] = new float[NUM_FRAME_VERTEX_TOTAL * 3];
    float frameTexCoords[] = new float[NUM_FRAME_VERTEX_TOTAL * 2];
    short frameIndices[] = new short[NUM_FRAME_INDEX];

    // Portrait/Landscape status detected in init()
    boolean isActivityPortrait;

    String frameVertexShader = " \n" + "attribute vec4 vertexPosition; \n"
            + "attribute vec2 vertexTexCoord; \n" + "\n"
            + "varying vec2 texCoord; \n" + "\n"
            + "uniform mat4 modelViewProjectionMatrix; \n" + "\n"
            + "void main() \n" + "{ \n"
            + "gl_Position = modelViewProjectionMatrix * vertexPosition; \n"
            + "texCoord = vertexTexCoord; \n" + "} \n";

    String frameFragmentShader = " \n" + "precision mediump float; \n" + "\n"
            + "varying vec2 texCoord; \n" + "\n"
            + "uniform sampler2D texSampler2D; \n" + "uniform vec4 keyColor; \n"
            + "\n" + "void main() \n" + "{ \n"
            + "vec4 texColor = texture2D(texSampler2D, texCoord); \n"
            + "gl_FragColor = keyColor * texColor; \n" + "} \n" + "";


    public RefFreeFrameGL(UserDefinedTargets activity,
                          ApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
        shaderProgramID = 0;
        vertexHandle = 0;
        textureCoordHandle = 0;
        mvpMatrixHandle = 0;

        Log.d(LOGTAG, "RefFreeFrameGL Ctor");

        color = new Vec4F();
    }


    void setColor(float c[]) {
        if (c.length != 4)
            throw new IllegalArgumentException(
                    "Color length must be 4 floats length");

        color.setData(c);
    }



    boolean init(int screenWidth, int screenHeight) {
        float tempMatrix44Array[] = new float[16];
        // modelview matrix set to identity
        modelview = new Matrix44F();

        tempMatrix44Array[0] = tempMatrix44Array[5] = tempMatrix44Array[10] = tempMatrix44Array[15] = 1.0f;
        modelview.setData(tempMatrix44Array);

        // color is set to pure white
        float tempColor[] = {1.0f, 1.0f, 1.0f, 0.6f};
        color.setData(tempColor);

        // Detect if we are in portrait mode or not
        Configuration config = mActivity.getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            isActivityPortrait = false;
        else
            isActivityPortrait = true;

        if ((shaderProgramID = SampleUtils.createProgramFromShaderSrc(
                frameVertexShader, frameFragmentShader)) == 0)
            return false;

        if ((vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexPosition")) == -1)
            return false;
        if ((textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexTexCoord")) == -1)
            return false;
        if ((mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "modelViewProjectionMatrix")) == -1)
            return false;
        if ((colorHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "keyColor")) == -1)
            return false;

        // retrieves the screen size and other video background config values
        Renderer renderer = Renderer.getInstance();
        VideoBackgroundConfig vc = renderer.getVideoBackgroundConfig();

        // makes ortho matrix
        projectionOrtho = new Matrix44F();
        for (int i = 0; i < tempMatrix44Array.length; i++) {
            tempMatrix44Array[i] = 0;
        }

        int tempVC[] = vc.getSize().getData();

        // Calculate the Orthograpic projection matrix
        tempMatrix44Array[0] = 2.0f / (float) (tempVC[0]);
        tempMatrix44Array[5] = 2.0f / (float) (tempVC[1]);
        tempMatrix44Array[10] = 1.0f / (-10.0f);
        tempMatrix44Array[11] = -5.0f / (-10.0f);
        tempMatrix44Array[15] = 1.0f;

        // Viewfinder size based on the Ortho matrix because it is an Ortho UI
        // element use the ratio of the reported screen size and the calculated
        // screen size to account for on screen OS UI elements such as the 
        // action bar in ICS.
        float sizeH_viewfinder = ((float) screenWidth / tempVC[0])
                * (2.0f / tempMatrix44Array[0]);
        float sizeV_viewfinder = ((float) screenHeight / tempVC[1])
                * (2.0f / tempMatrix44Array[5]);

        Log.d(LOGTAG, "Viewfinder Size: " + sizeH_viewfinder + ", "
                + sizeV_viewfinder);

        // ** initialize the frame with the correct scale to fit the current
        // perspective matrix
        int cnt = 0, tCnt = 0;

        // Define the vertices and texture coords for a triangle strip that will
        // define the Quad where the viewfinder is rendered.
        //
        // 0---------1 | | | | 3---------2

        // / Vertex 0
        frameVertices_viewfinder[cnt++] = (-1.0f) * sizeH_viewfinder;
        frameVertices_viewfinder[cnt++] = (1.0f) * sizeV_viewfinder;
        frameVertices_viewfinder[cnt++] = 0.0f;
        frameTexCoords[tCnt++] = 0.0f;
        frameTexCoords[tCnt++] = 1.0f;

        // / Vertex 1
        frameVertices_viewfinder[cnt++] = (1.0f) * sizeH_viewfinder;
        frameVertices_viewfinder[cnt++] = (1.0f) * sizeV_viewfinder;
        frameVertices_viewfinder[cnt++] = 0.0f;
        frameTexCoords[tCnt++] = 1.0f;
        frameTexCoords[tCnt++] = 1.0f;

        // / Vertex 2
        frameVertices_viewfinder[cnt++] = (1.0f) * sizeH_viewfinder;
        frameVertices_viewfinder[cnt++] = (-1.0f) * sizeV_viewfinder;
        frameVertices_viewfinder[cnt++] = 0.0f;
        frameTexCoords[tCnt++] = 1.0f;
        frameTexCoords[tCnt++] = 0.0f;

        // / Vertex 3
        frameVertices_viewfinder[cnt++] = (-1.0f) * sizeH_viewfinder;
        frameVertices_viewfinder[cnt++] = (-1.0f) * sizeV_viewfinder;
        frameVertices_viewfinder[cnt++] = 0.0f;
        frameTexCoords[tCnt++] = 0.0f;
        frameTexCoords[tCnt++] = 0.0f;

        // we also set the indices programmatically
        cnt = 0;
        for (short i = 0; i < NUM_FRAME_VERTEX_TOTAL; i++)
            frameIndices[cnt++] = i; // one full loop
        frameIndices[cnt++] = 0; // close the loop

        //load textures

        return true;
    }

}
