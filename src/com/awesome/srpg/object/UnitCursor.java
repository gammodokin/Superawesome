package com.awesome.srpg.object;

import com.awesome.game.base.Actor;
import com.awesome.game.base.RenderUtil;
import com.awesome.game.base.Screen;
import com.awesome.srpg.SRPG;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class UnitCursor extends Actor {

	private final String TEX_NAME = "stage_cursor.png";

	private final float STAGE_SPAN;
	private final float SIZE;

	private Unit unit;
//	private float rotation = 0;
	private final float ROTATE_SPEED = 300;

	private Texture tex;
	private Decal decal;

	public UnitCursor(Screen screen, float stageSpan) {
		super(screen);

		STAGE_SPAN = stageSpan;
		SIZE = stageSpan;

		tex = new Texture(RenderUtil.loadPixmap(TEX_NAME));

		decal = Decal.newDecal(SIZE, SIZE * 1.2f, new TextureRegion(tex), true);

		SRPG.getRenderer().addDecalToBatch(decal, false);
	}

	@Override
	public void update(float delta) {
//		rotation += ROTATE_SPEED;
//		rotation %= 180;
		if(unit == null)
			return;

		Vector3 rc = unit.getRealCoord();
		decal.setPosition(rc.x, rc.y + STAGE_SPAN * 1.7f, rc.z);
		decal.rotateY(ROTATE_SPEED * delta);
	}

	@Override
	public void render(GL10 gl) {
//		RenderUtil.setupBillboard(decal);
	}

	@Override
	public void dispose() {
		tex.dispose();
		SRPG.getRenderer().removeDecalFromBatch(decal);

		super.dispose();
	}

	public void setUnit(Unit u) {
		this.unit = u;
	}


}
