package com.awesome.srpg.object;

import com.awesome.game.base.Actor;
import com.awesome.game.base.Screen;
import com.awesome.srpg.logic.StageCell;
import com.awesome.srpg.logic.UnitManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;

public class Stage extends Actor {

	public static final boolean VIEW = false;

	public final float SCALE = 2;
	public final short LINES;
	public final float SPAN;

	final float cubeHeight = 0.05f;
	float[] cubeVerts = {
			// bottom
			0,		-cubeHeight,	0,
			0,		-cubeHeight,	1,
			1,		-cubeHeight,	1,
			1,		-cubeHeight,	0,
			// top
			0,		0,		0,
			0,		0,		1,
			1,		0,		1,
			1,		0,		0,
			// back
			0,		0,		0,
			0,		-cubeHeight,	0,
			1,		-cubeHeight,	0,
			1,		0,		0,
			// face
			0,		-cubeHeight,	1,
			0,		0,		1,
			1,		0,		1,
			1,		-cubeHeight,	1,
			// left
			0,		0,		1,
			0,		-cubeHeight,	1,
			0,		-cubeHeight,	0,
			0,		0,		0,
			// right
			1,		-cubeHeight,	0,
			1,		-cubeHeight,	1,
			1,		0,		1,
			1,		0,		0,
	};

/*	float[] cubeVerts = {
			// bottom
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f,  0.5f,
			0.5f, -0.5f,  0.5f,
			0.5f, -0.5f, -0.5f,
			// top
			-0.5f,  0.5f, -0.5f,
			-0.5f,  0.5f,  0.5f,
			0.5f,  0.5f,  0.5f,
			0.5f,  0.5f, -0.5f,
			// back
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			0.5f,  0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			// face
			-0.5f, -0.5f, 0.5f,
			-0.5f,  0.5f, 0.5f,
			0.5f,  0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			// left
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f,  0.5f, -0.5f,
			// right
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f,  0.5f,
			0.5f,  0.5f,  0.5f,
			0.5f,  0.5f, -0.5f,
	};*/

	float[] cubeNormals = {
			// bottom
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			// top
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			// back
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			// face
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			// left
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			//right
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
	};

	short[] cubeIndices = {
			// bottom
			0, 2, 1,
			0, 3, 2,
			// top
			4, 5, 6,
			4, 6, 7,
			// back
			8, 9, 10,
			8, 10, 11,
			// face
			12, 15, 14,
			12, 14, 13,
			// left
			16, 17, 18,
			16, 18, 19,
			// right
			20, 23, 22,
			20, 22, 21
	};

	float[] lineVerts;
	float[] lineIndices;

	Mesh cubeMesh;
	Mesh lineMesh;

	StageCell[][] cells;
	UnitManager manager;

	public Stage(Screen screen, int lines, StageCell[][] cells){
		super(screen);

//		SCALE = scale;
		LINES = (short)lines;
		SPAN = SCALE / LINES;

		this.cells = cells;
	}

	private void initCube() {
		arrMul(cubeVerts, SCALE);

		float bottom = Color.toFloatBits(40, 40, 40, 255);
		float top = Color.toFloatBits(240, 240, 240, 255);
		float back = Color.toFloatBits(80, 80, 80, 255);
		float face = Color.toFloatBits(200, 200, 200, 255);
		float left = Color.toFloatBits(120, 120, 120, 255);
		float right = Color.toFloatBits(160, 160, 160, 255);

		float[] cubeColors = {
				bottom, bottom, bottom, bottom,
				top, top, top, top,
				back, back, back, back,
				face, face, face, face,
				left, left, left, left,
				right, right, right, right,
		};

		cubeMesh = new Mesh(true, 24, 36,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Normal, 3, "a_normal"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));

		float[] vertices = new float[24*7];
		int pIdx = 0;
		int nIdx = 0;
		int cIdx = 0;
		for(int i = 0; i < vertices.length; ) {
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeColors[cIdx++];
		}

		cubeMesh.setVertices(vertices);
		cubeMesh.setIndices(cubeIndices);
	}


	private void initLine() {
		final int POINTS = (LINES + 1) * 4;
		lineMesh = new Mesh(true, POINTS, POINTS,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));

		final float Y_HEIGHT = 0.001f;

		lineVerts = new float[POINTS * 3];
		int vIdx = 0;
		for(int i = 0; i <= LINES; i++) {
			lineVerts[vIdx++] = i * SPAN;
			lineVerts[vIdx++] = Y_HEIGHT;
			lineVerts[vIdx++] = 0;

			lineVerts[vIdx++] = i * SPAN;
			lineVerts[vIdx++] = Y_HEIGHT;
			lineVerts[vIdx++] = SCALE;

			lineVerts[vIdx++] = 0;
			lineVerts[vIdx++] = Y_HEIGHT;
			lineVerts[vIdx++] = i * SPAN;

			lineVerts[vIdx++] = SCALE;
			lineVerts[vIdx++] = Y_HEIGHT;
			lineVerts[vIdx++] = i * SPAN;
		}

		float[] lineColors = new float[POINTS];
		for(int i = 0; i < lineColors.length; i++)
			lineColors[i] = Color.toFloatBits(100, 100, 100, 255);

		float[] vertices = new float[POINTS * (3 + 1)];
		vIdx = 0;
		int cIdx = 0;
		for(int i = 0; i < vertices.length;) {
			vertices[i++] = lineVerts[vIdx++];
			vertices[i++] = lineVerts[vIdx++];
			vertices[i++] = lineVerts[vIdx++];
			vertices[i++] = lineColors[cIdx++];
		}

		lineMesh.setVertices(vertices);

		short[] indices = new short[POINTS];
		for(short i = 0; i < indices.length; i++)
			indices[i] = i;

		lineMesh.setIndices(indices);

	}

	public void render(GL10 gl){
		if(VIEW) {
			cubeMesh.render(GL10.GL_TRIANGLES);
			lineMesh.render(GL10.GL_LINES);
		}
	}

	private void arrMul(float[] arr, float mul) {
		for(int i = 0; i < arr.length; i++)
			arr[i] *= mul;
	}

	@Override
	public void dispose() {
		cubeMesh.dispose();
		lineMesh.dispose();
		super.dispose();
	}

	@Override
	public void update(float delta) {
	}

	@Override
	protected void initRender() {
		initCube();
		initLine();
	}

	public Vector3 coordToReal(int cx, int cy) {
		return coordToReal(cx, cy, SPAN);
	}

	static public Vector3 coordToReal(int cx, int cy, float stageSpan) {
		float x, y, z;
		final float HALF = stageSpan / 2;

		x = HALF + cx * stageSpan;
		y = 0;
		z = HALF + cy * stageSpan;

		return new Vector3(x, y, z);
	}
}
