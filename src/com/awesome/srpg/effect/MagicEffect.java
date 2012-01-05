package com.awesome.srpg.effect;

import com.awesome.game.base.Actor;
import com.awesome.game.base.Animation;
import com.awesome.game.base.RenderUtil;
import com.awesome.game.base.Screen;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.logic.Damage;
import com.awesome.srpg.object.Unit;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class MagicEffect extends Actor implements Animation {

	private static final String TEX_NAME = "game_effect_red_256.png";

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

	private Vector3[] trailPos;

	private float now = 0.0f;

	public MagicEffect(Screen screen, Vector3 start, Vector3 end, float stageSpan, Unit target, Damage damage) {
		super(screen);

		STAGE_SPAN = stageSpan;
		SIZE = stageSpan * 0.8f;

		this.start = start;
		this.end = end;

		this.target = target;
		this.damage = damage;

		purePix = RenderUtil.loadPixmap(TEX_NAME);

		trails = new AnimDecal[COUNT];
		for(int i = 0; i < COUNT; i++) {

			Texture tex = new Texture(purePix);
			Decal decal = Decal.newDecal(SIZE, SIZE, new TextureRegion(tex), true);
			decal.setColor(1.0f, 1.0f, 1.0f, (float)(COUNT - i) / COUNT);
//			decal.setBlending(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
//			decal.setBlending(GL10.GL_ONE_MINUS_DST_COLOR, GL10.GL_ONE);
//			decal.setBlending(GL10.GL_ZERO, GL10.GL_SRC_COLOR);
			AnimDecal a = trails[i] = new AnimDecal(tex, decal, AnimDecal.hipHeight(end.cpy(), stageSpan), TIME, END_DELAY * i / COUNT);
			a.enableBillboard();
			a.initMove(AnimDecal.hipHeight(start.cpy(), stageSpan));
		}

		trailPos = new Vector3[COUNT];
		for(int i = 0; i < COUNT; i++)
			trailPos[i] = start;
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
		SRPG.getRenderer().getAnimationManager().startSerialAnimation(new DamageEffect(screen, end, STAGE_SPAN, target, damage));
	}

}
