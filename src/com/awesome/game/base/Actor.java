package com.awesome.game.base;


public abstract class Actor extends Renderable {

	protected Screen screen;

	protected Actor(Screen screen) {
		super();
		this.screen = screen;
		screen.addActor(this);
	}

	public abstract void update(float delta);

	public void dispose() {
		screen.removeActor(this);
		super.dispose();
	}

}
