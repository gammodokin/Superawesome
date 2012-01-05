package com.awesome.srpg.logic;

public class Damage {

	public static final Damage ZERO = new Damage(0, 0);

	private final int hp, mp;
	private UnusualStatus us = UnusualStatus.NONE;

	public Damage(int hp, int mp) {
		this.hp = hp;
		this.mp = mp;
	}

	public Damage(int hp, int mp, UnusualStatus us) {
		this.hp = hp;
		this.mp = mp;
		this.us = us;
	}

	int getHp() { return hp; }

	int getMp() { return mp; }

	UnusualStatus getUnusualStatus() { return us; }

	Damage mul(float f) {
		return new Damage(Math.round(hp * f), Math.round(mp * f), us);
	}

	Damage add(int i) {
		return new Damage(hp == 0 ? 0 : hp + i, mp == 0 ? 0 : mp + i, us);
	}

	@Override
	public String toString() {
		return "Damage : HP " + hp + ", MP " + mp;
	}
}
