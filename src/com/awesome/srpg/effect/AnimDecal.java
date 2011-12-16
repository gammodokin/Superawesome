package com.awesome.srpg.effect;

import com.awesome.game.base.Animation;
import com.awesome.game.base.RenderUtil;
import com.awesome.srpg.SRPG;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class AnimDecal implements Animation {

	private final float TIME;

	private Texture tex;
	private Decal decal;

	private Vector3 start;
	private Vector3 target;

	private float startW;
	private float startH;
	private float targetW;
	private float targetH;

	private Color startC;
	private Color targetC;

	private float now;
	private boolean started = false;
	private boolean done = false;

	private boolean depthTest = true;
	private boolean billboard = false;

	private boolean move = false;
	private boolean mug = false;
	private boolean color = false;

	AnimDecal(Texture tex, Decal decal, Vector3 target, float time, float delay) {
		super();

		TIME = time;

		this.target = target;
		decal.setPosition(target.x, target.y, target.z);

		this.tex = tex;
		this.decal = decal;

		now = -delay;
	}

	public void initMove(Vector3 start) {
		move = true;
		this.start = start;
	}

	public void initMug(float targetW, float targetH) {
		mug = true;
		this.targetW = targetW;
		this.targetH = targetH;

		startW = decal.getWidth();
		startH = decal.getHeight();
	}

	public void initColor(Color startC, Color targetC) {
		color = true;
		this.startC = startC;
		this.targetC = targetC;
	}

	public void enableBillboard() {
		billboard = true;
	}

	public void disableDepthTest() {
		depthTest = false;
	}

	@Override
	public void start() {
		started = true;
		SRPG.getRenderer().addDecalToBatch(decal, depthTest);
	}

	public void update(float delta) {
		now += delta;

		if(now < 0)
			return;
		else if(!started)
			start();
		else if(now > TIME) {
			dispose();
			return;
		}

		float ratio = now / TIME;

		if(move) {
			Vector3 v = start.cpy().add(calcPos(ratio));
			decal.setPosition(v.x, v.y, v.z);
		}

		if(mug) {
			decal.setDimensions(calcInterval(startW, targetW, ratio), calcInterval(startH, targetH, ratio));
		}

		if(color) {
			float r = calcInterval(startC.r, targetC.r, ratio);
			float g = calcInterval(startC.g, targetC.g, ratio);
			float b = calcInterval(startC.b, targetC.b, ratio);
			float a = calcInterval(startC.a, targetC.a, ratio);
			decal.setColor(r, g, b, a);
		}
	}

	public void render() {
		if(billboard)
			RenderUtil.setupBillboard(decal);
	}

	@Override
	public void dispose() {
		done = true;
		tex.dispose();
		SRPG.getRenderer().removeDecalFromBatch(decal);
	}

	@Override
	public boolean isDone() {
		return done;
	}

	public static Vector3 hipHeight(Vector3 v, float stageSpan) {
		return v.add(0, stageSpan / 2, 0);
	}

	private Vector3 calcPos(float timeRatio) {
		return target.cpy().sub(start).mul(timeRatio);
	}

	private float calcInterval(float start, float target, float ratio) {
		return (target - start) * ratio + start;
	}

}
