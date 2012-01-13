package com.awesome.script.macro;

import com.awesome.script.dynamic.Agent;

class WeightUpdateOneOnOne {

//	static double F(Agent a, Team g) {
//		double f = (3*g.F() + 3*a.A() + 2*g.B() + 2*g.C()) / 10;
//		assert 0 <= f && f <= 1 : f;
//		return f;
//	}

	static double Fstr(Agent A, Agent S) {
		double Fstr;

		if(A.ht > 0)
			Fstr = 0.55 + 0.35 * A.htDiv0;
		else
			Fstr = 0.1 * Math.min((double)A.D / Agent.D_MAX, 1) + 0.1 * (1 - S.htDiv0);

		return Fstr;
	}

	static final int Rmax = 100;	// maximum reward
	static final int Pmax = 20;		// maximum penalty
	static final double b = 0.3;	// break-even value

	static int dW(Agent A, Agent S) {
		int dw;
		double F = Fstr(A, S);
		if(F < b)
			dw = -(int)Math.floor(Pmax * (b - F)/b);
		else
			dw = (int)Math.floor(Rmax * (F - b)/(1 - b));

		return dw;
	}

}
