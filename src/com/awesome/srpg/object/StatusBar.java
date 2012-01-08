package com.awesome.srpg.object;

import com.awesome.game.base.Actor;
import com.awesome.game.base.ChangeTracer;
import com.awesome.game.base.RenderUtil;
import com.awesome.game.base.Screen;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.logic.Status;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class StatusBar extends Actor {

	private static final String HP_TEX_NAME = "bar_hp.png";
	private static final String MP_TEX_NAME = "bar_mp.png";

	private final float MAX_WIDTH;
	private final float HEIGHT;

	private Pixmap pix;
	private Texture tex;
	private Decal decal;

	private Pixmap hpPurePix;
	private Pixmap mpPurePix;

	private ChangeTracer<Integer> hpct = new ChangeTracer<Integer>(0);
	private ChangeTracer<Integer> mpct = new ChangeTracer<Integer>(0);

	private Unit unit;

	private Status st;

	protected StatusBar(Screen screen, Unit unit, float stageSpan) {
		super(screen);

		this.unit = unit;
		st = unit.getStatus();
		MAX_WIDTH = stageSpan;
		HEIGHT = stageSpan * 0.2f;
	}


	@Override
	public void update(float delta) {
//		st = unit.getStatus();

		Vector3 rc = unit.getRealCoord();
		if(decal != null)	// ”ñ“¯Šú‚ÌŠÖŒW
		decal.setPosition(rc.x, rc.y + MAX_WIDTH * 1.2f, rc.z);
	}

	@Override
	public void render(GL10 gl) {
//		if(st == null)
//			return;

		if(hpct.isChanged(st.getCurrentHp()) || mpct.isChanged(st.getCurrentMp())) {
			int hpw = (int)Math.round(pix.getWidth() * st.getCurrentHp() / st.getMaxHp());
			int mpw = (int)Math.round(pix.getWidth() * st.getCurrentMp() / st.getMaxMp());

			Pixmap.setBlending(Pixmap.Blending.None);
			pix.setColor(0.0f, 0.0f, 0.0f, 0.0f);
			pix.fill();
			Pixmap.setBlending(Pixmap.Blending.SourceOver);

			pix.drawPixmap(hpPurePix, 0, 0, hpw, hpPurePix.getHeight(), 0, 0, hpw, pix.getHeight() / 2);
			pix.drawPixmap(mpPurePix, 0, 0, mpw, mpPurePix.getHeight(), 0, pix.getHeight() / 2, mpw, pix.getHeight() / 2);
			tex.draw(pix, 0, 0);
		}

		RenderUtil.setupBillboard(decal);
	}

	@Override
	public void dispose() {
		pix.dispose();
		tex.dispose();
		SRPG.getRenderer().removeDecalFromBatch(decal);
		super.dispose();
	}

	@Override
	protected void initRender() {
		RenderUtil.loadPixmap(HP_TEX_NAME);
		RenderUtil.loadPixmap(MP_TEX_NAME);

		hpPurePix = RenderUtil.getPixmap(HP_TEX_NAME);
		mpPurePix = RenderUtil.getPixmap(MP_TEX_NAME);

		pix = new Pixmap(256, 256, Pixmap.Format.RGBA8888);
		tex = new Texture(pix);
		decal = Decal.newDecal(MAX_WIDTH, HEIGHT, new TextureRegion(tex), false);

		SRPG.getRenderer().addDecalToBatch(decal, false);
	}

}
