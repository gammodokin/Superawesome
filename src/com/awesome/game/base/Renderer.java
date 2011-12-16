package com.awesome.game.base;

import static com.awesome.game.base.RenderUtil.TEXTURE_PATH;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Matrix4;

public class Renderer {

	protected static Renderer instance;

	List<Renderable> renList;

	Map<String, Texture> textureMap = new HashMap<String, Texture>();
	Map<String, Mesh> meshMap = new HashMap<String, Mesh>();
	Map<String, Decal> decalMap = new HashMap<String, Decal>();

	private AnimationManager animManager;

	List<Decal> decals = new LinkedList<Decal>();
	List<Decal> noDepthDecals = new LinkedList<Decal>();
	DecalBatch decalBatch;

	private Camera camera;

	public static Renderer getInstance(GL10 gl) {
		if(instance == null)
			instance = new Renderer(gl);
		return instance;
	}

	protected Renderer(GL10 gl) {
		this.renList = new LinkedList<Renderable>();

		animManager = new AnimationManager();

		initGl(gl);
	}

	private void initGl(GL10 gl) {
		//”wŒi‚ÌƒNƒŠƒA
		gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
		gl.glClearDepthf(1.0f);
	}

	private void setupCamera() {
		camera.position.set(0.59f, 0.50f, 1).mul(4);
		camera.direction.set(-0.5f, -0.7f, -1).nor();
//		camera.lookAt(1, 0, 1);
		camera.update();
		camera.apply(Gdx.gl10);
	}

	private void initDecalBatch(Camera camera) {
		CameraGroupStrategy strategy = new CameraGroupStrategy(camera);
		if(decalBatch == null)
			decalBatch = new DecalBatch(strategy);
		else
			decalBatch.setGroupStrategy(strategy);
	}

	public void addRenderable(Renderable ren) {
		renList.add(ren);
	}

	public void removeRenderable(Renderable ren) {
		renList.remove(ren);
	}

//	private final static String TEXTURE_PATH = "res/model/";
	public void entry(String textureName) {
		Texture texture = new Texture(Gdx.files.internal(TEXTURE_PATH + textureName), true);
		texture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);

		textureMap.put(textureName, texture);
	}

	public void entry(String name, Mesh mesh) {
		meshMap.put(name, mesh);
	}

	public void entryDecal(String textureName, float width, float height) {
		Texture tex = textureMap.get(textureName);
		Decal decal = Decal.newDecal(width, height, new TextureRegion(tex), true);
		decalMap.put(textureName, decal);
	}

	public void addDecalToBatch(String textureName) {
		addDecalToBatch(textureName, true);
	}

	public void addDecalToBatch(String textureName, boolean depth) {
		addDecalToBatch(decalMap.get(textureName), depth);
	}

	public void addDecalToBatch(Decal decal, boolean depth) {
		if(depth)
			decals.add(decal);
		else
			noDepthDecals.add(decal);
	}

	public void removeDecalFromBatch(Decal decal) {
		decals.remove(decal);
		noDepthDecals.remove(decal);
	}

	public Texture getTexture(String name) {
		return textureMap.get(name);
	}

	public Mesh getMesh(String name) {
		return meshMap.get(name);
	}

	public Decal getDecal(String name) {
		return decalMap.get(name);
	}

	public Matrix4 getModelView() {
		return camera.invProjectionView;
	}

	public Camera getCamera() {
		return camera;
	}

	public AnimationManager getAnimationManager() {
		return animManager;
	}

//	private int rensize = 0;
	public void render(GL10 gl) {
//		if(rensize != renList.size()) {
//			System.out.println("\n-----------\nrenList : " + renList + "\n------------\n");
//			rensize = renList.size();
//		}

		animManager.update();

		//”wŒi“h‚è’×‚µ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

		gl.glDisable(GL10.GL_DITHER);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);

		setupCamera();

		for(Renderable ren : renList) {
			ren.render(gl);
		}

		gl.glEnable(GL10.GL_DEPTH_TEST);
		for(Decal d : decals)
			decalBatch.add(d);
		decalBatch.flush();

		gl.glDisable(GL10.GL_DEPTH_TEST);
		for(Decal d : noDepthDecals)
			decalBatch.add(d);
		decalBatch.flush();

	}

	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;
		camera = new PerspectiveCamera(30, 2f * aspectRatio, 2f);// new OrthographicCamera(2f * aspectRatio, 2f);

		initDecalBatch(camera);
	}

	public void dispose() {
		for(Renderable ren : renList) {
			ren.dispose();
		}

		for(Texture t : textureMap.values())
			t.dispose();
		for(Mesh m : meshMap.values())
			m.dispose();
	}

}
