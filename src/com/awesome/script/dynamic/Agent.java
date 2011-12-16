package com.awesome.script.dynamic;

public class Agent {
	public final int ht;			// ゲーム終了時の体力
	public final int h0;			// ゲーム開始時の体力

	public final double htDiv0;

	public final int D;			// 死んだ時間

	public static final int D_MAX = 100;	// TODO 本当に100で大丈夫か？

	final double A;

	public Agent(int ht, int h0, int D) {
		this.ht = ht;
		this.h0 = h0;
		this.D = D;

		htDiv0 = (double)ht/h0;

		double a = 0;
		if(ht <= 0)
			a = Math.min(D/D_MAX, 1);
		else
			a = 2 + htDiv0;
		a /= 3;

		assert 0 <= a && a <= 1 : a;
		A = a;
	}

	double A() { return A; }
}