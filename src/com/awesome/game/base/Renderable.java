package com.awesome.game.base;

import com.awesome.srpg.SRPG;
import com.badlogic.gdx.graphics.GL10;

public abstract class Renderable implements Disposable {

	protected Renderable() {
		SRPG.getRenderer().addRenderable(this);
	}

	public abstract void render(GL10 gl);

	public void dispose() {
		SRPG.getRenderer().removeRenderable(this);
	}

}