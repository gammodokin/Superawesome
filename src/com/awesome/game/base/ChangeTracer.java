package com.awesome.game.base;

public class ChangeTracer <T> {

	private T t0;

	public ChangeTracer(T t) {
		this.t0 = t;
	}

	public boolean isChanged(T t1) {
		T t00 = t0;
		t0 = t1;

		if(t00 == null) {
			if(t1 == null) {
				return false;
			} else {
				return true;
			}
		} else {
			return !t00.equals(t1);
		}
	}

}
