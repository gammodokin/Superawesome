package com.awesome.script.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.awesome.script.dynamic.Agent;
import com.awesome.script.dynamic.Rule;
import com.awesome.srpg.SRPG;

public class LearningMacroAction {

	private static final boolean TEIAN = true;
	private static final boolean MUTATION = false;

	private static final int SCRIPT_LEN = 3;
	private static final int MACRO_LEN = 3;

	private Rule[] rule;
	// CEM固有パラメータ
	private final int N = 100;			// 個体数
	private final int M;
	private final double rho = 0.1;	// 選択率
	private final double alpha = 0.7;	// ステップサイズ

	public static final int K = 15;	// 学習するマクロの数
	public static final int T = 500;	// トレーニング一回あたりの戦闘数

	public static final int BATTLE_COUNT = K * T;

	private int[][] w;

	private EpochRecorder recorder;

	public LearningMacroAction(Rule[] rule) {
		this.rule = rule;
		M = rule.length;
		w = new int[N][M];
	}

	public void setRecorder(EpochRecorder r) {
		recorder = r;
	}

	private List<Macro> L = new LinkedList<Macro>();
	private int macroCount = 0;
	private int phase = 0;	// 最初は0
	private ProbV p = null;

	private ProbV po = new ProbV(K/2, 1/(K/2.0));	// TODO とりあえずマクロの確率ベクトルは一様分布で

	private MacroScript[] S;
	private Record[] G;

	public void initLearn() {
//		phase = macroCount / (K / 2);	// 0 = opening, 1 = midgame
		phase = 0;

		if(MUTATION) {
			if(p == null)
				p = initProbabilities(M);
			else {
				ProbV c = p.comp();
				p = initProbabilities(M);
				p.blend(c, alpha);
			}
		} else
			p = initProbabilities(M);

		S = new MacroScript[T];
		G = new Record[T];

//		macroCount++;
		trainingCount = 0;

		if(recorder != null)
			recorder.nextEpoch();
	}

	public void initActivate() {
		for(Rule r : rule)
			r.initActivated();
	}

	private int trainingCount = 0;

	public void eval(MacroScript s, Record record) {
		G[trainingCount] = record;
		s.setF(getFitness(s, record, L));
		S[trainingCount] = s;

		if(recorder != null)
			recorder.addLeanerVic(record.A().ht > 0);

		p = updateProbabilities(p, trainingCount + 1, S);	// SとFは0からiまで

//		System.out.println("F : " + s.getF());

		if(++trainingCount >= T) {
			Macro M = extractMacro(p);
			L.add(M);	// リストに新しいマクロを追加する

			if(recorder != null)
				recorder.setRuleApplicationFreq(ruleFreq(w, this.M, N));

			if(++macroCount < K) {
				initLearn();
			} else {
				SRPG.learnerWinLog += "\n\n---------------\n\n" + L + "\n\n-----------------\n\n";
//				if(SRPG.CONSOLE_VIEW)
//					System.out.println("macros : " + L);
			}
		}

	}

//	void listing1() {
//		List<Macro> L = new LinkedList<Macro>();
//
//		for(int k = 0; k < K; k++) {
//			int phase = k / (K / 2);	// 0 = opening, 1 = midgame
//			ProbV p = initProbabilities();
//
//			Script[] S = new Script[T];
//			BattleRecord[] G = new BattleRecord[T];
////			double[] F = new double[T];
//			for(int i = 0; i < T; i++) {
//				// pに従ってランダムなスクリプトを引く
//				S[i] = generateScript(p, phase);
//				// S[i]を使って戦闘して、ゲームの記録を取る
//				G[i] = evaluateScript(S[i]);
//				// スクリプトの適応度を計算する
//				S[i].setF(getFitness(S[i], G[i], L));
//				p = updateProbabilities(p, i, S);	// SとFは0からiまで
//			}
//			Macro M = extractMacro(p);
//			L.add(M);	// リストに新しいマクロを追加する
//		}
//	}
	private Macro extractMacro(ProbV p) {
		return extractMacro(p, w, M, N, MACRO_LEN);
	}

