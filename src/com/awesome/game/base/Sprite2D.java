package com.awesome.game.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Sprite2D extends Disposable {

	public void draw(SpriteBatch spriteBatch);

	public float zDepth();

}
