package com.awesome.game.base;

public class ChangeTracer <T> {

	private T t0;

	public ChangeTracer(T t) {
		this.t0 = t;
	}

	public boolean isChanged(T t1) {
		return !t0.equals(t1);
	}

}
