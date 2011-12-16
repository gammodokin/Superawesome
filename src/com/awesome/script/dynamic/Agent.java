package com.awesome.script.dynamic;

public class Agent {
	public final int ht;			// �Q�[���I�����̗̑�
	public final int h0;			// �Q�[���J�n���̗̑�

	public final double htDiv0;

	public final int D;			// ���񂾎���

	public static final int D_MAX = 100;	// TODO �{����100�ő��v���H

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