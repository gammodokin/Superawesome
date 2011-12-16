package com.awesome.script.dynamic;

class WeightUpdate {

	static double F(Agent a, Team g) {
		double f = (3*g.F() + 3*a.A() + 2*g.B() + 2*g.C()) / 10;
		assert 0 <= f && f <= 1 : f;
		return f;
	}

	static final int Rmax = 100;	// maximum reward
	static final int Pmax = 70;		// maximum penalty
	static final float b = 0.3f;	// break-even value

	static int dW(Agent a, Team g) {
		int dw;
		double F = F(a, g);
		if(F < b)
			dw = -(int)Math.floor(Pmax * (b - F)/b);
		else
			dw = (int)Math.floor(Rmax * (F - b)/(1 - b));

		return dw;
	}

}