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

public class DamageEffect extends Actor implements Animation {

	private static final String TEX_NAME = "game_effect_yellow_256.png";

	private final float STAGE_SPAN;
	private final float SIZE;

	private final float TIME = 0.15f;
	private final float END_RATIO = 2.0f;

	private boolean started = false;
	private boolean done = false;

	private Vector3 start;

	private Unit target;
	private Damage damage;

	private Pixmap purePix;
//	private Texture tex;
//	private Decal decal;
	private AnimDecal anim;

	private float now = 0.0f;

	public DamageEffect(Screen screen, Vector3 end, float stageSpan, Unit target, Damage damage) {
		super(screen);

		STAGE_SPAN = stageSpan;
		SIZE = stageSpan * 1.0f;

		this.start = end;

		this.target = target;
		this.damage = damage;

		target.exertDamage(damage);
	}

	@Override
	public void dispose() {
		anim.dispose();
		super.dispose();
	}

	@Override
	public void start() {
		started = true;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public void update(float delta) {
		if(now > TIME) {
			finish();
			return;
		}

		if(!started || done)
			return;

		now += delta;

		anim.update(delta);

	}

	@Override
	public void render(GL10 gl) {
		if(anim != null)
			anim.render();
	}

	@Override
	protected void initRender() {
		purePix = RenderUtil.loadPixmap(TEX_NAME);

		Texture tex = new Texture(purePix);
		Decal decal = Decal.newDecal(0, 0, new TextureRegion(tex), true);
		decal.setBlending(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		anim = new AnimDecal(tex, decal, AnimDecal.hipHeight(start, STAGE_SPAN), TIME, 0);
		anim.enableBillboard();
		anim.disableDepthTest();
		anim.initMug(SIZE * END_RATIO, SIZE * END_RATIO);
		anim.initColor(new Color(1, 1, 1, 1), new Color(1, 1, 1, 0));
	}

	private void finish() {
		done = true;
	}

}