	private Macro extractMacro(ProbV p, int[][] w, int M, int N, int MACRO_LEN) {
//		double[] v = new double[M];
//		for(int j = 0; j < M; j++)
//			for(int i = 0; i < N; i++)
//				v[j] += w[i][j];
//
//		for(int j = 0; j < M; j++)
//			v[j] /= N;
		double[] v = ruleFreq(w, M, N);

		// vは一戦闘あたりに使われる確率
//		System.out.println("v : " + Arrays.toString(v));

		int[] top = new int[MACRO_LEN];
		for(int rank = 0; rank < MACRO_LEN; rank++) {
			double largest = Integer.MIN_VALUE;
			for(int j = 0; j < M; j++)
				if(v[j] > largest) {
					largest = v[j];
					top[rank] = j;
				}
			v[top[rank]] = -1;	// これ以降選ばれないため
		}

		List<Rule> rules = new ArrayList<Rule>(MACRO_LEN);
		for(int i : top)
			rules.add(rule[i]);

		Macro macro = new Macro(rules.toArray(new Rule[0]));

//		System.out.println("extracted macro : \n" + macro);

		return macro;
	}

	private double[] ruleFreq(int[][] w, int M, int N) {
		double[] v = new double[M];
		for(int j = 0; j < M; j++)
			for(int i = 0; i < N; i++)
				v[j] += w[i][j];

		for(int j = 0; j < M; j++)
			v[j] /= N;

		return v;
	}

	private ProbV updateProbabilities(ProbV p, int n, MacroScript[] scriptList) {
		return updateProbabilities(p, n, scriptList, N, rho);
	}
	private ProbV updateProbabilities(ProbV p, int n, MacroScript[] scriptList, int N, double rho) {
		if(n % N == 0) {	// 全個体の更新
			for(int i = 1; i <= N; i++) {
				for(int j = 0; j < M; j++)
					w[i - 1][j] = scriptList[n - i].firedInPhase(rule[j]) ? 1 : 0;	// 対応するphase中に実行されたかどうか

//				System.out.println(scriptList[n - i]);
			}

//			for(int i = 0; i < N; i++)
//				System.out.println(Arrays.toString(w[i]));

			// 適応度に従って直近N個のサンプルをソートする。ベストなのが最初
			scriptList = sortLast(scriptList, N);
			int Ne = (int)(rho * N);	// エリートサンプルの数
			// エリートサンプル内のルールの頻度を計算する
			ProbV pd = new ProbV(0);	// p' : 新規確率ベクトル

			int[] fired = new int[M];
			ProbV contain = new ProbV(0);

			for(int j = 0; j < M; j++) {
				pd.set(j, 0);
				for(int i = 0; i < Ne; i++) {
					if(scriptList[i].contains(rule[j])) {
						if(TEIAN)
							fired[j] += scriptList[i].firedInPhase(rule[j]) ? 1 : 0;
						pd.set(j, pd.get(j) + 1);
					}
				}
				if(TEIAN)
					contain.set(j, pd.get(j));

				pd.set(j, pd.get(j) / Ne);
			}

			pd.probNor();

//			System.out.println(pd);

			// 確率ベクトルを更新する
			if(TEIAN) {
				for(int j = 0; j < p.length(); j++) {
					// TEIAN アルファ値に実行確率をかけて、実行してなければ変化なし、実行していれば学習
					double c = fired[j] > 0 ? fired[j] / contain.get(j) : 0;
					double alpha = this.alpha * c;
					p.set(j, (1 - alpha)*p.get(j) + alpha*pd.get(j));
				}
			} else
				p.blend(pd, alpha);

			p.probNor();

//			System.out.println("p' : " + pd);
//			System.out.println("p updated : " + p);
		}

		return p;
	}

	private static final Comparator<MacroScript> SCRIPT_COM = new Comparator<MacroScript>(){
		@Override
		public int compare(MacroScript o1, MacroScript o2) {
			return (int)Math.signum(o2.getF() - o1.getF());
		}
	};

	private MacroScript[] sortLast(MacroScript[] scriptList, int n) {
		int last = scriptList.length;
		for(int i = 0; i < scriptList.length; i++)
			if(scriptList[i] == null) {
				last = i;
				break;
			}
		MacroScript[] copy = Arrays.copyOfRange(scriptList, last - n, last);
		Arrays.sort(copy, SCRIPT_COM);
		return copy;
	}


