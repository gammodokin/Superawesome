package com.awesome.game.base;

import java.util.HashMap;
import java.util.Map;

import com.awesome.srpg.SRPG;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class RenderUtil {

	public static final String TEXTURE_PATH = "res/model/";

	private static Map<String, Pixmap> pixmap = new HashMap<String, Pixmap>();

	public static Pixmap loadPixmap(String filename) {
		if(!pixmap.containsKey(filename)) {
			Pixmap pm = new Pixmap(Gdx.files.internal(TEXTURE_PATH + filename));
			pixmap.put(filename, pm);
		}

		return pixmap.get(filename);
	}

	public static Pixmap getPixmap(String filename) {
		Pixmap pm = pixmap.get(filename);
		return pm;
//		return cpyPixmap(pm);
	}

	public static Pixmap cpyPixmap(Pixmap src) {
		Pixmap npm = new Pixmap(src.getWidth(), src.getHeight(), src.getFormat());
		Pixmap.setBlending(Pixmap.Blending.None);
		npm.drawPixmap(src, 0, 0, 0, 0, src.getWidth(), src.getHeight());
		Pixmap.setBlending(Pixmap.Blending.SourceOver);
		return npm;
	}

	public static void setupBillboard(Decal decal) {
		Camera camera = SRPG.getRenderer().getCamera();
		decal.lookAt(camera.position, camera.up);
	}

}
