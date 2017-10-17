package com.company.sts_ar.loader;

public interface Object3D {

	// number of coordinates per vertex in this array
	int COORDS_PER_VERTEX = 3;
	int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	float[] DEFAULT_COLOR = { 1.0f, 0.0f, 0, 1.0f };

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId);

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int drawType, int drawSize, int textureId);
}