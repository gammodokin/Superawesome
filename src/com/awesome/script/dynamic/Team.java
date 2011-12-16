package com.awesome.script.dynamic;

public class Team {
	final boolean won;	// 勝ったか
	final int N;			// 個体の数
	final Agent[] cs;		// 個体のリスト

	final double F;	// Team fitness function の値
	final double B;	// 全個体の体力の評価
	final double C;	// 全個体に与えたダメージ

	final int notgN;		// 敵チーム個体の数
	final Agent[] notgcs;	// 敵チーム個体のリスト

	public Team(boolean won, int N, Agent[] cs, int notgN, Agent[] notgcs){

		this.won = won;
		this.N = N;
		this.cs = cs;

		this.notgN = notgN;
		this.notgcs = notgcs;

		double f = 0;
		if(won)
			for(Agent c : cs)
				f += 1 + c.htDiv0;
		else
			f = 0;
		f /= 2*N;

		assert 0 <= f && f <= 1 : f; // F(g) in [0, 1]
		F = f;

		double b = 0;
		for(Agent c : cs)
			b += c.ht <= 0 ? 0 : 1 + c.htDiv0;
		b /= 2*N;

		assert 0 <= b && b <= 1 : b;
		B = b;

		double c = 0;
		for(Agent a : notgcs)
			c += a.ht <= 0 ? 1 : 1 - a.htDiv0;
		c /= 2*notgN;

		assert 0 <= c && c <= 1 : c;
		C = c;
	}

	double F() { return F; }
	double B() { return B; }
	double C() { return C; }
}