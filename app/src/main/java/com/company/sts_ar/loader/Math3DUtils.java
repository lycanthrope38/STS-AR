package com.company.sts_ar.loader;

import android.opengl.Matrix;

/**
 * Utility class to calculate 3D stuff
 *
 * @author andresoviedo
 */
public class Math3DUtils {

    /**
     * Calculate face normal
     *
     * @param v0
     * @param v1
     * @param v2
     * @return
     */
    public static float[] calculateFaceNormal2(float[] v0, float[] v1, float[] v2) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        float[] n = new float[]{va[1] * vb[2] - va[2] * vb[1], va[2] * vb[0] - va[0] * vb[2],
                va[0] * vb[1] - va[1] * vb[0]};
        float modul = Matrix.length(n[0], n[1], n[2]);
        float[] vn = new float[]{n[0] / modul, n[1] / modul, n[2] / modul};

        return vn;
    }
}
