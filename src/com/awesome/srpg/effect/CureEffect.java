package com.awesome.srpg.effect;

import com.awesome.game.base.Actor;
import com.awesome.game.base.Animation;
import com.awesome.game.base.RenderUtil;
import com.awesome.game.base.Screen;
import com.awesome.srpg.logic.Damage;
import com.awesome.srpg.object.Unit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class CureEffect extends Actor implements Animation {

	private static final String TEX_NAME = "game_effect_blue_256.png";

	private final float STAGE_SPAN;
	private final float SIZE;

	private final float TIME = 0.25f;
	private final int COUNT = 6;
	private final float END_DELAY = 0.5f;

	private boolean started = false;
	private boolean finished = false;

	private Vector3 start;
	private Vector3 end;

	private Unit target;
	private Damage damage;

	private Pixmap purePix;
	private AnimDecal[] trails;


	private float now = 0.0f;

	public CureEffect(Screen screen, Vector3 start, float stageSpan, Unit target, Damage damage) {
		super(screen);

		STAGE_SPAN = stageSpan;
		SIZE = stageSpan * 3.0f;

		this.start = start;
//		this.end = start.cpy().add(new Vector3(0, stageSpan, 0));
		this.end = start.cpy().add(0, stageSpan / 2, 0);

		this.target = target;
		this.damage = damage;

		purePix = RenderUtil.loadPixmap(TEX_NAME);

		trails = new AnimDecal[COUNT];
		for(int i = 0; i < COUNT; i++) {

			Texture tex = new Texture(purePix);
			Decal decal = Decal.newDecal(SIZE, SIZE, new TextureRegion(tex), true);
			decal.setBlending(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
			decal.rotateX(-90);
			AnimDecal a = trails[i] = new AnimDecal(tex, decal, end, TIME, END_DELAY * i / COUNT);
//			a.initMove(start);
			a.disableDepthTest();
			a.initMug(stageSpan / 3, stageSpan / 3);
			a.initColor(new Color(1.0f, 1.0f, 1.0f, 0.0f), new Color(1.0f, 1.0f, 1.0f, 1.0f));

		}
	}

	@Override
	public void dispose() {
		for(AnimDecal a : trails)
			a.dispose();
		super.dispose();
	}

	@Override
	public void start() {
		started = true;
	}

	@Override
	public boolean isDone() {
		return finished;
	}

	@Override
	public void update(float delta) {
		if(now > TIME + END_DELAY) {
			finish();
			return;
		}

		if(!started || finished)
			return;

		now += delta;

		for(AnimDecal t : trails)
			t.update(delta);
	}

	@Override
	public void render(GL10 gl) {
		if(trails != null)
			for(AnimDecal t : trails)
				t.render();
	}

	private void finish() {
		finished = true;
		target.exertDamage(damage);
	}
}
