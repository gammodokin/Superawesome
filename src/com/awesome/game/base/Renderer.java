package com.awesome.game.base;

import static com.awesome.game.base.RenderUtil.TEXTURE_PATH;

import java.util.ArrayList;
import java.util.Collections;
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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Matrix4;

public class Renderer {

	protected static Renderer instance;

	private List<Renderable> renList;
	private List<Renderable> renInitList;

	private Map<String, Texture> textureMap = new HashMap<String, Texture>();
	private Map<String, Mesh> meshMap = new HashMap<String, Mesh>();
	private Map<String, Decal> decalMap = new HashMap<String, Decal>();

	private AnimationManager animManager;

	private List<Decal> decals;
	private List<Decal> noDepthDecals;
	private DecalBatch decalBatch;
	private DecalBatch noDepthBatch;

	private List<Sprite2D> sprites;
	private SpriteBatch spriteBatch;

	private Camera camera;

	private static final boolean SYNC = true;

	public static Renderer getInstance(GL10 gl) {
		if(instance == null)
			instance = new Renderer(gl);
		return instance;
	}

	protected Renderer(GL10 gl) {
		if(SYNC) {
			renList = Collections.synchronizedList(new LinkedList<Renderable>());
			renInitList = Collections.synchronizedList(new LinkedList<Renderable>());

			decals = Collections.synchronizedList(new LinkedList<Decal>());
			noDepthDecals = Collections.synchronizedList(new LinkedList<Decal>());

			sprites = Collections.synchronizedList(new LinkedList<Sprite2D>());
		} else {
			renList = new LinkedList<Renderable>();
			renInitList = new LinkedList<Renderable>();

			decals = new LinkedList<Decal>();
			noDepthDecals = new LinkedList<Decal>();

			sprites = new LinkedList<Sprite2D>();
		}

		spriteBatch = new SpriteBatch();

		animManager = new AnimationManager();

		initGl(gl);
	}

	private void initGl(GL10 gl) {
		//îwåiÇÃÉNÉäÉA
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
		if(decalBatch == null) {
			decalBatch = new DecalBatch(strategy);
			noDepthBatch = new DecalBatch(strategy);
		} else {
			decalBatch.setGroupStrategy(strategy);
			noDepthBatch.setGroupStrategy(strategy);
		}
	}

	public void addRenderable(Renderable ren) {
		renInitList.add(ren);
		renList.add(ren);
	}

	public void removeRenderable(Renderable ren) {
		renList.remove(ren);
	}

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

	public void addToSpriteBatch(Sprite2D sprite) {
		sprites.add(sprite);
	}

	public void removeFromSpriteBatch(Sprite2D sprite) {
		sprites.remove(sprite);
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

	// TODO RenderableÇ™ëùÇ¶ÇƒçsÇ©Ç»Ç¢Ç©UnitTestèëÇ≠
	//	private int rensize = 0;
	public void render(GL10 gl) {
		//		if(rensize != renList.size()) {
		//			System.out.println("\n-----------\nrenList : " + renList + "\n------------\n");
		//			rensize = renList.size();
		//		}

		if(SYNC) {
			synchronized (renInitList) {
				for(Renderable ren : renInitList)
					if(!ren.isInitedRender())
						ren.initRendering();

				renInitList.clear();
			}
		} else {
			for(Renderable ren : new ArrayList<Renderable>(renInitList))
				if(!ren.isInitedRender())
					ren.initRendering();

			renInitList.clear();
		}

		animManager.update();

		//îwåiìhÇËí◊Çµ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

		gl.glDisable(GL10.GL_DITHER);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);

		setupCamera();

		if(SYNC) {
			synchronized(renList) {
				for(Renderable ren : renList) {
					ren.render(gl);
				}
			}

			synchronized(decals) {
				for(Decal d : decals)
					decalBatch.add(d);
				gl.glEnable(GL10.GL_DEPTH_TEST);
				decalBatch.flush();
			}

			synchronized(noDepthDecals) {
				for(Decal d : noDepthDecals)
					noDepthBatch.add(d);
				gl.glDisable(GL10.GL_DEPTH_TEST);
				noDepthBatch.flush();
			}

			spriteBatch.begin();
			synchronized (sprites) {
				for(Sprite2D s : sprites)
					s.draw(spriteBatch);
			}
			spriteBatch.end();
		} else {
			for(Renderable ren : new ArrayList<Renderable>(renList)) {
				ren.render(gl);
			}

			for(Decal d : new ArrayList<Decal>(decals))
				decalBatch.add(d);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			decalBatch.flush();

			for(Decal d : new ArrayList<Decal>(noDepthDecals))
				noDepthBatch.add(d);
			gl.glDisable(GL10.GL_DEPTH_TEST);
			noDepthBatch.flush();

			spriteBatch.begin();
			for(Sprite2D s : new ArrayList<Sprite2D>(sprites))
				s.draw(spriteBatch);
			spriteBatch.end();
		}

	}

	// TODO ècÇ™êLÇ—ÇΩÇ∆Ç´â°Ç™èkÇﬁÇÊÇ§Ç…ÇµÇΩÇ¢
	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;

		float vw = 2f * aspectRatio;
		float vh = 2f;

		camera = new PerspectiveCamera(30, vw, vh);	// new OrthographicCamera(2f * aspectRatio, 2f);

		initDecalBatch(camera);
	}

	public void dispose() {
		synchronized (renList) {
			for(Renderable ren : renList) {
				ren.dispose();
			}
		}

		synchronized (sprites) {
			for(Sprite2D s : sprites)
				s.dispose();
		}

		for(Texture t : textureMap.values())
			t.dispose();
		for(Mesh m : meshMap.values())
			m.dispose();
	}

}