	static final double c = 0.25;
	private double getFitness(MacroScript script, Record gameRecord, List<Macro> l) {
		// A : 学習エージェント、S : Staticエージェント
		Agent A = gameRecord.A();
		Agent Static = gameRecord.S();
		double Fdiv = 0;
		Fdiv = getFdiv(script, l);

		double Fstr = WeightUpdateOneOnOne.Fstr(A, Static);

		return Fstr + c * Fdiv;
	}

	private double getFdiv(MacroScript script, List<Macro> l) {
		double Fdiv = 0;
		for(Macro m : l)
			for(int j = 0; j < M; j++)
				Fdiv += (!m.contains(rule[j]) && script.contains(rule[j]))
						|| (m.contains(rule[j]) && !script.contains(rule[j])) ? 1 : 0;
		Fdiv /= K;

		return Fdiv;
	}

	MacroScript generateScript() {
		return generateScript(p, phase);
	}

	private MacroScript generateScript(ProbV p, int phase) {
		MacroScript script = null;;
		switch(phase) {
		case 0:
			script = new MacroScript(rouletteSelect(p, SCRIPT_LEN), rule, phase);
			break;
		case 1:
			script = new MacroScript(L.get(rouletteSelect(po, 1).get(0)), rouletteSelect(p, SCRIPT_LEN), rule, phase);
			break;
		default:
			assert false;	// ここには来ない
		}

		return script;
	}

	Random ran = new Random();
	private List<Integer> rouletteSelect(ProbV p, int count) {
		List<Integer> sel = new ArrayList<Integer>(count);

		assert p.length() >= count;

		while(sel.size() < count) {
			double fraction = ran.nextDouble();

			double sumw = 0;
			for(int i = 0; i < p.length(); i++) {
				sumw += p.get(i);
				if(sumw > fraction) {
					if(!sel.contains(i)) {
						sel.add(i);
						break;
					}
				}
			}
		}

		Collections.sort(sel);
		Collections.reverse(sel);

		return sel;
	}

	private ProbV initProbabilities(int count) {
		return new ProbV(1.0 / count);	// TODO これでいいのか？
	}

	class ProbV {

		double[] p;

		public ProbV(double d) {
			this(M, d);
		}

		public ProbV(int len, double d) {
			p = new double[len];
			for(int i = 0; i < p.length; i++)
				p[i] = d;
		}

		public ProbV(ProbV pd) {
			p = Arrays.copyOf(pd.p, pd.length());
		}

		public int length() {
			return p.length;
		}

		public double get(int j) {
			return p[j];
		}

		public void set(int j, double d) {
			p[j] = d;
		}

		public void probNor() {
			double sum = 0;
			for(double d : p)
				sum += d;

			for(int i = 0; i < p.length; i++)
				p[i] /= sum;
		}

		public void blend(ProbV pd, double alpha) {
			for(int j = 0; j < p.length; j++)
				set(j, (1 - alpha)*get(j) + alpha*pd.get(j));
		}

		public ProbV comp() {
			ProbV c = new ProbV(p.length, 0);
			for(int i = 0; i < c.length(); i++)
				c.set(i, 1 - p[i]);

			c.probNor();
			return c;
		}

//		public ProbV nor() {
//			double len = len();
//			for(int i = 0; i < p.length; i++)
//				p[i] /= len;
//
//			return this;
//		}
//
//		double len2() {
//			double sum = 0;
//			for(double d : p)
//				sum += d * d;
//			return sum;
//		}
//
//		double len() {
//			return Math.sqrt(len2());
//		}

		@Override
		public String toString() {
			return Arrays.toString(p);
		}

	}

	class Macro {

		private List<Rule> rules;

		Macro(Rule... rules) {
			this.rules = Arrays.asList(rules);
		}

		public boolean contains(Rule r) {
			return rules.contains(r);
		}

		public List<Rule> getRules() {
			return rules;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			for(Rule r : rules)
				sb.append(r);

			return sb.toString();
		}

	}

	class Record {

		private Agent A, S;

		public Record(Agent a, Agent s) {
			super();
			A = a;
			S = s;
		}

		public Agent A() {
			return A;
		}

		public Agent S() {
			return S;
		}

	}

	public int getPhase() {
		return phase;
	}

	public Rule[] getRulebase() {
		return rule;
	}

//	class Rule {
//
//	}

}
