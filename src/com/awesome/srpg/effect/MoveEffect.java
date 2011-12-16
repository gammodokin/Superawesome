package com.awesome.srpg.effect;

import com.awesome.game.base.Actor;
import com.awesome.game.base.Animation;
import com.awesome.game.base.Screen;
import com.awesome.srpg.object.Stage;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.strategy.Coord;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector3;

public class MoveEffect extends Actor implements Animation {

	private final float TIME = 0.3f;

	private Unit unit;
	private int tx, ty;
	private Coord<Integer>[] path;
	private float stageSpan;

	private boolean started = false;
	private boolean finished = false;

	private float now = 0.0f;

	@SuppressWarnings("unchecked")
	public MoveEffect(Screen screen, Unit unit, int cx, int cy, int tx, int ty, Coord<Integer>[] path, float stageSpan) {
		super(screen);

		this.unit = unit;
		this.tx = tx;
		this.ty = ty;
		this.path = path;
		this.stageSpan = stageSpan;

		// どう見ても異常なし
		this.path = (Coord<Integer>[])new Coord<?>[path.length + 1];
		this.path[0] = new Coord<Integer>(cx, cy);
		for(int i = 0; i < path.length; i++)
			this.path[i + 1] = path[i];
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
//		if(now > TIME)
//			finish();

		if(!started || finished)
			return;

		now += delta;
		unit.setRealCoord(calcPos(tx, ty, path, stageSpan));
	}

	@Override
	public void render(GL10 gl) {
		// TODO 自動生成されたメソッド・スタブ

	}

	private void finish() {
		finished = true;
		//		dispose();
	}

	private Vector3 calcPos(int tx, int ty, Coord<Integer>[] path, float stageSpan) {
		int targetIndex = -1;
		for(int i = 0; i < path.length; i++)
			if(path[i].x == tx && path[i].y == ty)
				targetIndex = i;

		assert targetIndex >= 0;

		float timeRatio = now / TIME;
		if(targetIndex == 0 || timeRatio >= 1.0f) {
			finish();
			return Stage.coordToReal(tx, ty, stageSpan);
		}

		float cellSpan = 1.0f / targetIndex;
		float spanRatio = (timeRatio % cellSpan) / cellSpan;

		//		System.out.println("targetIndex = " + targetIndex);
		//		System.out.println("timeRatio = " + timeRatio);
		//		System.out.println("cellSpan = " + cellSpan);
		//		System.out.println("result = " + spanRatio);
		//		System.out.println();

		int cIndex = (int)Math.floor(timeRatio / cellSpan);
		Coord<Integer> cCoord = path[cIndex];
		Coord<Integer> tCoord = path[cIndex + 1];
		Vector3 cCell = Stage.coordToReal(cCoord.x, cCoord.y, stageSpan);
		Vector3 tCell = Stage.coordToReal(tCoord.x, tCoord.y, stageSpan);

		Vector3 cReal = cCell.add(tCell.sub(cCell).mul(spanRatio));

		return cReal;
	}

}
